package com.cornsoup.websocket.kafka;

import com.cornsoup.websocket.kafka.dto.ChatOutputForClient;
import com.cornsoup.websocket.kafka.dto.ChatOutputMessage;
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
public class ChatOutputConsumer {

    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "chat_output",
            groupId = "websocket-group",
            properties = {
                    "spring.json.value.default.type=com.cornsoup.websocket.kafka.dto.ChatOutputMessage",
                    "spring.json.trusted.packages=com.cornsoup.websocket.kafka.dto"
            }
    )
    public void consume(ChatOutputMessage message) {
        String memberId = message.getMemberId();
        WebSocketSession session = sessionManager.getSession(memberId);

        if (session != null && session.isOpen()) {
            try {
                // 클라이언트용 DTO로 변환
                ChatOutputForClient clientMessage = new ChatOutputForClient(
                        message.getMessage(),
                        message.getTimestamp()
                );

                String payload = objectMapper.writeValueAsString(clientMessage);
                session.sendMessage(new TextMessage(payload));

                log.info("클라이언트로 메시지 전송 - memberId: {}", memberId);
            } catch (Exception e) {
                log.error("WebSocket 전송 실패 - memberId: {}, error: {}", memberId, e.getMessage(), e);
            }
        } else {
            log.warn("연결된 세션 없음 - memberId: {}", memberId);
        }
    }

}
