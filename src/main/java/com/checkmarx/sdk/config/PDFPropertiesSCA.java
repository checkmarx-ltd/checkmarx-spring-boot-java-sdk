package com.checkmarx.sdk.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDFPropertiesSCA {

    private String fileNameFormat = "[APP]-[BRANCH]-[TIME]";
    private String dataFolder = "/tmp";
}
