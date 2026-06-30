import { useState, useEffect, useRef, type ReactNode } from 'react'
import { fetchViaWorker, setTokenToWorker } from '@/api/authWorkerClient'
import { AuthContext } from '@/contexts/AuthContext'
import { handleLogout } from '@/api/userApi'
import type { ApiResponse } from '@/types/Comm'


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
          const resdata: ApiResponse<Response> = await response.json()
          console.log("silentRefresh",response, resdata)
          await setTokenToWorker(resdata.data.token)
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
    setIsLoggedIn(false)   // UI 즉시 반영
    void handleLogout()    // 토큰 정리 + 페이지 이동 (async, 완료 후 이동)
  }

  return (
    <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}