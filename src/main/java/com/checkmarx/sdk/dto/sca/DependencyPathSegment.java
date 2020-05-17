package com.checkmarx.sdk.dto.sca;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DependencyPathSegment {
    public String id;
    public String name;
    public String version;
    public boolean isResolved;
    public boolean isDevelopment;
}
