export type payStatus = 'IDLE' |'PENDING' | 'PAID' | 'FAILED'
export type Currency = 'KRW' | 'USD' | 'EUR' | 'JPY' | 'CNY'
export type StockMarket = "KR" | "US"

export interface paymentStatus {
  status: payStatus,
  message?: string
}


export interface payment {
  paymentId: string,
  message: string,
  code?: number
}


export interface HoldingStock {
  ticker: string
  name: string
  quantity: number
  avgPrice: number
  currentPrice: number
  market: StockMarket
}
