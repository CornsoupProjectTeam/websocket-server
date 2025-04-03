package com.cornsoup.websocket.session;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(String memberId, WebSocketSession session) {
        sessions.put(memberId, session);
    }

    public WebSocketSession getSession(String memberId) {
        return sessions.get(memberId);
    }

    public void remove(String memberId) {
        sessions.remove(memberId);
    }

    public boolean exists(String memberId) {
        return sessions.containsKey(memberId);
    }
}
