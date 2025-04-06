package com.cornsoup.websocket.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtTokenProvider {

    private Key key;

    private static final long EXPIRATION_MS = 1000 * 60 * 60; // 1시간

    @PostConstruct
    public void init() {
        // 환경변수에서 SECRET_KEY 가져오기
        String secretKeyPlain = System.getenv("SECRET_KEY");

        if (secretKeyPlain == null || secretKeyPlain.length() < 32) {
            throw new IllegalArgumentException("환경변수 SECRET_KEY는 반드시 존재하고, 32자 이상이어야 합니다.");
        }

        this.key = Keys.hmacShaKeyFor(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
    }

    public String extractMemberId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

