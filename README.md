# 🚀 MIPS
### 인증 기반 미국 주식·ETF 및 자산 관리 플랫폼

MIPS는 금융권 시스템 개발 경험을 바탕으로 인증, 자산 관리, 주식 주문 흐름을 현대적인 웹 아키텍처로 재구성하는 개인 프로젝트입니다.

React와 Spring Boot를 기반으로 프론트엔드와 백엔드를 분리하고, JWT·OAuth2 인증, 자산 및 주문 도메인 모델링, 외부 주식 API 연동을 구현하고 있습니다.

현재 핵심 인증 구조와 주요 도메인 엔티티 설계를 완료했으며, 결제·예수금·주문 체결·동시성 처리 기능을 단계적으로 확장하고 있습니다.

---

## 주요 기능

### 구현 완료
- Google·Kakao OAuth2 로그인 연동
- Spring Security 기반 JWT 발급·검증
- Access Token과 Refresh Token 분리 관리
- 미국 주식 정보 조회(n8n을 이용)
- 사용자·계좌잔액·주식·주문 도메인 모델링
- Spring Boot 멀티 모듈 구조 구성
- React 기반 기본 화면 및 라우팅 구성
- PortOne 카카오페이 간편결제

### 구현 예정
- 일반 회원가입 및 로그인 API
- 주식 매수·매도 주문 처리
- 주문 체결 시 동시성 제어
- 거래 내역 및 자산 현황 조회

---

## 기술 스택

### Frontend
- React
- TypeScript
- Vite
- React Context API
- ESLint

### Backend
- Java
- Spring Boot
- Spring Security
- JPA
- Hibernate
- Gradle

### External API
- Finnhub
- PortOne
- Google OAuth2
- Kakao OAuth2

---

## 인증 설계

- Access Token은 Web Worker 메모리에 보관
- Refresh Token은 HttpOnly 쿠키에 저장
- 토큰 저장 위치를 분리해 인증 정보 노출 위험을 줄이도록 설계
- OAuth2 로그인 이후 프론트엔드 리다이렉션 처리 구현
- Spring Security 필터를 통한 JWT 검증

---

## 도메인 모델
- User : 사용자
- Account : 계좌
- Charg : 충전
- Comm : 공통
- Stock : 주식
- Order : 주문
- Payment : 결제

주식, 자산, 주문 도메인을 분리하고 각 엔티티 간 책임과 관계를 정의했습니다.

---

## 프로젝트 구조

```text
my-portfolio-project/
├── frontend/
│   └── mips-frontend/
└── backend/
    └── MIPS/