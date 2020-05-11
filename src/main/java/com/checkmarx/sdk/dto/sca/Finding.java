package com.checkmarx.sdk.dto.sca;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Finding {
    public String id;
    public String cveName;
    public double score;
    public String severity;
    public String publishDate;
    public List<String> references = new ArrayList<>();
    public String description;
    public String recommendations;
    public String packageId;
    public String similarityId;
    public String fixResolutionText;
    public boolean isIgnored;
}
