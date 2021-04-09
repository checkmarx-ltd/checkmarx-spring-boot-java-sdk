package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.ScanConfigBase;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ScaConfig extends ScanConfigBase implements Serializable {
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

    private List<String> excludeFiles;
    
    private String fingerprintsIncludePattern;
    private String manifestsIncludePattern;
    private String fingerprintFilePath;
    private String team;
}
