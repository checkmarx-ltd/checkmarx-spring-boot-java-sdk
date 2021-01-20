package com.cx.restclient.ast.dto.sca.report;

import com.cx.restclient.dto.scansummary.Severity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This entity is called vulnerability in SCA API, but here it is called Finding for consistency.
 * Indicates a specific type of vulnerability detected in a specific package.
 */
@Getter
@Setter
public class Finding implements Serializable {
    private String id;
    private String cveName;
    private double score;
    private Severity severity;
    private String publishDate;
    private List<String> references = new ArrayList<>();
    private String description;
    private String recommendations;
    private String packageId;
    private String similarityId;
    private String fixResolutionText;
    private boolean isIgnored;
}
