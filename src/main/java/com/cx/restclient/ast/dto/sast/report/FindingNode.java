package com.cx.restclient.ast.dto.sast.report;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FindingNode implements Serializable {
    private int column;
    private String fileName;
    private String fullName;
    private int length;
    private int line;
    private int methodLine;
    private String name;
    private String nodeSystemID;
}