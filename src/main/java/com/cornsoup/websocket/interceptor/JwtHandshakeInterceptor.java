package com.cornsoup.websocket.interceptor;

import com.cornsoup.websocket.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        try {
            // 1. URI에서 쿼리 파라미터 추출
            String token = UriComponentsBuilder.fromUri(request.getURI())
                    .build()
                    .getQueryParams()
                    .getFirst("token");

            // 2. 토큰 존재 여부 체크
            if (token == null || token.isEmpty()) {
                log.warn("WebSocket connection failed - Token not provided.");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            // 3. 토큰 유효성 검증
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("WebSocket connection failed: Token validation failed.");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            // 4. memberId 추출 및 저장
            String memberId = jwtTokenProvider.extractMemberId(token);
            attributes.put("memberId", memberId);
            log.info("WebSocket connection attempt - memberId: {}", memberId);
            return true;

        } catch (Exception e) {
            log.error("An exception occurred while establishing WebSocket connection: {}", e.getMessage(), e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
