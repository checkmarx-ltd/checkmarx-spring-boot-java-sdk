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
 * <p>Java class for ProjectScansResultSummary complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProjectScansResultSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="High" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Medium" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Low" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Info" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectScansResultSummary", propOrder = {
    "label",
    "high",
    "medium",
    "low",
    "info"
})
public class ProjectScansResultSummary {

    @XmlElement(name = "Label")
    protected String label;
    @XmlElement(name = "High")
    protected int high;
    @XmlElement(name = "Medium")
    protected int medium;
    @XmlElement(name = "Low")
    protected int low;
    @XmlElement(name = "Info")
    protected int info;

    /**
     * Gets the value of the label property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the high property.
     *
     */
    public int getHigh() {
        return high;
    }

    /**
     * Sets the value of the high property.
     *
     */
    public void setHigh(int value) {
        this.high = value;
    }

    /**
     * Gets the value of the medium property.
     *
     */
    public int getMedium() {
        return medium;
    }

    /**
     * Sets the value of the medium property.
     *
     */
    public void setMedium(int value) {
        this.medium = value;
    }

    /**
     * Gets the value of the low property.
     *
     */
    public int getLow() {
        return low;
    }

    /**
     * Sets the value of the low property.
     *
     */
    public void setLow(int value) {
        this.low = value;
    }

    /**
     * Gets the value of the info property.
     *
     */
    public int getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     *
     */
    public void setInfo(int value) {
        this.info = value;
    }

}
