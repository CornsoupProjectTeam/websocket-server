package com.cornsoup.websocket.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInputMessage {
    private String type; // "chat" 또는 "done"
    private String memberId;
    private String message; // type == "chat"일 때만 사용
    private String timestamp;
}
