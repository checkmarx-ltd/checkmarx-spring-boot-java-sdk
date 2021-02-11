package com.checkmarx.sdk.dto.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectsList {
    
    Integer totalCount;
    List<ProjectId> projects;
}
