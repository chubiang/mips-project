import { createContext, useState, useEffect, type ReactNode, useContext } from 'react'
import type { AuthContextType } from '@/types/AuthContextType'; // ★ TS 타입 정의 가져오기

// 2. 빈 컨텍스트 생성
const AuthContext = createContext<AuthContextType | null>(null);

// 3. 앱 전체를 감싸줄 Provider 컴포넌트
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);

  // 앱이 처음 켜질 때 로컬 스토리지 확인해서 상태 유지
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) setIsLoggedIn(true);
  }, []);

  // ★ 핵심! 로그인 함수: 토큰 저장 + 리액트 상태 변경을 동시에 쾅!
  const login = (token: string) => {
    localStorage.setItem('accessToken', token);
    setIsLoggedIn(true); // 이 State가 변하면서 네비바가 휙! 바뀝니다.
  };

  // 로그아웃 함수
  const logout = () => {
    localStorage.removeItem('accessToken');
    setIsLoggedIn(false);
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// 4. 컴포넌트에서 편하게 쓰기 위한 커스텀 훅
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth는 AuthProvider 안에서 써야해요!");
  return context;
};