// src/types/User.ts

export interface SignupRequest {
  email: string
  phone: string
  nickname: string
  password: string
}

// 0. 로그인 응답 타입
export interface LoginResponse {
  accessToken: string
}

export interface SignupForm {
  email: string
  phone: string
  nickname: string
  password: string
  passwordConfirm: string
}

export interface LoginForm {
  email: string,
  password: string,
}

export interface UserInfo {
  email: string
  phone?: string
  nickname?: string
  role: string
}

export interface SignupFieldError {
  email?: string
  phone?: string
  nickname?: string
  password?: string
  passwordConfirm?: string
}