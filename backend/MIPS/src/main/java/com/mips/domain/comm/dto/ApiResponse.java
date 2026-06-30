package com.mips.domain.comm.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모든 API의 공통 응답 포맷을 담당하는 래퍼(Wrapper) 클래스
 * @param <T> 응답으로 내려줄 실제 데이터의 타입
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success; // 성공 여부 (true/false)
    private String message;  // 클라이언트에게 보여줄 메시지
    private T data;          // 실제 넘겨줄 데이터 (성공 시에만 존재, 실패 시 null)

    // --- [정적 팩토리 메서드] 객체 생성을 깔끔하게 하기 위한 유틸리티 ---

    /**
     * 성공 응답 (데이터만 넘길 때)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
    }

    /**
     * 성공 응답 (메시지와 데이터를 함께 넘길 때)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 에러 응답 (데이터 없이 메시지만 넘길 때)
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}