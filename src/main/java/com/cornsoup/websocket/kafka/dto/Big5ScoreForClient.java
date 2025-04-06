package com.cornsoup.websocket.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Big5ScoreForClient {
    private Map<String, Double> scores;
    private String timestamp;
}