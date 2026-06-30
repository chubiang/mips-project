import type { Currency, payStatus } from '@/types/Asset'

export interface Charge {
  id: string
  name: string
  amount: number
  currency: Currency
  status: payStatus
}

export interface ChargeRequest {
  chargeId: string
  paymentId: string
  storeId: string,
  amount: number
  currency: Currency
  email?: string
}

export interface ChargeResponse {
  chargeId: string
  transactionId: string
  paymentId: string
  email: string
  amount: number
  currency: Currency
  status: 'PENDING' | 'PAID' | 'FAILED'
}

export const DEFAULT_CHARGE: Charge = {
  id: "",
  name: "포인트 충전",
  amount: 0,
  currency: "KRW",
  status: "IDLE",
}