package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Finding implements Serializable {
    private String queryID;
    private String queryName;
    private String severity;
    private int cweID;
    private int similarityID;
    private int uniqueID;
    private List<FindingNode> nodes = new ArrayList<>();
    private String pathSystemID;
    private String firstScanID;
    private String firstFoundAt;
    private String foundAt;
    private String status;
    private String description;
    private String state;
}
