package com.cornsoup.websocket.kafka;

import com.cornsoup.websocket.kafka.dto.ChatInputMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatInputProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(ChatInputMessage message) {
        kafkaTemplate.send("chat_input", message.getMemberId(), message);
        log.info("Kafka message published successfully - type: {}, memberId: {}, message: {}",
                message.getType(), message.getMemberId(), message.getMessage());
    }
}
