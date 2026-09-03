package com.mips.domain.stock.dto;

import java.util.List;

public record NasdaqData(
        int totalrecords,
        int limit,
        int offset,
        String date,
        NasdaqDataIndex data,
        String filters,
        String title
) {
}
