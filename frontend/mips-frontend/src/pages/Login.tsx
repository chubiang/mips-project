import { useState, type FormEvent, type ChangeEvent } from 'react'
import { TrendingUp } from 'lucide-react'
import type { LoginForm, LoginResponse } from '@/types/User'
import { handleGoogleLogin, handleKakaoLogin } from '@/api/userApi'
import apiClient from '@/api/apiClient'
import GoogleIcon from '@/components/login/GoogleIcon'
import KakaoIcon from '@/components/login/KakaoIcon'





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
      await apiClient.post<LoginResponse>('/api/login', { email: formData.email, password: formData.password })
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
