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
 * <p>Java class for CxWSDisplayCategoriesFilter complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSDisplayCategoriesFilter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="All" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IDs" type="{http://Checkmarx.com}ArrayOfLong" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSDisplayCategoriesFilter", propOrder = {
    "all",
    "iDs"
})
public class CxWSDisplayCategoriesFilter {

    @XmlElement(name = "All")
    protected boolean all;
    @XmlElement(name = "IDs")
    protected ArrayOfLong iDs;

    /**
     * Gets the value of the all property.
     *
     */
    public boolean isAll() {
        return all;
    }

    /**
     * Sets the value of the all property.
     *
     */
    public void setAll(boolean value) {
        this.all = value;
    }

    /**
     * Gets the value of the iDs property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfLong }
     *
     */
    public ArrayOfLong getIDs() {
        return iDs;
    }

    /**
     * Sets the value of the iDs property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfLong }
     *
     */
    public void setIDs(ArrayOfLong value) {
        this.iDs = value;
    }

}
