// src/types/SignupForm.ts

export interface SignupForm {
  email: string
  phone: string
  nickname: string
  password: string
  passwordConfirm: string
}

export interface SignupRequest {
  email: string
  phone: string
  nickname: string
  password: string
}

export interface SignupFieldError {
  email?: string
  phone?: string
  nickname?: string
  password?: string
  passwordConfirm?: string
}
