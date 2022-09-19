package com.checkmarx.sdk.dto.cx;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CxProjectBranch {
    public Integer id;
    public Integer originalProjectId;
    public Integer branchedOnScanId;
    public Integer branchedProjectId;
    public Date timestamp;
    public String comment;
    public Status status;
    public String errorMessage;

    @Getter
    @Setter
    @ToString
    public static class Status {
        public Integer id;
        public String value;
    }
}
