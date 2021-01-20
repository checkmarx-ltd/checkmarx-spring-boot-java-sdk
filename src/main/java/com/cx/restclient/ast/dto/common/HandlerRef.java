package com.cx.restclient.ast.dto.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HandlerRef {
    private String type;
    private String value;
}
