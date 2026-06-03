import { setTokenToWorker } from '@/api/apiClient'
import type { SignupRequest } from '@/types/SignupForm'

export const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8082/oauth2/authorization/google';
};

export const handleKakaoLogin = () => {
    window.location.href = 'http://localhost:8082/oauth2/authorization/kakao';
};

export const handleLogout = async (): Promise<void> => {
    // 1. 워커의 액세스 토큰을 먼저 비운다 (await 필수 — 완료 전에 페이지 이동하면 토큰이 남아 재로그인됨)
    await setTokenToWorker('')

    // 2. 백엔드에 로그아웃 요청 → HttpOnly 리프레시 토큰 쿠키 삭제
    try {
        await fetch('http://localhost:8082/api/auth/logout', {
            method: 'POST',
            credentials: 'include', // 쿠키 전송 필수
        })
    } catch {
        // 백엔드 요청이 실패해도 프론트 정리는 계속 진행
    }

    // 3. localStorage 잔여 항목 제거
    localStorage.removeItem('refresh_token')

    // 4. 메인 페이지로 이동 (1~3 완료 후)
    window.location.href = '/'
};

export async function handleSignup(data: SignupRequest) {
  const response = await fetch("http://localhost:8082/api/auth/signup", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message ?? "회원가입에 실패했습니다.");
  }

  return response.json();
}