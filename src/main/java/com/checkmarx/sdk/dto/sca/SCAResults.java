package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.sca.report.Finding;
import com.checkmarx.sdk.dto.sca.report.Package;
import com.checkmarx.sdk.dto.ResultsBase;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SCAResults extends ResultsBase implements Serializable {
    private String scanId;
    private String webReportLink;
    private List<Finding> findings;
    private List<Package> packages;
    private boolean scaResultReady;
    private int nonVulnerableLibraries;
    private int vulnerableAndOutdated;
    private Summary summary;
    private boolean isPolicyViolated;
    private List<String> violatedPolicies;
    
    public void calculateVulnerableAndOutdatedPackages() {
        int sum;
        if (this.packages != null) {
            for (Package pckg : this.packages) {
                sum = pckg.getHighVulnerabilityCount() + pckg.getMediumVulnerabilityCount() + pckg.getLowVulnerabilityCount();
                if (sum == 0) {
                    this.nonVulnerableLibraries++;
                } else if (sum > 0 && pckg.isOutdated()) {
                    this.vulnerableAndOutdated++;
                }
            }
        }
    }
}
