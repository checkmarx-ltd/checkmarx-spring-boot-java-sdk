package com.cx.restclient.ast.dto.sca.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DependencyPathSegment implements Serializable {
    private String id;
    private String name;
    private String version;
    private boolean isResolved;
    private boolean isDevelopment;
}
