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
 *         &lt;element name="GetAllUsersInGroupResult" type="{http://Checkmarx.com}CxWSResponseUserData" minOccurs="0"/>
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
    "getAllUsersInGroupResult"
})
@XmlRootElement(name = "GetAllUsersInGroupResponse")
public class GetAllUsersInGroupResponse {

    @XmlElement(name = "GetAllUsersInGroupResult")
    protected CxWSResponseUserData getAllUsersInGroupResult;

    /**
     * Gets the value of the getAllUsersInGroupResult property.
     *
     * @return
     *     possible object is
     *     {@link CxWSResponseUserData }
     *
     */
    public CxWSResponseUserData getGetAllUsersInGroupResult() {
        return getAllUsersInGroupResult;
    }

    /**
     * Sets the value of the getAllUsersInGroupResult property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWSResponseUserData }
     *
     */
    public void setGetAllUsersInGroupResult(CxWSResponseUserData value) {
        this.getAllUsersInGroupResult = value;
    }

}
