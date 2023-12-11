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
 * <p>Java class for UserData complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UserData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}WebClientUser">
 *       &lt;sequence>
 *         &lt;element name="IsActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RoleData" type="{http://Checkmarx.com}Role" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserData", propOrder = {
    "isActive",
    "roleData"
})
public class UserData
    extends WebClientUser
{

    @XmlElement(name = "IsActive")
    protected boolean isActive;
    @XmlElement(name = "RoleData")
    protected Role roleData;

    /**
     * Gets the value of the isActive property.
     *
     */
    public boolean isIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     *
     */
    public void setIsActive(boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the roleData property.
     *
     * @return
     *     possible object is
     *     {@link Role }
     *
     */
    public Role getRoleData() {
        return roleData;
    }

    /**
     * Sets the value of the roleData property.
     *
     * @param value
     *     allowed object is
     *     {@link Role }
     *
     */
    public void setRoleData(Role value) {
        this.roleData = value;
    }

}
