package com.cornsoup.websocket.interceptor;

import com.cornsoup.websocket.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

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
            // 1. 쿼리 파라미터에서 토큰 추출
            String uri = request.getURI().toString();
            String token = uri.substring(uri.indexOf("token=") + 6);

            // 2. 토큰 유효성 검증
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("JWT 토큰 검증 실패");
                return false;
            }

            // 3. memberId 추출 및 세션에 저장
            String memberId = jwtTokenProvider.extractMemberId(token);
            attributes.put("memberId", memberId);
            log.info("WebSocket 연결 요청 - memberId: {}", memberId);
            return true;

        } catch (Exception e) {
            log.error("WebSocket 연결 중 예외: {}", e.getMessage());
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
