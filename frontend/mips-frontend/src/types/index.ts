// src/types/index.ts

// 0. 로그인 응답 타입
export interface LoginResponse {
  accessToken: string
}

// 1. 공통 API 응답 타입 (Spring Boot가 내려줄 응답 형태와 맞춥니다)
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

// 2. 주식 및 ETF 정보
export interface Stock {
  ticker: string
  companyName: string
  market: string
  assetType: number // 1:INDEX, 2:STOCK, 3:ETF
  hasComponents: boolean
  status: string
}

// 3. 내 계좌 잔고
export interface AccountBalance {
  availableCash: number // 프론트에서는 BigDecimal 대신 number 사용
  lockedCash: number
}

// 4. 주문 정보 (상태값 통제)
export type OrderType = 'BUY' | 'SELL'
export type OrderStatus = 'PENDING' | 'VALIDATED' | 'REJECTED' | 'EXECUTED' | 'SETTLED'

export interface TradeOrder {
  orderId: string
  ticker: string
  orderType: OrderType
  price: number
  quantity: number
  status: OrderStatus
}