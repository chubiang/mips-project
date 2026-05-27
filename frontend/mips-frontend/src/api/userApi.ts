import apiClient from '@/api/apiClient';

// 토큰 주입, 401 에러 처리는 apiClient가 다 알아서 해줍니다.
// 우리는 깔끔하게 비즈니스 로직(URL, HTTP 메서드)만 신경 쓰면 됩니다!
export const fetchUserTest = async () => {
    const response = await apiClient.get('/api/user/test');
    return response.data;
};
export const fetchUserInfo = async () => {
    const response = await apiClient.get('/api/user/info');
    return response.data;
};

export const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8082/oauth2/authorization/google';
};

export const handleKakaoLogin = () => {
    window.location.href = 'http://localhost:8082/oauth2/authorization/kakao';
};

export const handleLogout = () => {
    localStorage.removeItem('accessToken');
    window.location.href = 'http://localhost:8082/login';
};