package com.cx.restclient.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SourceLocationType {
    LOCAL_DIRECTORY("upload"),
    REMOTE_REPOSITORY("git");

    /**
     * Value used in API calls. Currently all the clients using this enum use the same API values.
     */
    private final String apiValue;
}
