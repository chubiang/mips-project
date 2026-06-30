package com.mips.domain.comm.enums;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    // --- [성공 메시지] ---
    CHARGE_INIT_SUCCESS("충전 사전 요청이 성공적으로 생성되었습니다."),
    PAYMENT_VERIFY_SUCCESS("결제 검증 및 충전이 정상적으로 완료되었습니다."),

    // 동적 데이터가 들어가는 성공 메시지 템플릿 (%s: 문자열, %d: 숫자)
    PAYMENT_SUCCESS_DETAILS("결제 완료: %d원이 충전되었습니다. (결제번호: %s)"),

    // --- [에러/예외 메시지] ---
    CHARGE_NOT_FOUND("해당 충전 요청 건을 찾을 수 없습니다."),
    PAYMENT_ALREADY_PROCESSED("이미 처리가 완료된 결제 건입니다."),

    // 동적 데이터가 들어가는 에러 메시지 템플릿
    PAYMENT_AMOUNT_MISMATCH("결제 금액 위변조가 의심됩니다. (DB금액: %d, 포트원금액: %d)"),
    PAYMENT_STATUS_INVALID("유효하지 않은 결제 상태입니다. (현재상태: %s)"),

    // 공통 에러
    INVALID_REQUEST("잘못된 요청 파라미터입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생하여 처리에 실패했습니다."),
    INVALID_USER("등록된 사용자가 아닙니다.");

    /**
     * -- GETTER --
     *  기본 메시지 반환
     */
    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    /**
     * 가변 인자(동적 데이터)를 받아 메시지 템플릿을 완성하여 반환하는 유틸리티 메서드
     * 내부적으로 String.format을 사용하여 깔끔하게 문자열을 결합합니다.
     * * @param args 템플릿에 들어갈 값들
     * @return 완성된 문자열
     */
    public String format(Object... args) {
        return String.format(this.message, args);
    }
}
