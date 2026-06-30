package com.mips.domain.comm.utils;

import com.mips.domain.comm.enums.OrderPrefix;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 도메인별 고유 식별자(Business Key) 생성 유틸리티 클래스
 */
public class OrderNumberGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 지정된 접두어 규칙에 따라 고유 식별자를 생성합니다.
     * 형식: [접두어]-[YYYYMMDD]-[랜덤12자리] (예: CHG-20260619-A1B2C3D4F45E2)
     *
     * @param prefix 도메인 접두어 Enum
     * @return 생성된 고유 식별자 문자열
     */
    public static String generate(OrderPrefix prefix) {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        // UUID 앞 12자리만 추출하여 대문자로 변환
        String randomStr = UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        return String.format("%s-%s-%s", prefix.getCode(), dateStr, randomStr);
    }
}