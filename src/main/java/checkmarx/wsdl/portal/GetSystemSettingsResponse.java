//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetSystemSettingsResult" type="{http://Checkmarx.com}CxWsResponseSystemSettings" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getSystemSettingsResult"
})
@XmlRootElement(name = "GetSystemSettingsResponse")
public class GetSystemSettingsResponse {

    @XmlElement(name = "GetSystemSettingsResult")
    protected CxWsResponseSystemSettings getSystemSettingsResult;

    /**
     * Gets the value of the getSystemSettingsResult property.
     *
     * @return
     *     possible object is
     *     {@link CxWsResponseSystemSettings }
     *
     */
    public CxWsResponseSystemSettings getGetSystemSettingsResult() {
        return getSystemSettingsResult;
    }

    /**
     * Sets the value of the getSystemSettingsResult property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWsResponseSystemSettings }
     *
     */
    public void setGetSystemSettingsResult(CxWsResponseSystemSettings value) {
        this.getSystemSettingsResult = value;
    }

}
