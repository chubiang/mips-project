package com.mips.global.exception;

/**
 * Finnhub API가 HTTP 429(Too Many Requests)를 반환했을 때 발생한다.
 *
 * <p>호출 실패의 원인이 요청 제한임을 다른 일시적 장애와 구분하여
 * Batch Retry/Backoff 정책에서 명시적으로 처리하기 위한 예외다.</p>
 */
public class FinnhubRateLimitException extends RuntimeException {

    public FinnhubRateLimitException(String message) {
        super(message);
    }

    public FinnhubRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
