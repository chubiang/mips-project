import apiClient from '@/api/apiClient'
import type { ApiRequestOptions, ApiResponse } from '@/types/Comm';

const requestApi = async <T>({
  method,
  url,
  headers,
  withCredentials,
  params,
  data,
}: ApiRequestOptions): Promise<ApiResponse<T>> => {
  const response = 
  await apiClient.request<ApiResponse<T>>({
    method,
    url,
    headers,
    withCredentials,
    params,
    data
  });

  return response.data;
};

export default requestApi;