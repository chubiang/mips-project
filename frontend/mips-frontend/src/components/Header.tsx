// src/components/Header.tsx
import { Link } from 'react-router-dom'
import { LayoutDashboard, TrendingUp, Wallet, LogIn, LogOut } from 'lucide-react'
import { useAuth } from '@/components/AuthContext'

export default function Header() {
  const { isLoggedIn, logout } = useAuth()
  const handleLogout = () => {
    logout() // AuthContext의 로그아웃 함수 호출
  }


  return (
    <nav className="bg-slate-900 text-white shadow-md">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex justify-between h-16 items-center">
          <div className="text-2xl font-black tracking-wider flex items-center gap-2">
            <TrendingUp className="text-blue-400" /> MIPS
          </div>
          <div className="flex space-x-6 font-medium text-slate-300">
            <Link to="/" className="flex items-center gap-1 hover:text-white transition"><LayoutDashboard size={18} /> 홈</Link>
            <Link to="/market" className="flex items-center gap-1 hover:text-white transition"><TrendingUp size={18} /> 시장 동향</Link>
            <Link to="/portfolio" className="flex items-center gap-1 hover:text-white transition"><Wallet size={18} /> 자산 관리</Link>
            <Link to="/us-stock" className="flex items-center gap-1 hover:text-white transition"><TrendingUp size={18} /> 미국 주식</Link>
            {isLoggedIn ? (
              <button onClick={handleLogout} className="flex items-center gap-1 hover:text-white transition">
                <LogOut size={18} /> 로그아웃
              </button>
            ) : (
              <Link to="/login" className="flex items-center gap-1 hover:text-white transition">
                <LogIn size={18} /> 로그인
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  )
}
