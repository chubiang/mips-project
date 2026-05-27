# 🚀 MIPS (인증 및 주식/자산 관리 플랫폼)

> **프로젝트 상태:** 🛠️ 개발 진행 중 (WIP - Work In Progress)  
> 본 프로젝트는 현재 풀스택 아키텍처 및 보안 표준을 학습하고 적용하기 위해 고도화 중인 미완성 프로젝트입니다. 비록 완성이 끝나지 않았으나, 컴포넌트 설계, 데이터 흐름, 그리고 보안 핵심 로직의 구현 과정을 투명하게 공개하여 기술적 역량을 검증받고자 합니다.

---

## 📌 프로젝트 개요
MIPS는 안정적인 사용자 인증을 기반으로 미국 주식, ETF 정보 조회 및 거래, 자산 관리를 제공하는 모던 풀스택 웹 애플리케이션입니다. 프론트엔드와 백엔드의 엄격한 역할 분리를 지향하며, 확장성과 보안성을 최우선으로 설계하고 있습니다.

## 🛠️ 기술 스택 (Tech Stack)

### Frontend
- **Core:** React, TypeScript, Vite
- **State Management:** React Context API / 상태 관리 라이브러리
- **Build Tool / Linter:** Vite, ESLint

### Backend
- **Core:** Java, Spring Boot
- **Build Tool:** Gradle
- **Database / ORM:** JPA (Hibernate)

---

## 🔑 주요 아키텍처 및 보안 설계 포인트

### 1. 보안 중심의 인증 시스템 (구현 중)
- **JWT (JSON Web Token) 기반 인증:** 사용자 세션 관리를 위해 Access Token과 Refresh Token 구조를 설계하고 있으며, 프론트엔드 내에서 안전한 상태 관리를 통해 토큰 유출을 방지합니다.
- **OAuth2 소셜 로그인 연동:** 확장성 있는 회원 관리를 위해 OAuth2 리다이렉션 핸들러를 구축하고 있습니다.

### 2. 도메인 중심의 데이터 모델링 (Domain-Driven Design)
- **자산 및 거래 시스템:** 유기적인 데이터 흐름을 위해 `User`, `AccountBalance`, `Stock`, `TradeOrder`, `EtfComponent` 등의 엔티티를 도메인별로 분리하고 상호작용하도록 유연하게 설계했습니다.

---

## 📊 현재 구현 상황 및 로드맵 (Project Status)

### [프론트엔드]
- [x] Vite + TypeScript + React 프로젝트 초기 아키텍처 환경 세팅
- [x] UI 컴포넌트(Header, 기본적인 페이지 라우팅 및 레이아웃) 구조화
- [x] 로그인(Login) 및 주식 정보 조회(UsStock) 페이지 마크업 및 타입 정의
- [ ] JWT 및 OAuth2 기반 로그인 상태 연동 및 네비게이션 바 동적 렌더링 (Google, Kakao)
- [ ] 포트원(Portone) SDK 연동을 통한 가상결제 및 예수금 충전 UI 구현 
- [ ] 주식 상세 페이지 내 실시간 호가 반영 및 매수/매도 주문 유효성 검사 기능 

### [백엔드]
- [x] Spring Boot + Gradle 멀티 모듈 프로젝트 구조 설계
- [x] 핵심 도메인 엔티티(AccountBalance, TradeOrder, Stock, User) 모델링 및 JPA 매핑
- [x] 인증/인가를 위한 Spring Security 및 JWT 발급/검증 로직 구현
- [x] 로컬 n8n을 이용한 Finnhub 사의 미국 주식 API 조회 
- [ ] 가상 결제 완료 정보 수신 및 계좌 잔액(AccountBalance) 갱신 API 개발
- [ ] 동시성 이슈를 고려한 주식 주문(TradeOrder) 체결 및 트랜잭션 처리 로직 구현

---

## 📂 폴더 구조 (Directory Structure)

```text
my-portfolio-project/
├── frontend/mips-frontend/   # React + TypeScript 프론트엔드 소스코드
└── backend/MIPS/             # Spring Boot + Gradle 백엔드 소스코드
