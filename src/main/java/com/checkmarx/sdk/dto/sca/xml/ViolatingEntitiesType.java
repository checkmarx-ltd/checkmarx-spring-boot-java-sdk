//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.02.25 at 03:28:03 AM PST 
//


package com.checkmarx.sdk.dto.sca.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ViolatingEntitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ViolatingEntitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="conditionCategory" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reportId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="conditionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ViolatingEntitiesType", propOrder = {
    "id",
    "conditionCategory",
    "reportId",
    "conditionId"
})
public class ViolatingEntitiesType {

    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String conditionCategory;
    @XmlElement(required = true)
    protected String reportId;
    @XmlElement(required = true)
    protected String conditionId;

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
     * Gets the value of the conditionCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConditionCategory() {
        return conditionCategory;
    }

    /**
     * Sets the value of the conditionCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConditionCategory(String value) {
        this.conditionCategory = value;
    }

    /**
     * Gets the value of the reportId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * Sets the value of the reportId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportId(String value) {
        this.reportId = value;
    }

    /**
     * Gets the value of the conditionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConditionId() {
        return conditionId;
    }

    /**
     * Sets the value of the conditionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConditionId(String value) {
        this.conditionId = value;
    }

}
