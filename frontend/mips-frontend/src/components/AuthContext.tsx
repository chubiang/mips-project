import { createContext, useState, useEffect, useRef, type ReactNode, useContext } from 'react'
import type { AuthContextType } from '@/types/AuthContextType'
import { fetchViaWorker, setTokenToWorker } from '@/api/apiClient'
import { handleLogout } from '@/api/userApi'

interface RefreshTokenResponse {
  accessToken: string
}

const AuthContext = createContext<AuthContextType | null>(null)

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false)
  const isRequestSent = useRef(false)

  useEffect(() => {
    if (isRequestSent.current) return

    isRequestSent.current = true

    // silentRefresh를 fetchViaWorker 호출 전에 선언 (TS2448 방지)
    const silentRefresh = async (): Promise<void> => {
      try {
        const response = await fetch('http://localhost:8082/api/auth/refresh', {
          method: 'POST',
          credentials: 'include',
        })

        if (response.ok) {
          const data: RefreshTokenResponse = await response.json()
          await setTokenToWorker(data.accessToken)
          setIsLoggedIn(true)
          console.log("새로고침 방어 성공! 토큰 복구됨")
        } else {
          console.log("새로고침 방어 실패: 쿠키 없거나 만료됨")
          setIsLoggedIn(false)
        }
      } catch (error) {
        console.log("새로고침 방어 중 에러 발생:", error)
        setIsLoggedIn(false)
      }
    }

    fetchViaWorker('http://localhost:8082/api/user/pass')
      .then((data) => {
        console.log("로그인 상태 확인 성공:", data)
        setIsLoggedIn(true)
      })
      .catch((error) => {
        console.log("로그인 안 된 상태거나 토큰 만료됨:", error)
        silentRefresh()
        isRequestSent.current = false
      })
  }, [])

  const login = (token: string): void => {
    console.log('AuthProvider: 로그인 처리 중, 받은 토큰 ->', token)
    setIsLoggedIn(true)
  }

  const logout = (): void => {
    console.log('AuthProvider: 로그아웃 처리 중')
    handleLogout()
    setIsLoggedIn(false)
  }

  return (
    <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext)
  if (!context) throw new Error("useAuth는 AuthProvider 안에서 써야해요!")
  return context
}
