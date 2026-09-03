package com.mips.domain.stock.dto;

import java.util.List;

public record NasdaqDataIndex(
        String asOf,
        NasdaqDataHeader headers,
        List<NasdaqDataRow> rows
) {
}
