package com.cornsoup.websocket.handler;

import com.cornsoup.websocket.kafka.ChatInputProducer;
import com.cornsoup.websocket.kafka.dto.ChatInputMessage;
import com.cornsoup.websocket.session.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatInputProducer chatInputProducer;
    private final SessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket 연결 완료 - sessionId: {}", session.getId());

        String memberId = (String) session.getAttributes().get("memberId");
        sessionManager.register(memberId, session);
        log.info("세션 등록 완료 - memberId: {}", memberId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("메시지 수신 - sessionId: {}, payload: {}", session.getId(), message.getPayload());

        try {
            ObjectMapper mapper = new ObjectMapper();
            ChatInputMessage input = mapper.readValue(message.getPayload(), ChatInputMessage.class);

            // 토큰 기반 memberId 덮어쓰기
            String memberId = (String) session.getAttributes().get("memberId");
            input.setMemberId(memberId);
            input.setTimestamp(Instant.now().toString());

            chatInputProducer.send(input);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberId = (String) session.getAttributes().get("memberId");
        sessionManager.remove(memberId);
        log.info("WebSocket 연결 종료 - memberId: {}", memberId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("전송 오류 - sessionId: {}, error: {}", session.getId(), exception.getMessage());
    }
}
