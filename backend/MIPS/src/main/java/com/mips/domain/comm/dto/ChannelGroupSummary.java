package com.mips.domain.comm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChannelGroupSummary (
    String id,
    String name,
    boolean isForTest
)
{}
