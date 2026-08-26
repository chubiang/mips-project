import type { ApiResponse } from "@/types/Comm";

// 싱글톤으로 워커 인스턴스 생성
const authWorker = new Worker(new URL('@/workers/authWorker.ts', import.meta.url), {
    type: 'module',
});

// 비동기 통신을 위해 요청 ID별로 Promise의 resolve/reject를 저장할 맵
const pendingRequests = new Map<string, {
    // 서로 다른 반환 타입의 요청을 하나의 레지스트리에서 관리한다.
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolve: (value: any) => void;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    reject?: (reason?: any) => void;
}>();

authWorker.addEventListener('message', (event: MessageEvent) => {
    const { type, id, data, error } = event.data;
    const promiseHandlers = pendingRequests.get(id);

    if (promiseHandlers) {
        if (type === 'API_SUCCESS' || type === 'ACCESS_TOKEN_SET' || type === 'LOGGED_OUT') {
            promiseHandlers.resolve(data);
        } else if (type === 'API_ERROR') {
            promiseHandlers.reject?.(error);
        }
        pendingRequests.delete(id); // 처리 완료 후 메모리 정리
    }
});

export const setTokenToWorker = (token: string): Promise<void> => {
    return new Promise((resolve) => {
        const id = crypto.randomUUID();
        pendingRequests.set(id, { resolve });
        authWorker.postMessage({ type: 'SET_TOKEN', payload: { token }, id });
    });
};

export const fetchViaWorker = (url: string, options?: RequestInit): Promise<ApiResponse<Response>> => {
    return new Promise((resolve, reject) => {
        const id = crypto.randomUUID();
        pendingRequests.set(id, { resolve, reject });
        authWorker.postMessage({ type: 'API_REQUEST', payload: { url, options }, id });
    });
};

// 워커에게 토큰을 달라고 요청하고, 답장을 기다리는 함수
export const getTokenFromWorker = (): Promise<string | null> => {
    return new Promise((resolve) => {
      const id = crypto.randomUUID();
      const messageHandler = (event: MessageEvent) => {
        if (event.data.type !== 'ACCESS_TOKEN_RESPONSE' || event.data.id !== id) return

        authWorker.removeEventListener('message', messageHandler);
        resolve(event.data.token ?? null);
      }

      authWorker.addEventListener('message', messageHandler);
      // 2. 워커에게 "토큰 줘!" 하고 편지 발송
      authWorker.postMessage({ type: 'GET_TOKEN', id });
    })
}
