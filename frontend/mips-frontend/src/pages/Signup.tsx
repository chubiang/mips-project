// src/pages/Signup.tsx
import { useState, type FormEvent, type ChangeEvent } from 'react'
import { Link } from 'react-router-dom'
import { TrendingUp } from 'lucide-react'
import { handleSignup } from '@/api/userApi'
import type { SignupForm, SignupFieldError, SignupRequest } from '@/types/SignupForm'

const INITIAL_FORM: SignupForm = {
  email: '',
  phone: '',
  nickname: '',
  password: '',
  passwordConfirm: '',
}

function validate(form: SignupForm): SignupFieldError {
  const errors: SignupFieldError = {}

  if (!form.email) {
    errors.email = '이메일을 입력해주세요.'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = '올바른 이메일 형식이 아닙니다.'
  }

  if (!form.phone) {
    errors.phone = '휴대폰 번호를 입력해주세요.'
  } else if (!/^01[0-9]-?\d{3,4}-?\d{4}$/.test(form.phone.replace(/\s/g, ''))) {
    errors.phone = '올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)'
  }

  if (!form.nickname) {
    errors.nickname = '닉네임을 입력해주세요.'
  } else if (form.nickname.length < 2 || form.nickname.length > 16) {
    errors.nickname = '닉네임은 2~16자 사이로 입력해주세요.'
  }

  if (!form.password) {
    errors.password = '비밀번호를 입력해주세요.'
  } else if (form.password.length < 8) {
    errors.password = '비밀번호는 8자 이상이어야 합니다.'
  }

  if (!form.passwordConfirm) {
    errors.passwordConfirm = '비밀번호 확인을 입력해주세요.'
  } else if (form.password !== form.passwordConfirm) {
    errors.passwordConfirm = '비밀번호가 일치하지 않습니다.'
  }

  return errors
}

export default function Signup() {
  const [form, setForm] = useState<SignupForm>(INITIAL_FORM)
  const [fieldErrors, setFieldErrors] = useState<SignupFieldError>({})
  const [loading, setLoading] = useState<boolean>(false)
  const [serverError, setServerError] = useState<string>('')

  function handleChange(e: ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
    // 입력 시 해당 필드 에러 즉시 제거
    if (fieldErrors[name as keyof SignupFieldError]) {
      setFieldErrors(prev => ({ ...prev, [name]: undefined }))
    }
  }

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setServerError('')

    const errors = validate(form)
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors)
      return
    }

    setLoading(true)
    try {
      const body: SignupRequest = {
        email: form.email,
        phone: form.phone,
        nickname: form.nickname,
        password: form.password,
      }
      await handleSignup(body)
      // TODO: 회원가입 성공 후 처리 (로그인 페이지 이동 등)
    } catch {
      setServerError('회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.')
    } finally {
      setLoading(false)
    }
  }

  const fields: {
    name: keyof SignupForm
    label: string
    type: string
    placeholder: string
    fullWidth?: boolean
  }[] = [
    { name: 'email',           label: '이메일',        type: 'email',    placeholder: 'example@email.com', fullWidth: true },
    { name: 'phone',           label: '휴대폰 번호',   type: 'tel',      placeholder: '010-1234-5678' },
    { name: 'nickname',        label: '닉네임',        type: 'text',     placeholder: '2~16자 이내' },
    { name: 'password',        label: '비밀번호',      type: 'password', placeholder: '8자 이상' },
    { name: 'passwordConfirm', label: '비밀번호 확인', type: 'password', placeholder: '비밀번호를 다시 입력해주세요' },
  ]

  return (
    <div className="min-h-[calc(100vh-64px)] bg-slate-50 flex items-center justify-center p-4 py-10">
      <div className="w-full max-w-sm md:max-w-2xl">
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-8 md:p-10">
          {/* 브랜드 로고 */}
          <div className="flex flex-col items-center mb-8">
            <div className="flex items-center gap-2 text-2xl font-black text-slate-800 mb-2">
              <TrendingUp className="text-blue-500" size={28} />
              MIPS
            </div>
            <p className="text-sm text-slate-500">회원가입</p>
          </div>

          {/* 회원가입 폼 — 모바일: 1열 / md 이상: 2열 */}
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4 md:gap-5" noValidate>
            {fields.map(({ name, label, type, placeholder, fullWidth }) => (
              <div key={name} className={fullWidth ? 'md:col-span-2' : ''}>
                <label className="block text-xs font-medium text-slate-600 mb-1">
                  {label}
                </label>
                <input
                  type={type}
                  name={name}
                  value={form[name]}
                  onChange={handleChange}
                  placeholder={placeholder}
                  className={`w-full px-4 py-2.5 rounded-xl border text-sm text-slate-700 placeholder-slate-400 focus:outline-none focus:ring-2 focus:border-transparent transition-colors ${
                    fieldErrors[name]
                      ? 'border-red-400 focus:ring-red-400'
                      : 'border-slate-300 focus:ring-blue-500'
                  }`}
                />
                {fieldErrors[name] && (
                  <p className="mt-1 text-xs text-red-500 pl-1">{fieldErrors[name]}</p>
                )}
              </div>
            ))}

            {/* 서버 에러 + 버튼은 항상 전체 너비 */}
            <div className="md:col-span-2 space-y-3 pt-1">
              {serverError && (
                <p className="text-xs text-red-500 text-center">{serverError}</p>
              )}
              <button
                type="submit"
                disabled={loading}
                className="w-full py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 active:bg-blue-800 disabled:opacity-50 text-white font-medium text-sm transition-colors shadow-sm"
              >
                {loading ? '처리 중...' : '회원가입'}
              </button>
            </div>
          </form>

          {/* 로그인 이동 */}
          <p className="text-center text-xs text-slate-400 mt-6">
            이미 계정이 있으신가요?{' '}
            <Link to="/login" className="text-blue-500 hover:underline font-medium">
              로그인
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
