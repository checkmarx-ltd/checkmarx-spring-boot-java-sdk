package com.checkmarx.sdk.dto.sca.report;


import java.io.Serializable;

public class ScaSummaryBaseFormat implements Serializable {
    private int totalPackages;
    private int directPackages;
    private String createdOn;
    private double riskScore;
    private int totalOutdatedPackages;
    private int criticalVulnerabilityCount = 0;
    private int highVulnerabilityCount = 0;
    private int mediumVulnerabilityCount = 0;
    private int lowVulnerabilityCount = 0;

    public ScaSummaryBaseFormat() {
    }

    public ScaSummaryBaseFormat(int totalPackages, int directPackages, String createdOn, double riskScore, int totalOutdatedPackages, int criticalVulnerabilityCount,int highVulnerabilityCount, int mediumVulnerabilityCount, int lowVulnerabilityCount) {
        this.totalPackages = totalPackages;
        this.directPackages = directPackages;
        this.createdOn = createdOn;
        this.riskScore = riskScore;
        this.totalOutdatedPackages = totalOutdatedPackages;
        this.criticalVulnerabilityCount = criticalVulnerabilityCount;
        this.highVulnerabilityCount = highVulnerabilityCount;
        this.mediumVulnerabilityCount = mediumVulnerabilityCount;
        this.lowVulnerabilityCount = lowVulnerabilityCount;
    }

    public int getTotalOkLibraries() {
        int totalOk = (totalPackages - (criticalVulnerabilityCount+highVulnerabilityCount + mediumVulnerabilityCount + lowVulnerabilityCount));
        totalOk = Math.max(totalOk, 0);
        return totalOk;
    }

    public int getTotalPackages() {
        return totalPackages;
    }

    public void setTotalPackages(int totalPackages) {
        this.totalPackages = totalPackages;
    }

    public int getDirectPackages() {
        return directPackages;
    }

    public void setDirectPackages(int directPackages) {
        this.directPackages = directPackages;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public int getTotalOutdatedPackages() {
        return totalOutdatedPackages;
    }

    public void setTotalOutdatedPackages(int totalOutdatedPackages) {
        this.totalOutdatedPackages = totalOutdatedPackages;
    }

    public int getHighVulnerabilityCount() {
        return highVulnerabilityCount;
    }

    public void setHighVulnerabilityCount(int highVulnerabilityCount) {
        this.highVulnerabilityCount = highVulnerabilityCount;
    }

    public int getMediumVulnerabilityCount() {
        return mediumVulnerabilityCount;
    }

    public void setMediumVulnerabilityCount(int mediumVulnerabilityCount) {
        this.mediumVulnerabilityCount = mediumVulnerabilityCount;
    }

    public int getLowVulnerabilityCount() {
        return lowVulnerabilityCount;
    }

    public void setLowVulnerabilityCount(int lowVulnerabilityCount) {
        this.lowVulnerabilityCount = lowVulnerabilityCount;
    }
    public int getCriticalVulnerabilityCount() {
        return criticalVulnerabilityCount;
    }

    public void setCriticalVulnerabilityCount(int criticalVulnerabilityCount) {
        this.criticalVulnerabilityCount = criticalVulnerabilityCount;
    }
}
