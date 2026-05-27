import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from './AuthContext'; // ★ 컨트롤 타워 가져오기

const OAuth2RedirectHandler = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { login } = useAuth(); // ★ login 함수 쏙 빼오기

  useEffect(() => {
    const token = searchParams.get('token'); // 주소창에서 '?token=' 뒤에 값 줍기

    if (token) {
      // 로컬 스토리지에 넣고, 리액트 State도 변경해라!!
      login(token); 
      
      // 다 했으면 깔끔하게 메인 화면으로 이동! (replace: true는 뒤로가기 방지)
      navigate('/', { replace: true }); 
    } 
    // else {
    //   alert('로그인에 실패했습니다.');
    //   navigate('/login');
    // }
  }, [searchParams, navigate, login]);
  return null; // 이 컴포넌트는 화면에 아무것도 안 보여주니까 null 반환
};

export default OAuth2RedirectHandler;