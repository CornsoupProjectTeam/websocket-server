# [최유진] WebSocket Server
**Kafka 기반 실시간 채팅 중계 서버**

이 프로젝트는 사용자의 실시간 채팅 메시지를 WebSocket으로 수신하고,
Kafka를 통해 다른 서비스로 전달한 뒤,
처리된 응답을 다시 사용자에게 실시간으로 전송하는 채팅 중계 서버입니다.

JWT 인증을 통해 접속한 사용자를 확인하고,
사용자별 WebSocket 연결을 관리합니다.

---

## 기술 스택

- Java 21
- Spring Boot
- WebSocket
- Kafka (비동기 메시지 브로커)
