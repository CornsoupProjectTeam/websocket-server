package com.cornsoup.websocket.kafka;

import com.cornsoup.websocket.kafka.dto.Big5ScoreMessage;
import com.cornsoup.websocket.session.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class Big5ScoreConsumer {

    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "big5_scores", groupId = "websocket-group")
    public void consume(Big5ScoreMessage message) {
        String memberId = message.getMemberId();
        WebSocketSession session = sessionManager.getSession(memberId);

        if (session != null && session.isOpen()) {
            try {
                String payload = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(payload));
                log.info("Big5 점수 전송 완료 - memberId: {}", memberId);
            } catch (Exception e) {
                log.error("WebSocket 전송 실패 - memberId: {}, error: {}", memberId, e.getMessage(), e);
            }
        } else {
            log.warn("연결된 세션 없음 - memberId: {}", memberId);
        }
    }

}