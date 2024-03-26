package com.checkmarx.sdk.utils;

public class SASTScanReport {

    private int scanId;
    private boolean hasFindings;

    public SASTScanReport(int scanId, boolean hasFindings) {
        this.scanId = scanId;
        this.hasFindings = hasFindings;
    }

    public int getScanId() {
        return scanId;
    }

    public void setScanId(int scanId) {
        this.scanId = scanId;
    }

    public boolean isHasFindings() {
        return hasFindings;
    }

    public void setHasFindings(boolean hasFindings) {
        this.hasFindings = hasFindings;
    }
}
