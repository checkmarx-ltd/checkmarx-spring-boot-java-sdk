package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.sca.report.AstScaSummaryResults;
import com.checkmarx.sdk.dto.sca.report.Finding;
import com.checkmarx.sdk.dto.sca.report.Package;
import com.checkmarx.sdk.dto.ResultsBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AstScaResults extends ResultsBase implements Serializable {
    private String scanId;
    private AstScaSummaryResults summary;
    private String webReportLink;
    private List<Finding> findings;
    private List<Package> packages;
    private boolean scaResultReady;
    private int nonVulnerableLibraries;
    private int vulnerableAndOutdated;

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
