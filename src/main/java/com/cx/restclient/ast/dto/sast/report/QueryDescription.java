package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class QueryDescription  implements Serializable {
    
    private String queryId;
    private String queryDescriptionId;
    private String resultDescription;
    private String risk;
    private String cause;
    private String generalRecommendations;
}
