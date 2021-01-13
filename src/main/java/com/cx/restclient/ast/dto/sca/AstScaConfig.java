package com.cx.restclient.ast.dto.sca;

import com.cx.restclient.ast.dto.common.ASTConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AstScaConfig extends ASTConfig implements Serializable {
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
