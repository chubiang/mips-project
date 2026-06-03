import { useState, type FormEvent, type ChangeEvent } from 'react'
import { TrendingUp } from 'lucide-react'
import type { LoginForm } from '@/types/LoginForm'
import type { LoginResponse } from '@/types'
import { handleGoogleLogin, handleKakaoLogin } from '@/api/userApi'
import axios from 'axios'

function GoogleIcon() {
  return (
    <svg className="w-5 h-5 shrink-0" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
      <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z" />
      <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z" />
      <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z" />
      <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z" />
    </svg>
  )
}

function KakaoIcon() {
  return (
    <svg className="w-5 h-5 shrink-0" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
      <path
        fill="#191919"
        d="M12 3C6.477 3 2 6.477 2 10.8c0 2.7 1.55 5.08 3.9 6.54l-.99 3.68a.3.3 0 0 0 .46.33l4.31-2.85c.76.1 1.54.15 2.32.15 5.523 0 10-3.477 10-7.8S17.523 3 12 3z"
      />
    </svg>
  )
}

export default function Login() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [formData, setFormData] = useState<LoginForm>({
    email: '',
    password: '',
  })

  async function handleLogin(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (!formData.email || !formData.password) {
      setError('이메일과 비밀번호를 모두 입력해주세요.')
      return
    }
    setError('')
    setLoading(true)
    try {
      await axios.post<LoginResponse>('/api/login', { email: formData.email, password: formData.password })
      // TODO: 로그인 성공 후 처리 (토큰 저장, 리다이렉션 등)
    } catch {
      setError('이메일 또는 비밀번호가 올바르지 않습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-[calc(100vh-64px)] bg-slate-50 flex items-center justify-center p-4">
      <div className="w-full max-w-sm">
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
          {/* 브랜드 로고 */}
          <div className="flex flex-col items-center mb-7">
            <div className="flex items-center gap-2 text-2xl font-black text-slate-800 mb-2">
              <TrendingUp className="text-blue-500" size={28} />
              MIPS
            </div>
            <p className="text-sm text-slate-500">로그인하여 계속하세요</p>
          </div>

          {/* 소셜 로그인 */}
          <button
            onClick={() => { handleGoogleLogin() }}
            className="w-full flex items-center justify-center gap-3 px-4 py-3 mb-3 rounded-xl border border-slate-300 bg-white hover:bg-slate-50 active:bg-slate-100 text-slate-700 font-medium text-sm transition-colors shadow-sm"
          >
            <GoogleIcon />
            Google로 로그인
          </button>

          <button
            onClick={() => { handleKakaoLogin() }}
            className="w-full flex items-center justify-center gap-3 px-4 py-3 rounded-xl font-medium text-sm transition-opacity hover:opacity-90 active:opacity-80"
            style={{ backgroundColor: '#FEE500', color: '#191919' }}
          >
            <KakaoIcon />
            카카오로 로그인
          </button>

          {/* 구분선 */}
          <div className="flex items-center gap-3 my-6">
            <div className="flex-1 h-px bg-slate-200" />
            <span className="text-xs text-slate-400 shrink-0">또는</span>
            <div className="flex-1 h-px bg-slate-200" />
          </div>

          {/* MIPS 로그인 폼 */}
          <form onSubmit={handleLogin} className="space-y-3">
            <input
              type="text"
              placeholder="이메일"
              value={formData.email}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setFormData({ ...formData, email: e.target.value })}
              className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm text-slate-700 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <input
              type="password"
              placeholder="비밀번호"
              value={formData.password}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setFormData({ ...formData, password: e.target.value })}
              className="w-full px-4 py-2.5 rounded-xl border border-slate-300 text-sm text-slate-700 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />

            {error && (
              <p className="text-xs text-red-500 pl-1">{error}</p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 active:bg-blue-800 disabled:opacity-50 text-white font-medium text-sm transition-colors shadow-sm"
            >
              {loading ? '로그인 중...' : '로그인'}
            </button>
          </form>
          <br />
          {/* 약관 안내 */}
          <p className="text-center text-xs text-slate-400 mt-6 leading-relaxed">
            로그인 시{' '}
            <span className="underline cursor-pointer hover:text-slate-600">이용약관</span>
            {' '}및{' '}
            <span className="underline cursor-pointer hover:text-slate-600">개인정보처리방침</span>
            에 동의하는 것으로 간주합니다.
          </p>
        </div>
      </div>
    </div>
  )
}
