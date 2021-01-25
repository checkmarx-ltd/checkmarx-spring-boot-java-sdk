package com.checkmarx.sdk.dto.sca;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AstScaConfig extends com.checkmarx.sdk.dto.AstScaConfig implements Serializable {
    private String accessControlUrl;
    private String username;
    private String password;
    private String tenant;

    /**
     * true: upload all sources for scan
     * <br>
     * false: only upload manifest and fingerprints for scan. Useful for customers that don't want their proprietary
     * code to be uploaded into the cloud.
     */
    private boolean includeSources;
    
    private String fingerprintsIncludePattern;
    private String manifestsIncludePattern;
    private String fingerprintFilePath;
}
