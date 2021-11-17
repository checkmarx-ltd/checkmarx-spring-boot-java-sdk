package com.checkmarx.sdk.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HandlerRef {
    private String type;
    private String value;
}
