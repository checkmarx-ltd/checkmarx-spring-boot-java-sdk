//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.06.07 at 11:08:45 PM EST
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
 *         &lt;element name="ApprovePendingUsersResult" type="{http://Checkmarx.com}CxWSBasicRepsonse" minOccurs="0"/>
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
    "approvePendingUsersResult"
})
@XmlRootElement(name = "ApprovePendingUsersResponse")
public class ApprovePendingUsersResponse {

    @XmlElement(name = "ApprovePendingUsersResult")
    protected CxWSBasicRepsonse approvePendingUsersResult;

    /**
     * Gets the value of the approvePendingUsersResult property.
     *
     * @return
     *     possible object is
     *     {@link CxWSBasicRepsonse }
     *
     */
    public CxWSBasicRepsonse getApprovePendingUsersResult() {
        return approvePendingUsersResult;
    }

    /**
     * Sets the value of the approvePendingUsersResult property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWSBasicRepsonse }
     *
     */
    public void setApprovePendingUsersResult(CxWSBasicRepsonse value) {
        this.approvePendingUsersResult = value;
    }

}
