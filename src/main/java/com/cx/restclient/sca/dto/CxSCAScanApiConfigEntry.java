package com.cx.restclient.sca.dto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CxSCAScanApiConfigEntry implements ScanAPIConfigEntry {
    private String type;
    private CxSCAScanAPIConfig value;
}
