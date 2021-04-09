package com.checkmarx.sdk.dto.ast.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SeverityCounter implements Serializable {
    private String severity;
    private int counter;
}
