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
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userData" type="{http://Checkmarx.com}UserData" minOccurs="0"/>
 *         &lt;element name="userType" type="{http://Checkmarx.com}CxUserTypes"/>
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
    "sessionID",
    "userData",
    "userType"
})
@XmlRootElement(name = "AddNewUser")
public class AddNewUser {

    protected String sessionID;
    protected UserData userData;
    @XmlElement(required = true)
    protected CxUserTypes userType;

    /**
     * Gets the value of the sessionID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the userData property.
     *
     * @return
     *     possible object is
     *     {@link UserData }
     *
     */
    public UserData getUserData() {
        return userData;
    }

    /**
     * Sets the value of the userData property.
     *
     * @param value
     *     allowed object is
     *     {@link UserData }
     *
     */
    public void setUserData(UserData value) {
        this.userData = value;
    }

    /**
     * Gets the value of the userType property.
     *
     * @return
     *     possible object is
     *     {@link CxUserTypes }
     *
     */
    public CxUserTypes getUserType() {
        return userType;
    }

    /**
     * Sets the value of the userType property.
     *
     * @param value
     *     allowed object is
     *     {@link CxUserTypes }
     *
     */
    public void setUserType(CxUserTypes value) {
        this.userType = value;
    }

}
