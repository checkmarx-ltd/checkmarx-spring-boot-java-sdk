package com.checkmarx.sdk.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SourceLocationType {
    LOCAL_DIRECTORY("upload"),
    REMOTE_REPOSITORY("git"),
    CLONED_REMOTE_REPOSITORY("cloned");

    /**
     * Value used in API calls. Currently all the clients using this enum use the same API values.
     */
    private final String apiValue;
    
}
