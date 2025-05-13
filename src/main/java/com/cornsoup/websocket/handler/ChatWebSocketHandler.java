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
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatInputProducer chatInputProducer;
    private final SessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connection established - sessionId: {}", session.getId());

        String memberId = (String) session.getAttributes().get("memberId");
        sessionManager.register(memberId, session);
        log.info("Session successfully registered - memberId: {}", memberId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Message received - sessionId: {}, payload: {}", session.getId(), message.getPayload());
        CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ChatInputMessage input = mapper.readValue(message.getPayload(), ChatInputMessage.class);

                // 토큰 기반 memberId 덮어쓰기
                String memberId = (String) session.getAttributes().get("memberId");
                input.setMemberId(memberId);
                input.setTimestamp(Instant.now().toString());

                chatInputProducer.send(input);
            } catch (Exception e) {
                log.error("An error occurred while processing the message", e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberId = (String) session.getAttributes().get("memberId");
        sessionManager.remove(memberId);
        log.info("WebSocket connection closed - memberId: {}", memberId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Message sending error - sessionId: {}, error: {}", session.getId(), exception.getMessage());
    }
}
