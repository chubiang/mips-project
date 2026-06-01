// src/workers/authWorker.ts

// 🌟 React 메인 스레드에서는 절대 접근할 수 없는 워커 전용 메모리 공간
let accessToken: string | null = null;

self.addEventListener('message', async (event: MessageEvent) => {
    const { type, payload, id } = event.data;

    switch (type) {
        case 'SET_TOKEN':
            // 카카오 로그인 성공 직후 React가 워커로 토큰을 던져주면 메모리에 저장합니다.
            accessToken = payload.token;
            self.postMessage({ type: 'TOKEN_SET', id });
            break;

        case 'API_REQUEST':
            // React가 API 요청을 부탁하면, 워커가 토큰을 붙여서 대신 통신합니다.
            try {
                const headers = new Headers(payload.options?.headers || {});
                if (accessToken) {
                    headers.append('Authorization', `Bearer ${accessToken}`);
                }

                const response = await fetch(payload.url, {
                    ...payload.options,
                    headers,
                });

                // 401(만료) 에러 처리 로직도 워커 안에서 1차적으로 제어 가능합니다.
                if (response.status === 401) {
                    self.postMessage({ type: 'API_ERROR', id, error: 'TOKEN_EXPIRED' });
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