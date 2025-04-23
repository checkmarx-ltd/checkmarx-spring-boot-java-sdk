package com.checkmarx.sdk.dto.sca.report;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@Getter
public class PackageUsage implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(PackageUsage.class);
    private String usageType;
    private String packageId;

    public void setUsageType(String usageType) {
        logIfNull("usageType",usageType);
        this.usageType = usageType;
    }

    public void setPackageId(String packageId) {
        logIfNull("packageId",packageId);
        this.packageId = packageId;
    }

    private void logIfNull(String fieldName, Object value) {
        if (value == null) {
            logger.warn("field '{}' in PackageUsage class with id={} has null value", fieldName,this.packageId);
        }
    }
}
