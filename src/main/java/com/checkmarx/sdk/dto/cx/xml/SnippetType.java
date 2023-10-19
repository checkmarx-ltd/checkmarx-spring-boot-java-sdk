package com.checkmarx.sdk.dto.cx.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SnippetType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SnippetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Line" type="{}LineType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SnippetType", propOrder = {
    "line"
})
public class SnippetType {

    @XmlElement(name = "Line", required = true)
    protected LineType line;

    /**
     * Gets the value of the line property.
     *
     * @return
     *     possible object is
     *     {@link LineType }
     *
     */
    public LineType getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     *
     * @param value
     *     allowed object is
     *     {@link LineType }
     *
     */
    public void setLine(LineType value) {
        this.line = value;
    }

}
