package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.sast.Filter;
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
    public Filter.Severity severity;
    public String publishDate;
    public List<String> references = new ArrayList<>();
    public String description;
    public String recommendations;
    public String packageId;
    public String similarityId;
    public String fixResolutionText;
    public boolean isIgnored;
}
