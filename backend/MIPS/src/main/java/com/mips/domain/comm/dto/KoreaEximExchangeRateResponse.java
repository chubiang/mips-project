package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KoreaEximExchangeRateResponse(
        int result,
        String curUnit,
        String ttb,
        String tts,
        String dealBasR,
        int bkpr,
        int yyEfeeR,
        int tenDdEfeeR,
        int kftcBkpr,
        double kftcDealBasR,
        String curNm
) {
}
