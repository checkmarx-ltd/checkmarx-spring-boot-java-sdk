package com.checkmarx.sdk.dto.cxgo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CreateScan {
    @JsonProperty("project_id")
    public Integer projectId;
    @JsonProperty("engine_types")
    public List<String> engineTypes;

    CreateScan(Integer projectId, List<String> engineTypes) {
        this.projectId = projectId;
        this.engineTypes = engineTypes;
    }

    public static OdScanBuilder builder() {
        return new OdScanBuilder();
    }

    @JsonProperty("project_id")
    public Integer getProjectId() {
        return this.projectId;
    }

    @JsonProperty("project_id")
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("engine_types")
    public List<String> getEngineTypes() {
        return engineTypes;
    }

    @JsonProperty("engine_types")
    public void setEngineTypes(List<String> engineTypes) {
        this.engineTypes = engineTypes;
    }

    public static class OdScanBuilder {
        private Integer projectId;
        private List<String> engineTypes;

        OdScanBuilder() {
        }

        public OdScanBuilder projectId(Integer projectId) {
            this.projectId = projectId;
            return this;
        }

        public OdScanBuilder engineTypes(List<String> engineTypes) {
            this.engineTypes = engineTypes;
            return this;
        }

        public CreateScan build() {
            return new CreateScan(projectId, engineTypes);
        }
    }
}
