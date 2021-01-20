package com.cx.restclient.dto;

import com.checkmarx.sdk.exception.ASTRuntimeException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Results {
    private ASTRuntimeException exception;
}
