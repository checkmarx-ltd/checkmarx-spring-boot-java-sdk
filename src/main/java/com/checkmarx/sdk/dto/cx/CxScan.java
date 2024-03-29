package com.checkmarx.sdk.dto.cx;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;

@JsonPropertyOrder({
        "projectId",
        "isIncremental",
        "isPublic",
        "forceScan",
        "comment",
        "customFields"
})

public class CxScan {
    @JsonProperty("projectId")
    public Integer projectId;
    @JsonProperty("isIncremental")
    public Boolean isIncremental;
    @JsonProperty("isPublic")
    public Boolean isPublic;
    @JsonProperty("forceScan")
    public Boolean forceScan;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("customFields")
    public Map<String, String> customFields;

    @java.beans.ConstructorProperties({"projectId", "isIncremental", "isPublic", "forceScan", "comment", "customFields"})
    CxScan(Integer projectId, Boolean isIncremental, Boolean isPublic, Boolean forceScan, String comment, Map<String, String> customFields) {
        this.projectId = projectId;
        this.isIncremental = isIncremental;
        this.isPublic = isPublic;
        this.forceScan = forceScan;
        this.comment = comment;
        this.customFields = customFields;
    }

    public static CxScanBuilder builder() {
        return new CxScanBuilder();
    }

    public Integer getProjectId() {
        return this.projectId;
    }

    public Boolean getIsIncremental() {
        return this.isIncremental;
    }

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public Boolean getForceScan() {
        return this.forceScan;
    }

    public String getComment() {
        return this.comment;
    }

    public Map<String, String> getCustomFields() {
        return this.customFields;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public void setIsIncremental(Boolean isIncremental) {
        this.isIncremental = isIncremental;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setForceScan(Boolean forceScan) {
        this.forceScan = forceScan;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    public String toString() {
        return "CxScan(projectId=" + this.getProjectId() +
                ", isIncremental=" + this.getIsIncremental() +
                ", isPublic=" + this.getIsPublic() +
                ", forceScan=" + this.getForceScan() +
                ", comment=" + this.getComment() +
                ", customFields=" + this.getCustomFields() +
                ")";
    }

    public static class CxScanBuilder {
        private Integer projectId;
        private Boolean isIncremental;
        private Boolean isPublic;
        private Boolean forceScan;
        private String comment;
        private Map<String, String> customFields;

        CxScanBuilder() {
        }

        public CxScan.CxScanBuilder projectId(Integer projectId) {
            this.projectId = projectId;
            return this;
        }

        public CxScan.CxScanBuilder isIncremental(Boolean isIncremental) {
            this.isIncremental = isIncremental;
            return this;
        }

        public CxScan.CxScanBuilder isPublic(Boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public CxScan.CxScanBuilder forceScan(Boolean forceScan) {
            this.forceScan = forceScan;
            return this;
        }

        public CxScan.CxScanBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public CxScan.CxScanBuilder customFields(Map<String, String> customFields) {
            this.customFields = customFields;
            return this;
        }

        public CxScan build() {
            return new CxScan(projectId, isIncremental, isPublic, forceScan, comment, customFields);
        }

        public String toString() {
            return "CxScan.CxScanBuilder(projectId=" + this.projectId +
                    ", isIncremental=" + this.isIncremental +
                    ", isPublic=" + this.isPublic +
                    ", forceScan=" + this.forceScan +
                    ", comment=" + this.comment +
                    ", customFields=" + this.customFields +
                    ")";
        }
    }
}