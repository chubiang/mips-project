import axios from 'axios';
import type { 
    AxiosInstance, 
    // InternalAxiosRequestConfig, 
    AxiosError, 
    AxiosResponse 
} from 'axios';
import { getTokenFromWorker } from '@/api/authWorkerClient'
import { API_BASE_URL } from '@/api/comm'

// 1. 공통 Axios 인스턴스 생성
const apiClient: AxiosInstance = axios.create({
    baseURL: API_BASE_URL,
    timeout: 5000, // 5초 이상 응답 없으면 타임아웃
    headers: {
        'Content-Type': 'application/json',
    },
});

// 모든 토큰 주입과 401 에러 처리를 워커가 담당하기 때문에, React에서는 인터셉터를 제거하거나 최소한의 로깅 용도로만 사용할 수 있습니다.
// 2. 요청 인터셉터 (Request Interceptor)
// API를 호출하기 직전에 항상 이 로직을 탑니다. 여기서 헤더에 토큰을 심어줍니다.
apiClient.interceptors.request.use(
    async (config) => {
//         console.log('API 요청 시작:', config.method?.toUpperCase(), config.url);
        const token = await getTokenFromWorker();
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error: AxiosError) => Promise.reject(error)
);

// 3. 응답 인터셉터 (Response Interceptor)
// 백엔드에서 응답이 돌아왔을 때, 컴포넌트로 전달되기 전에 여기서 먼저 낚아챕니다.
apiClient.interceptors.response.use(
    (response: AxiosResponse) => {
        // HTTP 상태 코드가 2xx 대일 경우 정상 통과
        console.log('API 응답 성공:', response);
        return response;
    },
    (error: AxiosError) => {
        // 401 Unauthorized 에러가 발생한 경우 (토큰 만료, 미인증 등)
        const requestUrl = error.config?.url ?? '';
        const isAuthBootstrapRequest = requestUrl.includes('/api/user/pass')
            || requestUrl.includes('/api/auth/refresh');

        if (error.response?.status === 401
            && !isAuthBootstrapRequest
            && window.location.pathname !== '/login') {
            console.warn('권한이 없습니다.', error.response.data);

            // 강제로 로그인 페이지로 튕겨냅니다.
            window.location.href = '/login'; 
        }
        
        return Promise.reject(error);
    }
);

export default apiClient;
