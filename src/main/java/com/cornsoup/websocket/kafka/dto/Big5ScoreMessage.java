package com.cornsoup.websocket.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Big5ScoreMessage {
    private String memberId;
    private Map<String, Double> scores;
    private String timestamp;
}