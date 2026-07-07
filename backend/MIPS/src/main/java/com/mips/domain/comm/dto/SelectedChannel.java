package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.portone.sdk.server.common.PgProvider;


@JsonIgnoreProperties(ignoreUnknown = true)
public record SelectedChannel (
    String type,
    String id,
    String key,
    String name,
    String pgProvider,
    String pgMerchantId
) {}