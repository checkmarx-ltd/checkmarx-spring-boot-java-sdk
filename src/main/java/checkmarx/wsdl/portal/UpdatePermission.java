//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
 *         &lt;element name="permission" type="{http://Checkmarx.com}CxPermission" minOccurs="0"/>
 *         &lt;element name="teamId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "permission",
    "teamId"
})
@XmlRootElement(name = "UpdatePermission")
public class UpdatePermission {

    protected String sessionID;
    protected CxPermission permission;
    protected String teamId;

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
     * Gets the value of the permission property.
     *
     * @return
     *     possible object is
     *     {@link CxPermission }
     *
     */
    public CxPermission getPermission() {
        return permission;
    }

    /**
     * Sets the value of the permission property.
     *
     * @param value
     *     allowed object is
     *     {@link CxPermission }
     *
     */
    public void setPermission(CxPermission value) {
        this.permission = value;
    }

    /**
     * Gets the value of the teamId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTeamId() {
        return teamId;
    }

    /**
     * Sets the value of the teamId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTeamId(String value) {
        this.teamId = value;
    }

}
