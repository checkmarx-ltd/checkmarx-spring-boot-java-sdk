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
 * <p>Java class for UserPermission complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UserPermission">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IsAllowedToDelete" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsAllowedToDuplicate" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsAllowedToRun" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsAllowedToUpdate" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserPermission", propOrder = {
    "isAllowedToDelete",
    "isAllowedToDuplicate",
    "isAllowedToRun",
    "isAllowedToUpdate"
})
public class UserPermission {

    @XmlElement(name = "IsAllowedToDelete")
    protected boolean isAllowedToDelete;
    @XmlElement(name = "IsAllowedToDuplicate")
    protected boolean isAllowedToDuplicate;
    @XmlElement(name = "IsAllowedToRun")
    protected boolean isAllowedToRun;
    @XmlElement(name = "IsAllowedToUpdate")
    protected boolean isAllowedToUpdate;

    /**
     * Gets the value of the isAllowedToDelete property.
     *
     */
    public boolean isIsAllowedToDelete() {
        return isAllowedToDelete;
    }

    /**
     * Sets the value of the isAllowedToDelete property.
     *
     */
    public void setIsAllowedToDelete(boolean value) {
        this.isAllowedToDelete = value;
    }

    /**
     * Gets the value of the isAllowedToDuplicate property.
     *
     */
    public boolean isIsAllowedToDuplicate() {
        return isAllowedToDuplicate;
    }

    /**
     * Sets the value of the isAllowedToDuplicate property.
     *
     */
    public void setIsAllowedToDuplicate(boolean value) {
        this.isAllowedToDuplicate = value;
    }

    /**
     * Gets the value of the isAllowedToRun property.
     *
     */
    public boolean isIsAllowedToRun() {
        return isAllowedToRun;
    }

    /**
     * Sets the value of the isAllowedToRun property.
     *
     */
    public void setIsAllowedToRun(boolean value) {
        this.isAllowedToRun = value;
    }

    /**
     * Gets the value of the isAllowedToUpdate property.
     *
     */
    public boolean isIsAllowedToUpdate() {
        return isAllowedToUpdate;
    }

    /**
     * Sets the value of the isAllowedToUpdate property.
     *
     */
    public void setIsAllowedToUpdate(boolean value) {
        this.isAllowedToUpdate = value;
    }

}
