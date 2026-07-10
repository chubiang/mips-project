import type { Currency } from '@/types/Asset'

export interface Charge {
  chargeId: string
  amount: number
  currency: Currency
  status: string
}

export interface ChargeRequest {
  chargeId: string
  paymentId: string
  storeId: string
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
  status: 'IDLE' |'PENDING' | 'PAID' | 'FAILED'
}

export interface PortoneResponse {
  type: string
  message: string

  id: string
  status: string
  storeId: string
  billKey?: string

  paymentId: string
  transactionId: string

  orderName: string
  totalAmount: number

  channel: SelectedChannel
  payMethod: PayMethod
  currency: Currency

  version: string

  pgTxId?: string

  requestedAt: string
  createdAt: string
  updatedAt: string

  statusChangedAt?: string
  paidAt?: string
  cancelAt?: string
  failedAt?: string
}

export interface SelectedChannel {
  type: string
  id?: string
  key?: string
  name?: string
  pgProvider: string
  pgMerchantId: string
}
export interface PayMethod {
  type: 'CARD' | 'BANK' | 'EASY_PAY'
  provider: string
  easyPayMethod?: {
    type:
      | 'KAKAO_PAY'
      | 'NAVER_PAY'
      | 'PAYCO'
      | 'TOSS_PAY'
      | 'CULTURELAND'
      | 'SMARTRO'
      | 'SAMSUNG_PAY'
      | 'LPOINT'
      | 'SSGPAY'
      | 'HANA_MEMBERSHIP'
  }
}

export interface AccountInfo {
  accountId: string
}

export const DEFAULT_CHARGE: Charge = {
  chargeId: '',
  amount: 0,
  currency: 'KRW',
  status: 'IDLE',
}