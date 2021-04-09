package com.checkmarx.sdk.dto.sca;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Project {

    private String name;
    private String id;
    private List<String> assignedTeams;
}
