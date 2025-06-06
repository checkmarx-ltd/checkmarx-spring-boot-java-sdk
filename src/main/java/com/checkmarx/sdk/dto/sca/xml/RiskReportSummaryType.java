//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.02.25 at 03:28:03 AM PST 
//


package com.checkmarx.sdk.dto.sca.xml;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RiskReportSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RiskReportSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RiskReportId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProjectId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProjectName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProjectCreatedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="CriticalVulnerabilityCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="HighVulnerabilityCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="MediumVulnerabilityCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="LowVulnerabilityCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="TotalPackages" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="DirectPackages" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="CreatedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="RiskScore" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="TotalOutdatedPackages" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="VulnerablePackages" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="TotalPackagesWithLegalRisk" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="HighVulnerablePackages" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="MediumVulnerablePackages" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="LowVulnerablePackages" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="LicensesLegalRisk" type="{}LicensesLegalRiskType"/>
 *         &lt;element name="ScanOrigin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExploitablePathEnabled" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExploitablePathsFound" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="HasRemediationRecommendation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BuildBreakerPolicies" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="ProjectPolicies" type="{}ProjectPoliciesType"/>
 *         &lt;element name="ViolatingPoliciesCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RiskReportSummaryType", propOrder = {
    "riskReportId",
    "projectId",
    "projectName",
    "projectCreatedOn",
    "criticalVulnerabilityCount",
    "highVulnerabilityCount",
    "mediumVulnerabilityCount",
    "lowVulnerabilityCount",
    "totalPackages",
    "directPackages",
    "createdOn",
    "riskScore",
    "totalOutdatedPackages",
    "vulnerablePackages",
    "totalPackagesWithLegalRisk", "criticalVulnerablePackages",
    "highVulnerablePackages",
    "mediumVulnerablePackages",
    "lowVulnerablePackages",
    "licensesLegalRisk",
    "scanOrigin",
    "exploitablePathEnabled",
    "exploitablePathsFound",
    "hasRemediationRecommendation",
    "buildBreakerPolicies",
    "projectPolicies",
    "violatingPoliciesCount"
})
public class RiskReportSummaryType {

    @XmlElement(name = "RiskReportId", required = true)
    protected String riskReportId;
    @XmlElement(name = "ProjectId", required = true)
    protected String projectId;
    @XmlElement(name = "ProjectName", required = true)
    protected String projectName;
    @XmlElement(name = "ProjectCreatedOn", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar projectCreatedOn;
    @XmlElement(name = "CriticalVulnerabilityCount")
    protected byte criticalVulnerabilityCount;
    @XmlElement(name = "HighVulnerabilityCount")
    protected byte highVulnerabilityCount;
    @XmlElement(name = "MediumVulnerabilityCount")
    protected byte mediumVulnerabilityCount;
    @XmlElement(name = "LowVulnerabilityCount")
    protected byte lowVulnerabilityCount;
    @XmlElement(name = "TotalPackages")
    protected short totalPackages;
    @XmlElement(name = "DirectPackages")
    protected byte directPackages;
    @XmlElement(name = "CreatedOn", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdOn;
    @XmlElement(name = "RiskScore")
    protected float riskScore;
    @XmlElement(name = "TotalOutdatedPackages")
    protected short totalOutdatedPackages;
    @XmlElement(name = "VulnerablePackages")
    protected byte vulnerablePackages;
    @XmlElement(name = "TotalPackagesWithLegalRisk")
    protected byte totalPackagesWithLegalRisk;
    @XmlElement(name = "CriticalVulnerablePackages")
    protected byte criticalVulnerablePackages;
    @XmlElement(name = "HighVulnerablePackages")
    protected byte highVulnerablePackages;
    @XmlElement(name = "MediumVulnerablePackages")
    protected byte mediumVulnerablePackages;
    @XmlElement(name = "LowVulnerablePackages")
    protected byte lowVulnerablePackages;
    @XmlElement(name = "LicensesLegalRisk", required = true)
    protected LicensesLegalRiskType licensesLegalRisk;
    @XmlElement(name = "ScanOrigin", required = true)
    protected String scanOrigin;
    @XmlElement(name = "ExploitablePathEnabled", required = true)
    protected String exploitablePathEnabled;
    @XmlElement(name = "ExploitablePathsFound")
    protected byte exploitablePathsFound;
    @XmlElement(name = "HasRemediationRecommendation", required = true)
    protected String hasRemediationRecommendation;
    @XmlElement(name = "BuildBreakerPolicies")
    protected byte buildBreakerPolicies;
    @XmlElement(name = "ProjectPolicies", required = true)
    protected ProjectPoliciesType projectPolicies;
    @XmlElement(name = "ViolatingPoliciesCount")
    protected byte violatingPoliciesCount;

    /**
     * Gets the value of the riskReportId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiskReportId() {
        return riskReportId;
    }

    /**
     * Sets the value of the riskReportId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiskReportId(String value) {
        this.riskReportId = value;
    }

    /**
     * Gets the value of the projectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Sets the value of the projectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectId(String value) {
        this.projectId = value;
    }

    /**
     * Gets the value of the projectName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the value of the projectName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectName(String value) {
        this.projectName = value;
    }

    /**
     * Gets the value of the projectCreatedOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getProjectCreatedOn() {
        return projectCreatedOn;
    }

    /**
     * Sets the value of the projectCreatedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setProjectCreatedOn(XMLGregorianCalendar value) {
        this.projectCreatedOn = value;
    }

    /**
     * Gets the value of the highVulnerabilityCount property.
     * 
     */
    public byte getHighVulnerabilityCount() {
        return highVulnerabilityCount;
    }

    /**
     * Sets the value of the highVulnerabilityCount property.
     * 
     */
    public void setHighVulnerabilityCount(byte value) {
        this.highVulnerabilityCount = value;
    }

    /**
     * Gets the value of the mediumVulnerabilityCount property.
     * 
     */
    public byte getMediumVulnerabilityCount() {
        return mediumVulnerabilityCount;
    }

    /**
     * Sets the value of the mediumVulnerabilityCount property.
     * 
     */
    public void setMediumVulnerabilityCount(byte value) {
        this.mediumVulnerabilityCount = value;
    }

    /**
     * Gets the value of the lowVulnerabilityCount property.
     * 
     */
    public byte getLowVulnerabilityCount() {
        return lowVulnerabilityCount;
    }

    /**
     * Sets the value of the lowVulnerabilityCount property.
     * 
     */
    public void setLowVulnerabilityCount(byte value) {
        this.lowVulnerabilityCount = value;
    }

    /**
     * Gets the value of the totalPackages property.
     * 
     */
    public short getTotalPackages() {
        return totalPackages;
    }

    /**
     * Sets the value of the totalPackages property.
     * 
     */
    public void setTotalPackages(short value) {
        this.totalPackages = value;
    }

    /**
     * Gets the value of the directPackages property.
     * 
     */
    public byte getDirectPackages() {
        return directPackages;
    }

    /**
     * Sets the value of the directPackages property.
     * 
     */
    public void setDirectPackages(byte value) {
        this.directPackages = value;
    }

    /**
     * Gets the value of the createdOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatedOn() {
        return createdOn;
    }

    /**
     * Sets the value of the createdOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatedOn(XMLGregorianCalendar value) {
        this.createdOn = value;
    }

    /**
     * Gets the value of the riskScore property.
     * 
     */
    public float getRiskScore() {
        return riskScore;
    }

    /**
     * Sets the value of the riskScore property.
     * 
     */
    public void setRiskScore(float value) {
        this.riskScore = value;
    }

    /**
     * Gets the value of the totalOutdatedPackages property.
     * 
     */
    public short getTotalOutdatedPackages() {
        return totalOutdatedPackages;
    }

    /**
     * Sets the value of the totalOutdatedPackages property.
     * 
     */
    public void setTotalOutdatedPackages(short value) {
        this.totalOutdatedPackages = value;
    }

    /**
     * Gets the value of the vulnerablePackages property.
     * 
     */
    public byte getVulnerablePackages() {
        return vulnerablePackages;
    }

    /**
     * Sets the value of the vulnerablePackages property.
     * 
     */
    public void setVulnerablePackages(byte value) {
        this.vulnerablePackages = value;
    }

    /**
     * Gets the value of the totalPackagesWithLegalRisk property.
     * 
     */
    public byte getTotalPackagesWithLegalRisk() {
        return totalPackagesWithLegalRisk;
    }

    /**
     * Sets the value of the totalPackagesWithLegalRisk property.
     * 
     */
    public void setTotalPackagesWithLegalRisk(byte value) {
        this.totalPackagesWithLegalRisk = value;
    }

    /**
     * Gets the value of the highVulnerablePackages property.
     * 
     */
    public byte getHighVulnerablePackages() {
        return highVulnerablePackages;
    }

    public byte getCriticalVulnerabilityCount() {
        return criticalVulnerabilityCount;
    }

    public void setCriticalVulnerabilityCount(byte criticalVulnerabilityCount) {
        this.criticalVulnerabilityCount = criticalVulnerabilityCount;
    }

    /**
     * Sets the value of the highVulnerablePackages property.
     * 
     */
    public void setHighVulnerablePackages(byte value) {
        this.highVulnerablePackages = value;
    }

    /**
     * Gets the value of the mediumVulnerablePackages property.
     * 
     */
    public byte getMediumVulnerablePackages() {
        return mediumVulnerablePackages;
    }

    /**
     * Sets the value of the mediumVulnerablePackages property.
     * 
     */
    public void setMediumVulnerablePackages(byte value) {
        this.mediumVulnerablePackages = value;
    }

    /**
     * Gets the value of the lowVulnerablePackages property.
     * 
     */
    public byte getLowVulnerablePackages() {
        return lowVulnerablePackages;
    }

    /**
     * Sets the value of the lowVulnerablePackages property.
     * 
     */
    public void setLowVulnerablePackages(byte value) {
        this.lowVulnerablePackages = value;
    }

    /**
     * Gets the value of the licensesLegalRisk property.
     * 
     * @return
     *     possible object is
     *     {@link LicensesLegalRiskType }
     *     
     */
    public LicensesLegalRiskType getLicensesLegalRisk() {
        return licensesLegalRisk;
    }

    /**
     * Sets the value of the licensesLegalRisk property.
     * 
     * @param value
     *     allowed object is
     *     {@link LicensesLegalRiskType }
     *     
     */
    public void setLicensesLegalRisk(LicensesLegalRiskType value) {
        this.licensesLegalRisk = value;
    }

    /**
     * Gets the value of the scanOrigin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScanOrigin() {
        return scanOrigin;
    }

    /**
     * Sets the value of the scanOrigin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScanOrigin(String value) {
        this.scanOrigin = value;
    }

    /**
     * Gets the value of the exploitablePathEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExploitablePathEnabled() {
        return exploitablePathEnabled;
    }

    /**
     * Sets the value of the exploitablePathEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExploitablePathEnabled(String value) {
        this.exploitablePathEnabled = value;
    }

    /**
     * Gets the value of the exploitablePathsFound property.
     * 
     */
    public byte getExploitablePathsFound() {
        return exploitablePathsFound;
    }

    /**
     * Sets the value of the exploitablePathsFound property.
     * 
     */
    public void setExploitablePathsFound(byte value) {
        this.exploitablePathsFound = value;
    }

    /**
     * Gets the value of the hasRemediationRecommendation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHasRemediationRecommendation() {
        return hasRemediationRecommendation;
    }

    /**
     * Sets the value of the hasRemediationRecommendation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHasRemediationRecommendation(String value) {
        this.hasRemediationRecommendation = value;
    }

    /**
     * Gets the value of the buildBreakerPolicies property.
     * 
     */
    public byte getBuildBreakerPolicies() {
        return buildBreakerPolicies;
    }

    /**
     * Sets the value of the buildBreakerPolicies property.
     * 
     */
    public void setBuildBreakerPolicies(byte value) {
        this.buildBreakerPolicies = value;
    }

    /**
     * Gets the value of the projectPolicies property.
     * 
     * @return
     *     possible object is
     *     {@link ProjectPoliciesType }
     *     
     */
    public ProjectPoliciesType getProjectPolicies() {
        return projectPolicies;
    }

    /**
     * Sets the value of the projectPolicies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProjectPoliciesType }
     *     
     */
    public void setProjectPolicies(ProjectPoliciesType value) {
        this.projectPolicies = value;
    }

    /**
     * Gets the value of the violatingPoliciesCount property.
     * 
     */
    public byte getViolatingPoliciesCount() {
        return violatingPoliciesCount;
    }

    /**
     * Sets the value of the violatingPoliciesCount property.
     * 
     */
    public void setViolatingPoliciesCount(byte value) {
        this.violatingPoliciesCount = value;
    }

}
