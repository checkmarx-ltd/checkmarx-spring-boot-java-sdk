package com.checkmarx.sdk.dto.ast.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StatusCounter implements Serializable {
    private String status;
    private int counter;
}
