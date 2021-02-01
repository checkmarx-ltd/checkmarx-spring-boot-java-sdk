package com.checkmarx.sdk.dto.sca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateProjectRequest {

    private String name;
    private List<String> assignedTeams;
}
