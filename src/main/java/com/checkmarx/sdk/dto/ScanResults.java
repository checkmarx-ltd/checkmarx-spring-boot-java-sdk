package com.checkmarx.sdk.dto;

import com.checkmarx.sdk.dto.ast.ASTResults;
import com.checkmarx.sdk.dto.sca.SCAResults;
import com.checkmarx.sdk.dto.sca.report.Finding;
import com.checkmarx.sdk.dto.sca.report.Package;
import com.checkmarx.sdk.dto.cx.CxScanSummary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Representation of Issues for a particular product/scan
 */
public class ScanResults{

    private Boolean osa = false;
    private String  projectId;
    private Integer SastScanId;
    private String  team;
    private String  project;
    private String  link;
    private String  files;
    private String  loc;
    private String  scanType;
    private List<XIssue> xIssues;
    private String output;
    private Map<String, Object> additionalDetails;
    private CxScanSummary scanSummary;
    private SCAResults scaResults;
    private ASTResults astResults;

    public ScanResults(Boolean osa, String projectId, String team, String project, String link, String files, String loc, String scanType,
                       List<XIssue> xIssues, Map<String, Object> additionalDetails, CxScanSummary scanSummary, SCAResults scaResults, ASTResults astResults) {
        this.osa = osa;
        this.projectId = projectId;
        this.team = team;
        this.project = project;
        this.link = link;
        this.files = files;
        this.loc = loc;
        this.scanType = scanType;
        this.xIssues = xIssues;
        this.additionalDetails = additionalDetails;
        this.scanSummary = scanSummary;
        this.scaResults = scaResults;
        this.astResults = astResults;
    }

    public ScanResults() {
    }

    public ASTResults getAstResults() {
        return astResults;
    }

    public void setAstResults(ASTResults astResults) {
        this.astResults = astResults;
    }

    public Integer getSastScanId() {
        return SastScanId;
    }

    public void setSastScanId(Integer sastScanId) {
        SastScanId = sastScanId;
    }

    public SCAResults getScaResults() {
        return scaResults;
    }

    public void setScaResults(SCAResults scaResults) {
        this.scaResults = scaResults;
    }

    public static ScanResultsBuilder builder() {
        return new ScanResultsBuilder();
    }

    public String getProjectId(){
        return this.projectId;
    }

    public void setProjectId(String projectId){
        this.projectId = projectId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Boolean getOsa() {
        return this.osa;
    }

    public String getScanType() {
        return this.scanType;
    }

    public List<XIssue> getXIssues() {
        return this.xIssues;
    }

    public Map<String, Object> getAdditionalDetails() { return this.additionalDetails; }

    public String getLink(){
        return this.link;
    }

    public void setLink(String link){
        this.link = link;
    }

    public String getFiles(){
        return this.files;
    }

    public void setFiles(String files){
        this.files = files;
    }

    public String getLoc(){
        return this.loc;
    }

    public void setLoc(String loc){
        this.loc = loc;
    }

    public void setOsa(Boolean osa) {
        this.osa = osa;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public void setXIssues(List<XIssue> xIssues) {
        this.xIssues = xIssues;
    }

    public void setAdditionalDetails(Map<String, Object> additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public CxScanSummary getScanSummary() {
        return scanSummary;
    }

    public void setScanSummary(CxScanSummary scanSummary) {
        this.scanSummary = scanSummary;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isAstResults(){
        return getAstResults() != null ;
    }

    public boolean isSastResults(){
        return Optional.ofNullable(getScanSummary()).isPresent();
    }
    
    @Override
    public String toString() {
        return "ScanResults(osa=" + this.getOsa()  + ", link=" + this.getLink() + ", files=" + this.getFiles() + ", loc=" + this.getLoc() + ", scanType=" + this.getScanType() + ", xIssues=" + this.getXIssues() + ")";
    }

    public void mergeWith(ScanResults scanResultsToMerge) {
        if(scanResultsToMerge!=null) {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

            modelMapper.map(scanResultsToMerge, this);
        }
    }

    public static class XIssue{
        private static final int HASH_CONST = 5225;
        private String vulnerability;
        private String vulnerabilityStatus;
        private String similarityId;
        private String cwe;
        private String cve;
        private String description;
        private String language;
        private String severity;
        private String link;
        private String filename;
        private String gitUrl;
        private int falsePositiveCount = 0;
        private List<OsaDetails> osaDetails;
        private List<ScaDetails> scaDetails;
        private Map<Integer, IssueDetails>  details;
        private Map<String, Object> additionalDetails;
        private String queryId;

        XIssue(String vulnerability, String vulnerabilityStatus, String similarityId, String cwe, String cve, String description, String language,
               String severity, String link, String filename, String gitUrl, List<OsaDetails> osaDetails, List<ScaDetails> scaDetails, Map<Integer, IssueDetails> details,
               Map<String, Object> additionalDetails, String queryId) {
            this.vulnerability = vulnerability;
            this.vulnerabilityStatus = vulnerabilityStatus;
            this.similarityId = similarityId;
            this.cwe = cwe;
            this.cve = cve;
            this.description = description;
            this.language = language;
            this.severity = severity;
            this.link = link;
            this.filename = filename;
            this.gitUrl = gitUrl;
            this.osaDetails = osaDetails;
            this.scaDetails = scaDetails;
            this.details = details;
            this.additionalDetails = additionalDetails;
            this.queryId = queryId;
        }

        public static XIssueBuilder builder() {
            return new XIssueBuilder();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            XIssue issue = (XIssue) o;

            if (!vulnerability.equals(issue.vulnerability)) return false;
            return filename.equals(issue.filename);
        }

        @Override
        public int hashCode() {
            int result = 0;
            if(vulnerability != null) {
                result = vulnerability.hashCode();
                result = HASH_CONST * result + filename.hashCode();
            }else{
                if(scaDetails != null){
                    result = scaDetails.get(0).finding.hashCode();
                    result = HASH_CONST * result +  scaDetails.get(0).vulnerabilityPackage.hashCode();
                }
            }

            return result;
        }

        public String getVulnerabilityStatus() {
            return vulnerabilityStatus;
        }

        public String gerQueryId() {
            return queryId;
        }
        public void setVulnerabilityStatus(String vulnerabilityStatus) {
            this.vulnerabilityStatus = vulnerabilityStatus;
        }

        public List<ScaDetails> getScaDetails() {
            return scaDetails;
        }

        public void setScaDetails(List<ScaDetails> scaDetails) {
            this.scaDetails = scaDetails;
        }

        public boolean isAllFalsePositive(){
            if(this.getDetails() == null){
                return false;
            }
            return this.getDetails().size() <= this.getFalsePositiveCount();
        }

        public String getSimilarityId() {
            return similarityId;
        }

        public void setSimilarityId(String similarityId) {
            this.similarityId = similarityId;
        }

        public String getVulnerability() {
            return this.vulnerability;
        }

        public String getCwe() {
            return this.cwe;
        }

        public String getCve() {
            return this.cve;
        }

        public String getDescription() {
            return this.description;
        }

        public String getLanguage() {
            return this.language;
        }

        public String getSeverity() {
            return this.severity;
        }

        public String getLink() {
            return this.link;
        }

        public String getFilename() {
            return this.filename;
        }

        public String getGitUrl() {
            return this.gitUrl;
        }

        public List<OsaDetails> getOsaDetails() {
            return this.osaDetails;
        }

        public Map<Integer, IssueDetails> getDetails() {
            return this.details;
        }

        public Map<String, Object> getAdditionalDetails() {
            return this.additionalDetails;
        }

        public void setVulnerability(String vulnerability) {
            this.vulnerability = vulnerability;
        }

        public void setCwe(String cwe) {
            this.cwe = cwe;
        }

        public void setCve(String cve) {
            this.cve = cve;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public void setGitUrl(String gitUrl) {
            this.gitUrl = gitUrl;
        }

        public void setOsaDetails(List<OsaDetails> osaDetails) {
            this.osaDetails = osaDetails;
        }

        public void setDetails(Map<Integer, IssueDetails> details) {
            this.details = details;
        }

        public void setAdditionalDetails(Map<String, Object> additionalDetails) {
            this.additionalDetails = additionalDetails;
        }

        public int getFalsePositiveCount(){
            return this.falsePositiveCount;
        }

        public void setFalsePositiveCount(int falsePositiveCount) {
            this.falsePositiveCount = falsePositiveCount;
        }

        public static class XIssueBuilder {
            private String vulnerability;
            private String vulnerabilityStatus;
            private String similarityId;
            private String cwe;
            private String cve;
            private String description;
            private String language;
            private String severity;
            private String link;
            private String file;
            private String queryId;
            private List<OsaDetails> osaDetails;
            private List<ScaDetails> scaDetails;

            private Map<Integer, IssueDetails> details;
            private Map<String, Object> additionalDetails;

            XIssueBuilder() {
            }

            public XIssue.XIssueBuilder vulnerability(String vulnerability) {
                this.vulnerability = vulnerability;
                return this;
            }

            public void vulnerabilityStatus(String vulnerabilityStatus) {
                this.vulnerabilityStatus = vulnerabilityStatus;
            }

            public XIssue.XIssueBuilder similarityId(String similarityId) {
                this.similarityId = similarityId;
                return this;
            }

            public XIssue.XIssueBuilder cwe(String cwe) {
                this.cwe = cwe;
                return this;
            }

            public XIssue.XIssueBuilder cve(String cve) {
                this.cve = cve;
                return this;
            }

            public XIssue.XIssueBuilder description(String description) {
                this.description = description;
                return this;
            }

            public XIssue.XIssueBuilder language(String language) {
                this.language = language;
                return this;
            }

            public XIssue.XIssueBuilder severity(String severity) {
                this.severity = severity;
                return this;
            }

            public XIssue.XIssueBuilder link(String link) {
                this.link = link;
                return this;
            }

            public XIssue.XIssueBuilder file(String file) {
                this.file = file;
                return this;
            }

            public XIssue.XIssueBuilder queryId(String queryId) {
                this.queryId = queryId;
                return this;
            }

            public XIssue.XIssueBuilder osaDetails(List<OsaDetails> osaDetails) {
                this.osaDetails = osaDetails;
                return this;
            }

            public XIssue.XIssueBuilder scaDetails(List<ScaDetails> scaDetails) {
                this.scaDetails = scaDetails;
                return this;
            }

            public XIssue.XIssueBuilder details(Map<Integer, IssueDetails> details) {
                this.details = details;
                return this;
            }

            public XIssue.XIssueBuilder additionalDetails(Map<String, Object> additionalDetails) {
                this.additionalDetails = additionalDetails;
                return this;
            }

            public XIssue build() {
                return new XIssue(vulnerability,  vulnerabilityStatus, similarityId, cwe, cve, description, language, severity, link, file, "", osaDetails, scaDetails, details, additionalDetails, queryId);
            }

            @Override
            public String toString() {
                return "ScanResults.XIssue.XIssueBuilder(simiarlityId="+ this.similarityId +",vulnerability=" + this.vulnerability + ", cwe=" + this.cwe + ", cve=" + this.cve + ", description=" + this.description + ", language=" + this.language + ", severity=" + this.severity + ", link=" + this.link + ", filename=" + this.file + ", osaDetails=" + this.osaDetails + ", details=" + this.details + ", additionalDetails=" + this.additionalDetails + ")";
            }
        }
    }

    public static class IssueDetails{
        private boolean falsePositive = false;
        private String codeSnippet;
        private String comment;
        public boolean isFalsePositive() {
            return falsePositive;
        }

        public void setFalsePositive(boolean falsePositive) {
            this.falsePositive = falsePositive;
        }

        public String getCodeSnippet() {
            return codeSnippet;
        }

        public void setCodeSnippet(String codeSnippet) {
            this.codeSnippet = codeSnippet;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public IssueDetails falsePositive(final boolean falsePositive) {
            this.falsePositive = falsePositive;
            return this;
        }

        public IssueDetails codeSnippet(final String codeSnippet) {
            this.codeSnippet = codeSnippet;
            return this;
        }

        public IssueDetails comment(final String comment) {
            this.comment = comment;
            return this;
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ScaDetails {
        private String vulnerabilityLink;
        private Finding finding;
        private Package vulnerabilityPackage;
    }

    public static class OsaDetails {
        private String cve;
        private String description;
        private String recommendation;
        private String severity;
        private String url;
        private String version;

        @ConstructorProperties({"cve", "description", "recommendation", "severity", "url", "version"})
        OsaDetails(String cve, String description, String recommendation, String severity, String url, String version) {
            this.cve = cve;
            this.description = description;
            this.recommendation = recommendation;
            this.severity = severity;
            this.url = url;
            this.version = version;
        }

        public static OsaDetailsBuilder builder() {
            return new OsaDetailsBuilder();
        }

        public String getCve() {
            return this.cve;
        }

        public String getDescription() {
            return this.description;
        }

        public String getRecommendation() {
            return this.recommendation;
        }

        public String getSeverity() {
            return this.severity;
        }

        public String getUrl() {
            return this.url;
        }

        public String getVersion() {
            return this.version;
        }

        public void setCve(String cve) {
            this.cve = cve;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public static class OsaDetailsBuilder {
            private String cve;
            private String description;
            private String recommendation;
            private String severity;
            private String url;
            private String version;

            OsaDetailsBuilder() {
            }

            public OsaDetails.OsaDetailsBuilder cve(String cve) {
                this.cve = cve;
                return this;
            }

            public OsaDetails.OsaDetailsBuilder description(String description) {
                this.description = description;
                return this;
            }

            public OsaDetails.OsaDetailsBuilder recommendation(String recommendation) {
                this.recommendation = recommendation;
                return this;
            }

            public OsaDetails.OsaDetailsBuilder severity(String severity) {
                this.severity = severity;
                return this;
            }

            public OsaDetails.OsaDetailsBuilder url(String url) {
                this.url = url;
                return this;
            }

            public OsaDetails.OsaDetailsBuilder version(String version) {
                this.version = version;
                return this;
            }

            public OsaDetails build() {
                return new OsaDetails(cve, description, recommendation, severity, url, version);
            }

            @Override
            public String toString() {
                return "ScanResults.OsaDetails.OsaDetailsBuilder(cve=" + this.cve + ", description=" + this.description + ", recommendation=" + this.recommendation + ", severity=" + this.severity + ", url=" + this.url + ", version=" + this.version + ")";
            }
        }
    }

    public static class ScanResultsBuilder {
        private Boolean osa;
        private String projectId;
        private String team;
        private String project;
        private String link;
        private String files;
        private String loc;
        private String scanType;
        private List<XIssue> xIssues;
        private Map<String, Object> additionalDetails;
        private CxScanSummary scanSummary;
        private SCAResults scaResults;
        private ASTResults astResults;

        ScanResultsBuilder() {
        }

        public ScanResults.ScanResultsBuilder osa(Boolean osa) {
            this.osa = osa;
            return this;
        }


        public ScanResults.ScanResultsBuilder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public ScanResults.ScanResultsBuilder project(String project) {
            this.project = project;
            return this;
        }

        public ScanResults.ScanResultsBuilder team(String team) {
            this.team = team;
            return this;
        }

        public ScanResults.ScanResultsBuilder link(String link) {
            this.link = link;
            return this;
        }

        public ScanResults.ScanResultsBuilder files(String filesScanned) {
            this.files = filesScanned;
            return this;
        }

        public ScanResults.ScanResultsBuilder loc(String locScanned) {
            this.loc = locScanned;
            return this;
        }

        public ScanResults.ScanResultsBuilder scanType(String scanType) {
            this.scanType = scanType;
            return this;
        }

        public ScanResults.ScanResultsBuilder xIssues(List<XIssue> xIssues) {
            this.xIssues = xIssues;
            return this;
        }

        public ScanResults.ScanResultsBuilder additionalDetails(Map<String, Object> additionalDetails) {
            this.additionalDetails = additionalDetails;
            return this;
        }

        public ScanResults.ScanResultsBuilder scanSummary(CxScanSummary scanSummary) {
            this.scanSummary = scanSummary;
            return this;
        }

        public ScanResults.ScanResultsBuilder scaResults(SCAResults scaResults) {
            this.scaResults = scaResults;
            return this;
        }

        public ScanResults.ScanResultsBuilder astResults(ASTResults astResults) {
            this.astResults = astResults;
            return this;
        }

        public ScanResults build() {
            return new ScanResults(osa, projectId, team, project, link, files, loc, scanType, xIssues, additionalDetails, scanSummary, scaResults, astResults);
        }

        @Override
        public String toString() {
            return "ScanResults.ScanResultsBuilder(osa=" + this.osa + ", link=" + this.link + ", files=" + this.files + ", loc=" + this.loc + ", scanType=" + this.scanType + ", xIssues=" + this.xIssues + ", additionalDetails=" + this.additionalDetails + ")";
        }
    }
}
