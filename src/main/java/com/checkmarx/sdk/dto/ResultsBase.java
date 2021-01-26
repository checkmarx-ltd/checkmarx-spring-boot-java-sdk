package com.checkmarx.sdk.dto;

import com.checkmarx.sdk.exception.ASTRuntimeException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ResultsBase {
    private ASTRuntimeException exception;
}
