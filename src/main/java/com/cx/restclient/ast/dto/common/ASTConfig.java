package com.cx.restclient.ast.dto.common;

import com.cx.restclient.dto.SourceLocationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class ASTConfig implements Serializable {
    private String apiUrl;
    private String webAppUrl;
    private SourceLocationType sourceLocationType;
    private String zipFilePath;

    /**
     * Must be specified if sourceLocationType is {@link SourceLocationType#REMOTE_REPOSITORY}
     */
    private RemoteRepositoryInfo remoteRepositoryInfo;
}
