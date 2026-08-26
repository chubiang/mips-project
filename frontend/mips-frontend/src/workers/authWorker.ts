// src/workers/authWorker.ts

// 🌟 React 메인 스레드에서는 절대 접근할 수 없는 워커 전용 메모리 공간
let accessToken: string | null = null;


function getTokenExpiration(token: string | null): Date | null {
  try {
    const payload = token?.split(".")[1];
    if (!payload) return null;
    const normalized = payload
      .replace(/-/g, "+")
      .replace(/_/g, "/");

    const decoded = JSON.parse(atob(normalized));

    return typeof decoded.exp === "number"
      ? new Date(decoded.exp * 1000)
      : null;
  } catch {
    return null;
  }
}

self.addEventListener('message', async (event: MessageEvent) => {
    const { type, payload, id } = event.data;

    switch (type) {
        case 'GET_TOKEN':
            // React가 워커에게 토큰을 달라고 요청하면, 메모리에서 꺼내서 전달합니다.
            self.postMessage({ type: 'ACCESS_TOKEN_RESPONSE', id, token: accessToken });
            console.log({
            exists: !!accessToken,
            expiration: accessToken
                ? getTokenExpiration(accessToken)
                : null,
            suffix: accessToken?.slice(-6),
            });
            break;
        case 'SET_TOKEN':
            // 카카오 로그인 성공 직후 React가 워커로 토큰을 던져주면 메모리에 저장합니다.
            accessToken = payload.token;
            self.postMessage({ type: 'ACCESS_TOKEN_SET', id, token: accessToken });
            break;

        case 'API_REQUEST':
            // React가 API 요청을 부탁하면, 워커가 토큰을 붙여서 대신 통신합니다.
            try {
                const headers = new Headers(payload.options?.headers || {});
                headers.append('Content-Type', 'application/json');
                if (accessToken) {
                    headers.append('Authorization', `Bearer ${accessToken}`);
                }
                if (payload.options?.withCredentials === true) {
                    payload.options.credentials = 'include';
                }
                const response = await fetch(`http://localhost:8082${payload.url}`, {
                    method: payload.options?.method || 'GET',
                    headers,
                    ...payload.options,
                });

                // 401(만료) 에러 처리 로직도 워커 안에서 1차적으로 제어 가능합니다.
                if (response.status === 401) {
                    self.postMessage({ type: 'API_ERROR', id, error: 'ACCESS_TOKEN_EXPIRED' });
                    return;
                }

                const data = await response.json();
                self.postMessage({ type: 'API_SUCCESS', id, data });
            } catch (error) {
                self.postMessage({ type: 'API_ERROR', id, error });
            }
            break;
            
        case 'LOGOUT':
            // 로그아웃 시 메모리에서 토큰을 완전히 날려버립니다.
            accessToken = null;
            self.postMessage({ type: 'LOGGED_OUT', id });
            break;
    }
});