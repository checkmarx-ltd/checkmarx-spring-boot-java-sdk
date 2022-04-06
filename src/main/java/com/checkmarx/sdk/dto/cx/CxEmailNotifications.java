package com.checkmarx.sdk.dto.cx;

//import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CxEmailNotifications {
    //@JsonProperty("afterScan")
    private List<String> afterScan;
    //@JsonProperty("beforeScan")
    private List<String> beforeScan;
    //@JsonProperty("failedScan")
    private List<String> failedScan;

    public CxEmailNotifications() {
        this(null, null, null);
    }

    public CxEmailNotifications(List<String> afterScan, List<String> beforeScan, List<String> failedScan) {
        this.afterScan = afterScan;
        this.beforeScan = beforeScan;
        this.failedScan = failedScan;
    }

    public List<String> getAfterScan() {
        return afterScan;
    }

    public void setAfterScan(List<String> afterScan) {
        this.afterScan = afterScan;
    }

    public List<String> getBeforeScan() {
        return beforeScan;
    }

    public void setBeforeScan(List<String> beforeScan) {
        this.beforeScan = beforeScan;
    }

    public List<String> getFailedScan() {
        return failedScan;
    }

    public void setFailedScan(List<String> failedScan) {
        this.failedScan = failedScan;
    }

    public String toString() {
        return "CxScanSettings.EmailNotifications(afterScan=" + this.afterScan + ", beforeScan=" +
                this.beforeScan + ", failedScan=" + this.failedScan + ")";
    }
}
