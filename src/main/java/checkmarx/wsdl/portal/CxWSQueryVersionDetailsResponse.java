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
 * <p>Java class for CxWSQueryVersionDetailsResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSQueryVersionDetailsResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="QueryDetails" type="{http://Checkmarx.com}CxWSQueryVersionDetails" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSQueryVersionDetailsResponse", propOrder = {
    "queryDetails"
})
public class CxWSQueryVersionDetailsResponse
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "QueryDetails")
    protected CxWSQueryVersionDetails queryDetails;

    /**
     * Gets the value of the queryDetails property.
     *
     * @return
     *     possible object is
     *     {@link CxWSQueryVersionDetails }
     *
     */
    public CxWSQueryVersionDetails getQueryDetails() {
        return queryDetails;
    }

    /**
     * Sets the value of the queryDetails property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWSQueryVersionDetails }
     *
     */
    public void setQueryDetails(CxWSQueryVersionDetails value) {
        this.queryDetails = value;
    }

}
