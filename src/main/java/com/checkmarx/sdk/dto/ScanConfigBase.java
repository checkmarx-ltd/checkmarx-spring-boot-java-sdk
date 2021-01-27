package com.checkmarx.sdk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class ScanConfigBase implements Serializable {
    private String apiUrl;
    private String webAppUrl;
    private SourceLocationType sourceLocationType;
    private String zipFilePath;

    /**
     * Must be specified if sourceLocationType is {@link SourceLocationType#REMOTE_REPOSITORY}
     */
    private RemoteRepositoryInfo remoteRepositoryInfo;
}
