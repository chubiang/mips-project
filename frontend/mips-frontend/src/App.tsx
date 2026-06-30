// src/App.tsx
import { BrowserRouter, Routes, Route } from "react-router-dom"
import Login from "@/pages/Login"
import Signup from "./pages/Signup"
import Header from "@/components/Header"
import AboutMe from "@/pages/AboutMe"
import Dashboard from "@/pages/Dashboard"
import UsStock from "@/pages/UsStock"
import AssetMng from "@/pages/AssetMng"
import { AuthProvider } from '@/components/AuthContext'


// ----------------------------------------------------
// 임시 페이지 컴포넌트들 (나중에 src/pages 폴더로 분리할 예정입니다)
// ----------------------------------------------------
function Market() {
  return (
    <div className="bg-white p-8 rounded-xl shadow-sm border border-slate-200">
      <h1 className="text-2xl font-bold text-slate-800 mb-6">📈 시장 동향 (주식/ETF)</h1>
      
      {/* 임시 종목 카드 (내일 API 붙여서 진짜 데이터로 바꿀 곳) */}
      <div className="border p-5 rounded-lg flex justify-between items-center bg-slate-50 max-w-md border-l-4 border-l-blue-600">
        <div>
          <h3 className="font-bold text-lg text-slate-800">AAPL <span className="text-sm font-normal text-slate-500 ml-1">Apple Inc.</span></h3>
          <p className="text-blue-600 font-bold mt-1">$ 150.00</p>
        </div>
        <button className="bg-red-500 hover:bg-red-600 text-white px-5 py-2 rounded shadow transition-colors font-semibold">
          매수하기
        </button>
      </div>
    </div>
  )
}

// ----------------------------------------------------
// 앱 메인 네비게이션 및 라우팅 설정
// ----------------------------------------------------
function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Header />

        {/* 메인 콘텐츠 영역 */}
        <main className="max-w-7xl mx-auto px-4 py-8">
          <Routes>
            <Route path="/about-me" element={<AboutMe />} />
            <Route path="/" element={<Dashboard />} />
            <Route path="/market" element={<Market />} />
            <Route path="/asset-mng" element={<AssetMng/>} />
            <Route path="/us-stock" element={<UsStock />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/login" element={<Login />} />
          </Routes>
        </main>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App