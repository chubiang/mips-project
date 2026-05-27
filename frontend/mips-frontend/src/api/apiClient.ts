import axios from 'axios';
import type { 
    AxiosInstance, 
    InternalAxiosRequestConfig, 
    AxiosError, 
    AxiosResponse 
} from 'axios';

// 1. 공통 Axios 인스턴스 생성
const apiClient: AxiosInstance = axios.create({
    baseURL: 'http://localhost:8082', // 백엔드 기본 주소
    timeout: 5000, // 5초 이상 응답 없으면 타임아웃
    headers: {
        'Content-Type': 'application/json',
    },
});

// 2. 요청 인터셉터 (Request Interceptor)
// API를 호출하기 직전에 항상 이 로직을 탑니다. 여기서 헤더에 토큰을 심어줍니다.
apiClient.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        console.log('API 요청 시작:', config.method?.toUpperCase(), config.url);
        // 로컬 스토리지나 전역 상태(Zustand 등)에서 저장된 토큰을 가져옵니다.
        const token = localStorage.getItem('accessToken');
        
        // 토큰이 존재하면 Authorization 헤더에 Bearer 타입으로 주입합니다.
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error: AxiosError) => {
        return Promise.reject(error);
    }
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
        if (error.response?.status === 401) {
            console.warn('인증이 만료되었거나 권한이 없습니다.', error.response.data);
            
            // 만료된 토큰을 비워줍니다.
            localStorage.removeItem('accessToken');

            // 강제로 로그인 페이지로 튕겨냅니다.
            window.location.href = '/login'; 
            
            // React Router의 useNavigate를 전역으로 빼서 사용할 수도 있지만, 
            // 가장 확실하고 간편한 방법은 window.location을 사용하는 것입니다.
        }
        
        return Promise.reject(error);
    }
);

export default apiClient;