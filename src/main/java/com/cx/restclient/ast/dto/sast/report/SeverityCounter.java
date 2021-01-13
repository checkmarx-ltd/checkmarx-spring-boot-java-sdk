package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SeverityCounter implements Serializable {
    private String severity;
    private int counter;
}
