package com.cornsoup.websocket.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatOutputMessage {
    private String memberId;
    private String message;
    private String timestamp;
}