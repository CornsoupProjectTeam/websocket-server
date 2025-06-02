package com.cornsoup.websocket.kafka;

import com.cornsoup.websocket.kafka.dto.ChatOutputForClient;
import com.cornsoup.websocket.kafka.dto.ChatOutputMessage;
import com.cornsoup.websocket.kafka.dto.ChatOutputWithTypeForReact;
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
                String type = message.getType();

                // type 필드가 chat이면 일반 메시지
                if ("chat".equals(type)) {
                    // Client용 DTO
                    ChatOutputForClient clientMessage = new ChatOutputForClient(
                            type,
                            message.getMessage(),
                            message.getTimestamp()
                    );
                    payload = objectMapper.writeValueAsString(clientMessage);
                } else if ("done".equals(type)) {
                    // React용 DTO
                    ChatOutputWithTypeForReact reactMessage = new ChatOutputWithTypeForReact(
                            type,
                            message.getTimestamp()
                    );
                    payload = objectMapper.writeValueAsString(reactMessage);
                } else {
                    // 로그만 남김
                    log.warn("Unhandled message type received: {}", type);
                    return;
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
