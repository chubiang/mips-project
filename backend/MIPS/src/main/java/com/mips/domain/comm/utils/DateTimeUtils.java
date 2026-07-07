package com.mips.domain.comm.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static LocalDateTime toKstLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return LocalDateTime.ofInstant(instant, KST);
    }
}
