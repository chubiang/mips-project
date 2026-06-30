import type { ApiResponse } from "@/types/Comm";

// 싱글톤으로 워커 인스턴스 생성
const authWorker = new Worker(new URL('@/workers/authWorker.ts', import.meta.url), {
    type: 'module',
});

// 비동기 통신을 위해 요청 ID별로 Promise의 resolve/reject를 저장할 맵
const pendingRequests = new Map();

authWorker.addEventListener('message', (event: MessageEvent) => {
    const { type, id, data, error } = event.data;
    const promiseHandlers = pendingRequests.get(id);

    if (promiseHandlers) {
        if (type === 'API_SUCCESS' || type === 'ACCESS_TOKEN_SET' || type === 'LOGGED_OUT') {
            promiseHandlers.resolve(data);
        } else if (type === 'API_ERROR') {
            promiseHandlers.reject(error);
        }
        pendingRequests.delete(id); // 처리 완료 후 메모리 정리
    }
});

export const setTokenToWorker = (token: string): Promise<ApiResponse<void>> => {
    return new Promise((resolve) => {
        const id = Date.now().toString();
        pendingRequests.set(id, { resolve });
        authWorker.postMessage({ type: 'SET_TOKEN', payload: { token }, id });
    });
};

export const fetchViaWorker = (url: string, options?: RequestInit): Promise<ApiResponse<Response>> => {
    return new Promise((resolve, reject) => {
        const id = crypto.randomUUID(); // 고유 ID 부여
        pendingRequests.set(id, { resolve, reject });
        authWorker.postMessage({ type: 'API_REQUEST', payload: { url, options }, id });
    });
};

// 워커에게 토큰을 달라고 요청하고, 답장을 기다리는 함수
export const getTokenFromWorker = (): Promise<ApiResponse<string>> => {
    return new Promise((resolve) => {
      const messageHandler = (event: MessageEvent) => {
        if (event.data.type !== 'ACCESS_TOKEN_RESPONSE') return

        authWorker.removeEventListener('message', messageHandler);
        resolve(event.data.token ?? null);
      }

      authWorker.addEventListener('message', messageHandler);
      const id = crypto.randomUUID(); // 고유 ID 부여
      pendingRequests.set(id, { resolve });
      // 2. 워커에게 "토큰 줘!" 하고 편지 발송
      authWorker.postMessage({ type: 'GET_TOKEN', id });
    })
}