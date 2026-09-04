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
- 미국 주식 정보 조회(Docker를 통해 로컬 n8n을 이용) = [2026.09.04 / 스케줄러로 변경, 비용문제로 n8n으론 로컬만 가능해서]
- 사용자·계좌잔액·주식·주문 도메인 모델링 = [2026.09.02 / 주식 데이터 모델링 리펙토링]
- Spring Boot 멀티 모듈 구조 구성
- React 기반 기본 화면 및 라우팅 구성
- PortOne 카카오페이 간편결제(테스트용 서버)
- Docker 로컬에서 bootJar,Postgres 컨테이너 실행 테스트 완료

### 구현 예정
- 일반 회원가입 및 로그인 API
- 주식 매수·매도 주문 처리
- 주문 체결 시 동시성 제어
- 거래 내역 및 자산 현황 조회
- 원화 달러 환율 조회 추가 (API 테스트는 완료)
---

## 기술 스택

### Frontend
- React
- TypeScript
- Vite
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

## 도메인 구성

- `User` : 사용자 정보 및 인증 연계
- `Account` : 사용자 예수금 계좌 및 잔액 관리
- `Charge` : 예수금 충전 요청 및 충전 이력
- `Comm` : 공통 코드와 공통 기능
- `Stock` : 미국 주식 및 ETF 정보
- `Order` : 주식 매수·매도 주문
- `Payment` : 외부 결제 요청 및 결제 결과 관리

사용자, 계좌, 충전, 결제, 주식, 주문 영역을 분리해 각 도메인의 책임과 관계를 정의했습니다.

사용자, 계좌, 충전, 결제, 주식, 주문 영역을 분리해 각 도메인의 책임과 관계를 정의했습니다.

주식, 자산, 주문 도메인을 분리하고 각 엔티티 간 책임과 관계를 정의했습니다.

---

## 프로젝트 구조

```text
my-portfolio-project/
├── frontend/
│   └── mips-frontend/
└── backend/
    └── MIPS/
