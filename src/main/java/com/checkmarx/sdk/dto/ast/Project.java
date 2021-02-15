package com.checkmarx.sdk.dto.ast;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Project {

    String name;
    List<String> groups;
    Tags tags;

    @Getter
    @Setter
    public class Tags {
        private String test;
        private String priority;
    }
}
