package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.GitCredentials;
import com.checkmarx.sdk.dto.HandlerRef;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AstScanStartHandler {
    /**
     * For local directory scan - the URL where the zipped directory has been uploaded.
     * For remote repo scan - a URL for which 'git clone' is possible.
     */
    private String repoUrl;

    /**
     * For remote repo scan, contains a reference to a specific commit.
     */
    private HandlerRef ref;

    private String username;

    private GitCredentials credentials;
}
