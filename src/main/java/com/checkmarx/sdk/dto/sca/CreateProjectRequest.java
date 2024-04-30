package com.checkmarx.sdk.dto.sca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateProjectRequest {

    private String name;
    private List<String> assignedTeams;
    private Map<String,String> tags;
}
