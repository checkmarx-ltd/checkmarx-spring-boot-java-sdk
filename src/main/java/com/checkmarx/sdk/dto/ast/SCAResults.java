package com.checkmarx.sdk.dto.ast;


import lombok.Getter;
import lombok.Setter;
import com.cx.restclient.ast.dto.sca.report.Finding;
import com.cx.restclient.ast.dto.sca.report.Package;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@XmlRootElement
public class SCAResults {
    private String scanId;
    private Summary summary;
    private String webReportLink;
    private List<Finding> findings;
    private List<Package> packages;
}
