// src/types/Stock.ts

export type AssetType = 'STOCK' | 'ETF' | 'INDEX'

export interface UsTopStock {
  ticker: string
  companyName: string
  assetType: AssetType
  sector: string
  status: string       // "true" | "false"
  price: number
  changeAmount: number // 전일 대비 변화 금액
  changeRate: number   // 전일 대비 변화율 (%)
  highPrice: number    // 당일 고가
  lowPrice: number     // 당일 저가
  openPrice: number    // 시가
  prevClose: number    // 전일 종가
  updatedAt: string    // ISO 8601
}

export type SortField = 'ticker' | 'companyName' | 'price' | 'changeAmount' | 'changeRate'
export type SortOrder = 'asc' | 'desc'

export interface StockSort {
  field: SortField
  order: SortOrder
}

export interface StockFilter {
  search: string
  assetType: AssetType | 'ALL'
}
