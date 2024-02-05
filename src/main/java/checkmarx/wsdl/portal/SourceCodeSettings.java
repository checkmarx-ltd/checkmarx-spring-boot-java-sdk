//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SourceCodeSettings complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SourceCodeSettings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SourceOrigin" type="{http://Checkmarx.com}SourceLocationType"/>
 *         &lt;element name="UserCredentials" type="{http://Checkmarx.com}Credentials" minOccurs="0"/>
 *         &lt;element name="PathList" type="{http://Checkmarx.com}ArrayOfScanPath" minOccurs="0"/>
 *         &lt;element name="SourceControlSetting" type="{http://Checkmarx.com}SourceControlSettings" minOccurs="0"/>
 *         &lt;element name="PackagedCode" type="{http://Checkmarx.com}LocalCodeContainer" minOccurs="0"/>
 *         &lt;element name="SourcePullingAction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SourceFilterLists" type="{http://Checkmarx.com}SourceFilterPatterns" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourceCodeSettings", propOrder = {
    "sourceOrigin",
    "userCredentials",
    "pathList",
    "sourceControlSetting",
    "packagedCode",
    "sourcePullingAction",
    "sourceFilterLists"
})
public class SourceCodeSettings {

    @XmlElement(name = "SourceOrigin", required = true)
    protected SourceLocationType sourceOrigin;
    @XmlElement(name = "UserCredentials")
    protected Credentials userCredentials;
    @XmlElement(name = "PathList")
    protected ArrayOfScanPath pathList;
    @XmlElement(name = "SourceControlSetting")
    protected SourceControlSettings sourceControlSetting;
    @XmlElement(name = "PackagedCode")
    protected LocalCodeContainer packagedCode;
    @XmlElement(name = "SourcePullingAction")
    protected String sourcePullingAction;
    @XmlElement(name = "SourceFilterLists")
    protected SourceFilterPatterns sourceFilterLists;

    /**
     * Gets the value of the sourceOrigin property.
     *
     * @return
     *     possible object is
     *     {@link SourceLocationType }
     *
     */
    public SourceLocationType getSourceOrigin() {
        return sourceOrigin;
    }

    /**
     * Sets the value of the sourceOrigin property.
     *
     * @param value
     *     allowed object is
     *     {@link SourceLocationType }
     *
     */
    public void setSourceOrigin(SourceLocationType value) {
        this.sourceOrigin = value;
    }

    /**
     * Gets the value of the userCredentials property.
     *
     * @return
     *     possible object is
     *     {@link Credentials }
     *
     */
    public Credentials getUserCredentials() {
        return userCredentials;
    }

    /**
     * Sets the value of the userCredentials property.
     *
     * @param value
     *     allowed object is
     *     {@link Credentials }
     *
     */
    public void setUserCredentials(Credentials value) {
        this.userCredentials = value;
    }

    /**
     * Gets the value of the pathList property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfScanPath }
     *
     */
    public ArrayOfScanPath getPathList() {
        return pathList;
    }

    /**
     * Sets the value of the pathList property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfScanPath }
     *
     */
    public void setPathList(ArrayOfScanPath value) {
        this.pathList = value;
    }

    /**
     * Gets the value of the sourceControlSetting property.
     *
     * @return
     *     possible object is
     *     {@link SourceControlSettings }
     *
     */
    public SourceControlSettings getSourceControlSetting() {
        return sourceControlSetting;
    }

    /**
     * Sets the value of the sourceControlSetting property.
     *
     * @param value
     *     allowed object is
     *     {@link SourceControlSettings }
     *
     */
    public void setSourceControlSetting(SourceControlSettings value) {
        this.sourceControlSetting = value;
    }

    /**
     * Gets the value of the packagedCode property.
     *
     * @return
     *     possible object is
     *     {@link LocalCodeContainer }
     *
     */
    public LocalCodeContainer getPackagedCode() {
        return packagedCode;
    }

    /**
     * Sets the value of the packagedCode property.
     *
     * @param value
     *     allowed object is
     *     {@link LocalCodeContainer }
     *
     */
    public void setPackagedCode(LocalCodeContainer value) {
        this.packagedCode = value;
    }

    /**
     * Gets the value of the sourcePullingAction property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSourcePullingAction() {
        return sourcePullingAction;
    }

    /**
     * Sets the value of the sourcePullingAction property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSourcePullingAction(String value) {
        this.sourcePullingAction = value;
    }

    /**
     * Gets the value of the sourceFilterLists property.
     *
     * @return
     *     possible object is
     *     {@link SourceFilterPatterns }
     *
     */
    public SourceFilterPatterns getSourceFilterLists() {
        return sourceFilterLists;
    }

    /**
     * Sets the value of the sourceFilterLists property.
     *
     * @param value
     *     allowed object is
     *     {@link SourceFilterPatterns }
     *
     */
    public void setSourceFilterLists(SourceFilterPatterns value) {
        this.sourceFilterLists = value;
    }

}
