package com.mips.global.handler;

import com.mips.domain.comm.dto.ApiResponse;
import com.mips.domain.comm.enums.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("요청 거절: {}", e.getMessage()); // e.printStackTrace() 대신 log 사용!

        return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage()) // "유효하지 않은 회원입니다" 메시지가 나감
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllException(Exception e) {
        log.error("서버 내부 에러 발생!", e); // 에러 로그 기록

        return ResponseEntity.internalServerError().body(
                ApiResponse.error(ResponseMessage.INTERNAL_SERVER_ERROR.getMessage())
        );
    }
}