import { useEffect, useState } from 'react';
import { setTokenToWorker, fetchViaWorker } from '@/api/apiClient';
// import OAuth2RedirectHandler from "@/components/OAuth2RedirectHandler"; // ★ 로그인 처리 컴포넌트

export default function Dashboard() {
    const [userData, setUserData] = useState(null);

    // 1. 카카오 로그인 리다이렉트 성공 시 처리
    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');
        
        if (token) {
            // 토큰을 워커 메모리에 저장하고, 로그인 상태를 유지하기 위해 필요한 작업을 수행합니다.
            setTokenToWorker(token).then(() => {
                console.log("워커 메모리에 안전하게 토큰 저장 완료");
                // 주소창 토큰은 즉시 지워버림
                window.history.replaceState({}, document.title, "/"); 
            });
        }
    }, []);

    return (
      <div className="bg-white p-8 rounded-xl shadow-sm border border-slate-200">
        {/* <OAuth2RedirectHandler /> {/* ★ 로그인 처리 컴포넌트 삽입 */}
        <h1 className="text-2xl font-bold text-slate-800 mb-2">환영합니다! MIPS 플랫폼입니다.</h1>
        <p className="text-slate-500">결제/정산 및 AI 기반 모의 투자 프론트엔드가 시작되었습니다.</p>
      </div>
    );
}