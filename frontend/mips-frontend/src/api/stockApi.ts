import requestApi from '@/api/requestApi';
import type {
  RealtimeStockQuote
} from '@/types/Stock'
import { API_BASE_URL } from '@/api/comm'

export async function fetchAllUsStock() {
  const response = await requestApi({
    url: '/api/stock/us/all',
    method: 'GET'
  });
  return response.data;
}

export function getRealtimeStockStreamUrl() {
  return `${API_BASE_URL}/api/stock/us/realtime/stream`;
}

export async function fetchRealtimeUsStocks(): Promise<RealtimeStockQuote[]> {
  const response = await requestApi<RealtimeStockQuote[]>({
    url: '/api/stock/us/realtime',
    method: 'GET'
  });

  // 실행 중인 백엔드가 배열을 직접 반환하는 구버전이어도 화면이 깨지지 않게 한다.
  if (Array.isArray(response)) {
    return response as RealtimeStockQuote[];
  }

  if (Array.isArray(response.data)) {
    return response.data;
  }

  throw new Error('실시간 시세 API 응답 형식이 올바르지 않습니다.');
}
