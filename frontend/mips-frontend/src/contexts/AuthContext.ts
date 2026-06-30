import { createContext, useContext } from 'react'
import type { AuthContextType } from '@/types/AuthContextType'

export const AuthContext = createContext<AuthContextType | null>(null)

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext)
  if (!context) throw new Error("useAuth는 AuthProvider 안에서 써야해요!")
  return context
}
