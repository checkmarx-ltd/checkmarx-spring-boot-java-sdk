package com.checkmarx.sdk.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GitCredentials {
    private String type;
    private String value;
}
