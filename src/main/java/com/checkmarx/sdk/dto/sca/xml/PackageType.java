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
 * <p>Java class for PackageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Licenses" type="{}LicensesType"/>
 *         &lt;element name="MatchType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HighVulnerabilityCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="MediumVulnerabilityCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="LowVulnerabilityCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="NumberOfVersionsSinceLastUpdate" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="NewestVersionReleaseDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NewestVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Outdated" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ReleaseDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="RiskScore" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Severity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Locations" type="{}LocationsType"/>
 *         &lt;element name="PackageRepository" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsMalicious" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsDirectDependency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsDevelopmentDependency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsTestDependency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsViolatingPolicy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="UsageType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VulnerabilityCount" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageType", propOrder = {
    "id",
    "name",
    "version",
    "licenses",
    "matchType",
    "highVulnerabilityCount",
    "mediumVulnerabilityCount",
    "lowVulnerabilityCount",
    "numberOfVersionsSinceLastUpdate",
    "newestVersionReleaseDate",
    "newestVersion",
    "outdated",
    "releaseDate",
    "riskScore",
    "severity",
    "locations",
    "packageRepository",
    "isMalicious",
    "isDirectDependency",
    "isDevelopmentDependency",
    "isTestDependency",
    "isViolatingPolicy",
    "usageType",
    "vulnerabilityCount"
})
public class PackageType {

    @XmlElement(name = "Id", required = true)
    protected String id;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Version", required = true)
    protected String version;
    @XmlElement(name = "Licenses", required = true)
    protected LicensesType licenses;
    @XmlElement(name = "MatchType", required = true)
    protected String matchType;
    @XmlElement(name = "HighVulnerabilityCount")
    protected byte highVulnerabilityCount;
    @XmlElement(name = "MediumVulnerabilityCount")
    protected byte mediumVulnerabilityCount;
    @XmlElement(name = "LowVulnerabilityCount")
    protected byte lowVulnerabilityCount;
    @XmlElement(name = "NumberOfVersionsSinceLastUpdate")
    protected byte numberOfVersionsSinceLastUpdate;
    @XmlElement(name = "NewestVersionReleaseDate", required = true)
    protected String newestVersionReleaseDate;
    @XmlElement(name = "NewestVersion", required = true)
    protected String newestVersion;
    @XmlElement(name = "Outdated", required = true)
    protected String outdated;
    @XmlElement(name = "ReleaseDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar releaseDate;
    @XmlElement(name = "RiskScore")
    protected float riskScore;
    @XmlElement(name = "Severity", required = true)
    protected String severity;
    @XmlElement(name = "Locations", required = true)
    protected LocationsType locations;
    @XmlElement(name = "PackageRepository", required = true)
    protected String packageRepository;
    @XmlElement(name = "IsMalicious", required = true)
    protected String isMalicious;
    @XmlElement(name = "IsDirectDependency", required = true)
    protected String isDirectDependency;
    @XmlElement(name = "IsDevelopmentDependency", required = true)
    protected String isDevelopmentDependency;
    @XmlElement(name = "IsTestDependency", required = true)
    protected String isTestDependency;
    @XmlElement(name = "IsViolatingPolicy", required = true)
    protected String isViolatingPolicy;
    @XmlElement(name = "UsageType", required = true)
    protected String usageType;
    @XmlElement(name = "VulnerabilityCount")
    protected byte vulnerabilityCount;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the licenses property.
     * 
     * @return
     *     possible object is
     *     {@link LicensesType }
     *     
     */
    public LicensesType getLicenses() {
        return licenses;
    }

    /**
     * Sets the value of the licenses property.
     * 
     * @param value
     *     allowed object is
     *     {@link LicensesType }
     *     
     */
    public void setLicenses(LicensesType value) {
        this.licenses = value;
    }

    /**
     * Gets the value of the matchType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatchType() {
        return matchType;
    }

    /**
     * Sets the value of the matchType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatchType(String value) {
        this.matchType = value;
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
     * Gets the value of the numberOfVersionsSinceLastUpdate property.
     * 
     */
    public byte getNumberOfVersionsSinceLastUpdate() {
        return numberOfVersionsSinceLastUpdate;
    }

    /**
     * Sets the value of the numberOfVersionsSinceLastUpdate property.
     * 
     */
    public void setNumberOfVersionsSinceLastUpdate(byte value) {
        this.numberOfVersionsSinceLastUpdate = value;
    }

    /**
     * Gets the value of the newestVersionReleaseDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewestVersionReleaseDate() {
        return newestVersionReleaseDate;
    }

    /**
     * Sets the value of the newestVersionReleaseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewestVersionReleaseDate(String value) {
        this.newestVersionReleaseDate = value;
    }

    /**
     * Gets the value of the newestVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewestVersion() {
        return newestVersion;
    }

    /**
     * Sets the value of the newestVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewestVersion(String value) {
        this.newestVersion = value;
    }

    /**
     * Gets the value of the outdated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutdated() {
        return outdated;
    }

    /**
     * Sets the value of the outdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutdated(String value) {
        this.outdated = value;
    }

    /**
     * Gets the value of the releaseDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the value of the releaseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReleaseDate(XMLGregorianCalendar value) {
        this.releaseDate = value;
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
     * Gets the value of the severity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeverity(String value) {
        this.severity = value;
    }

    /**
     * Gets the value of the locations property.
     * 
     * @return
     *     possible object is
     *     {@link LocationsType }
     *     
     */
    public LocationsType getLocations() {
        return locations;
    }

    /**
     * Sets the value of the locations property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationsType }
     *     
     */
    public void setLocations(LocationsType value) {
        this.locations = value;
    }

    /**
     * Gets the value of the packageRepository property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackageRepository() {
        return packageRepository;
    }

    /**
     * Sets the value of the packageRepository property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackageRepository(String value) {
        this.packageRepository = value;
    }

    /**
     * Gets the value of the isMalicious property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsMalicious() {
        return isMalicious;
    }

    /**
     * Sets the value of the isMalicious property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsMalicious(String value) {
        this.isMalicious = value;
    }

    /**
     * Gets the value of the isDirectDependency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsDirectDependency() {
        return isDirectDependency;
    }

    /**
     * Sets the value of the isDirectDependency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsDirectDependency(String value) {
        this.isDirectDependency = value;
    }

    /**
     * Gets the value of the isDevelopmentDependency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsDevelopmentDependency() {
        return isDevelopmentDependency;
    }

    /**
     * Sets the value of the isDevelopmentDependency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsDevelopmentDependency(String value) {
        this.isDevelopmentDependency = value;
    }

    /**
     * Gets the value of the isTestDependency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsTestDependency() {
        return isTestDependency;
    }

    /**
     * Sets the value of the isTestDependency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsTestDependency(String value) {
        this.isTestDependency = value;
    }

    /**
     * Gets the value of the isViolatingPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsViolatingPolicy() {
        return isViolatingPolicy;
    }

    /**
     * Sets the value of the isViolatingPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsViolatingPolicy(String value) {
        this.isViolatingPolicy = value;
    }

    /**
     * Gets the value of the usageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsageType() {
        return usageType;
    }

    /**
     * Sets the value of the usageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsageType(String value) {
        this.usageType = value;
    }

    /**
     * Gets the value of the vulnerabilityCount property.
     * 
     */
    public byte getVulnerabilityCount() {
        return vulnerabilityCount;
    }

    /**
     * Sets the value of the vulnerabilityCount property.
     * 
     */
    public void setVulnerabilityCount(byte value) {
        this.vulnerabilityCount = value;
    }

}
