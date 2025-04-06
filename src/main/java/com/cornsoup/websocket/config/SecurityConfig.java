package com.cornsoup.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // WebSocket은 CSRF 보통 disable
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/ws/**").permitAll()  // WebSocket 엔드포인트 허용
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())  // HTTP Basic 방식(ID/PW)으로 로그인하지 않음
                .formLogin(form -> form.disable()); // formLogin 끄기

        return http.build();
    }
}
