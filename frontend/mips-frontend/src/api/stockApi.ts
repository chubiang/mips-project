import requestApi from '@/api/requestApi';
import type {
  UsTopStock
} from '@/types/Stock'

export async function fetchAllUsStock() {
  const response = await requestApi({
    url: '/api/stock/us/all',
    method: 'GET'
  });
  return response.data;
}