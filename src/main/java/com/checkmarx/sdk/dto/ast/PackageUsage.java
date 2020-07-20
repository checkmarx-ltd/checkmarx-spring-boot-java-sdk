package com.checkmarx.sdk.dto.ast;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageUsage {
    public String usageType;
    public String packageId;
}
