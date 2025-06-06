package com.cornsoup.websocket.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatOutputWithTypeForReact {
    private String type;
    private String timestamp;
}