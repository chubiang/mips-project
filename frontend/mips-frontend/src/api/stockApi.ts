import apiClient from '@/api/apiClient'
import type {
  UsTopStock
} from '@/types/Stock'

export async function fetchAllUsStock() {
  const response = await apiClient.get<UsTopStock[]>('/api/stock/us/all')
  return response.data;
}