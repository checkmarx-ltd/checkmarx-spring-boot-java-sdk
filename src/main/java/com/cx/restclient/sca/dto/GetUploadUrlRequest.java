package com.cx.restclient.sca.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetUploadUrlRequest {
    private List<ScanAPIConfigEntry> config;
}
