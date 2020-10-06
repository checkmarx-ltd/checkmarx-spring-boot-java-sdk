package com.checkmarx.sdk.dto.od;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScanStatus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("progress")
    private Integer progress;

    @JsonProperty("fail_code")
    private ScanFailCodeEnum failCode;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("progress")
    public Integer getProgress() {
        return progress;
    }

    @JsonProperty("progress")
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public ScanFailCodeEnum getFailCode() {
        return failCode;
    }

    public void setFailCode(ScanFailCodeEnum failCode) {
        this.failCode = failCode;
    }

    public enum Status {
        DRAFT("DRAFT"),
        UPLOADING("UPLOADING"),
        PROCESSING("PROCESSING"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED");

        private String s;

        private Status(String status) {
            this.s = status;
        }

        public String getStatus() {
            return s;
        }

        public void setStatus(String s) {
            this.s = s;
        }
    }

    public enum ScanFailCodeEnum {
        UNEXPECTED_ERROR("UNEXPECTED_ERROR");

        private String error;

        private ScanFailCodeEnum(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}