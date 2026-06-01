import { createContext, useState, useEffect, useRef, type ReactNode, useContext } from 'react'
import type { AuthContextType } from '@/types/AuthContextType' // ★ TS 타입 정의 가져오기
import { fetchViaWorker, setTokenToWorker } from '@/api/apiClient'
import { handleLogout } from '@/api/userApi' // ★ API 함수 가져오기

// 2. 빈 컨텍스트 생성
const AuthContext = createContext<AuthContextType | null>(null)

// 3. 앱 전체를 감싸줄 Provider 컴포넌트
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false)
  // API 호출 여부를 추적하는 flag
  const isRequestSent = useRef(false);

  // 앱이 처음 켜질 때 로컬 스토리지 확인해서 상태 유지
  useEffect(() => {
    if (isRequestSent.current) return; // 이미 API 요청을 보냈다면 중복 방지

    isRequestSent.current = true
    // 1. 토큰이 있는지 확인해서 로그인 상태 초기화
    fetchViaWorker('http://localhost:8082/api/user/pass')
      .then((data) => {
        console.log("로그인 상태 확인 성공:", data)
        setIsLoggedIn(true)
      })
      .catch((error) => {
        // 401(만료)이거나 토큰이 없어서 에러가 났다면 로그인 안 된 상태로 간주
        console.log("로그인 안 된 상태거나 토큰 만료됨:", error)
        silentRefresh()
        isRequestSent.current = false
      })

    // 2. 페이지 새로고침 시 토큰이 만료되었을 때 자동으로 새로고침 방어하기
    const silentRefresh = async () => {
        try {
          // 🌟 credentials: 'include'가 있어야 브라우저가 몰래 HttpOnly 쿠키를 같이 보내줍니다!
          const response = await fetch('http://localhost:8082/api/auth/refresh', {
              method: 'POST',
              credentials: 'include', 
          })

          if (response.ok) {
            const data = await response.json()
            // 백엔드가 준 새 토큰을 워커 메모리에 다시 주입!
            await setTokenToWorker(data.accessToken) 
            setIsLoggedIn(true)
            console.log("새로고침 방어 성공! 토큰 복구됨")
          } else {
            // 쿠키가 없거나 만료되었을 때는 401 에러가 뜹니다. 이 경우 로그인 상태를 초기화합니다.
            console.log("새로고침 방어 실패: 쿠키 없거나 만료됨")
            setIsLoggedIn(false)
          }
        } catch (error) {
          console.log("새로고침 방어 중 에러 발생:", error)
          setIsLoggedIn(false)
        }
    }
    
  }, [])
  

  // ★ 핵심! 로그인 함수: 토큰 저장 + 리액트 상태 변경을 동시에 쾅!
  const login = (token: string) => {
    console.log('AuthProvider: 로그인 처리 중, 받은 토큰 ->', token) // 디버깅용 로그
    // localStorage.setItem('accessToken', token)
    setIsLoggedIn(true) // 이 State가 변하면서 네비바가 휙! 바뀝니다.
  }

  // 로그아웃 함수
  const logout = () => {
    console.log('AuthProvider: 로그아웃 처리 중') // 디버깅용 로그
    handleLogout() // API 함수로 로그아웃 처리
    setIsLoggedIn(false)
  }

  return (
    <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// 4. 컴포넌트에서 편하게 쓰기 위한 커스텀 훅
export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) throw new Error("useAuth는 AuthProvider 안에서 써야해요!")
  return context
}