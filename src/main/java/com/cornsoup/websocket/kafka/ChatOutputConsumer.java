package com.cornsoup.websocket.kafka;

import com.cornsoup.websocket.kafka.dto.ChatOutputForClient;
import com.cornsoup.websocket.kafka.dto.ChatOutputMessage;
import com.cornsoup.websocket.kafka.dto.ChatOutputWithTypeForClient;
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
                String payload;

                // type 필드가 null 이거나 비어있으면 일반 메시지
                if (message.getType() == null || message.getType().isEmpty()) {
                    // 일반 메시지용 DTO 변환
                    ChatOutputForClient clientMessage = new ChatOutputForClient(
                            message.getMessage(),
                            message.getTimestamp()
                    );
                    payload = objectMapper.writeValueAsString(clientMessage);
                } else {
                    // type이 있는 메시지용 DTO 변환
                    ChatOutputWithTypeForClient clientMessage = new ChatOutputWithTypeForClient(
                            message.getType(),
                            message.getMessage(),
                            message.getTimestamp()
                    );
                    payload = objectMapper.writeValueAsString(clientMessage);
                }

                session.sendMessage(new TextMessage(payload));

                log.info("Message sent to client - memberId: {}", memberId);
            } catch (Exception e) {
                log.error("WebSocket message sending failed - memberId: {}, error: {}", memberId, e.getMessage(), e);
            }
        } else {
            log.warn("No active session found - memberId: {}", memberId);
        }
    }

}
