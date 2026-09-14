package com.mips.domain.stock.dto;

import java.util.List;

public record FinnhubTradeResponse(
        String type,
        List<FinnhubTradeData> data
) {

}
