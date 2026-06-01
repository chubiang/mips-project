import { setTokenToWorker } from '@/api/apiClient';

export const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8082/oauth2/authorization/google';
};

export const handleKakaoLogin = () => {
    window.location.href = 'http://localhost:8082/oauth2/authorization/kakao';
};

export const handleLogout = () => {
    localStorage.removeItem('accessToken');
    setTokenToWorker(''); // 워커에도 토큰 제거 명령 전달
    window.location.href = '/'; // 로그아웃 후 메인페이지로 이동
};