package com.checkmarx.sdk.dto.sca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScaPDFExport {
    private String scanId;
    private String fileFormat;
}
