import apiClient from '@/api/apiClient'
import type { ApiRequestOptions } from '@/types/Comm';

const requestApi = <T>({
  method,
  url,
  headers,
  withCredentials,
  params,
  data,
}: ApiRequestOptions) => {
  return apiClient.request<T>({
    method,
    url,
    headers,
    withCredentials,
    params,
    data,
  });
};

export default requestApi;