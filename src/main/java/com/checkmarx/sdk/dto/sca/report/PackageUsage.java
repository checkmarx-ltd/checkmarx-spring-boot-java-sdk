package com.checkmarx.sdk.dto.sca.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PackageUsage implements Serializable {
    private String usageType;
    private String packageId;
}
