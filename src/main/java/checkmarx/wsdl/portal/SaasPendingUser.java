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
 * <p>Java class for SaasPendingUser complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SaasPendingUser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CompanyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Industry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ActivationToken" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Languages" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="PackageId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="CreatedDate" type="{http://Checkmarx.com}CxDateTime" minOccurs="0"/>
 *         &lt;element name="IsActivated" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SaasPendingUser", propOrder = {
    "id",
    "firstName",
    "lastName",
    "password",
    "userName",
    "phone",
    "companyName",
    "industry",
    "activationToken",
    "languages",
    "packageId",
    "createdDate",
    "isActivated"
})
public class SaasPendingUser {

    @XmlElement(name = "ID")
    protected long id;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "UserName")
    protected String userName;
    @XmlElement(name = "Phone")
    protected String phone;
    @XmlElement(name = "CompanyName")
    protected String companyName;
    @XmlElement(name = "Industry")
    protected String industry;
    @XmlElement(name = "ActivationToken")
    protected String activationToken;
    @XmlElement(name = "Languages")
    protected long languages;
    @XmlElement(name = "PackageId")
    protected long packageId;
    @XmlElement(name = "CreatedDate")
    protected CxDateTime createdDate;
    @XmlElement(name = "IsActivated")
    protected boolean isActivated;

    /**
     * Gets the value of the id property.
     *
     */
    public long getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     */
    public void setID(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the firstName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the password property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the userName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the phone property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the companyName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the value of the companyName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCompanyName(String value) {
        this.companyName = value;
    }

    /**
     * Gets the value of the industry property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndustry() {
        return industry;
    }

    /**
     * Sets the value of the industry property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndustry(String value) {
        this.industry = value;
    }

    /**
     * Gets the value of the activationToken property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getActivationToken() {
        return activationToken;
    }

    /**
     * Sets the value of the activationToken property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setActivationToken(String value) {
        this.activationToken = value;
    }

    /**
     * Gets the value of the languages property.
     *
     */
    public long getLanguages() {
        return languages;
    }

    /**
     * Sets the value of the languages property.
     *
     */
    public void setLanguages(long value) {
        this.languages = value;
    }

    /**
     * Gets the value of the packageId property.
     *
     */
    public long getPackageId() {
        return packageId;
    }

    /**
     * Sets the value of the packageId property.
     *
     */
    public void setPackageId(long value) {
        this.packageId = value;
    }

    /**
     * Gets the value of the createdDate property.
     *
     * @return
     *     possible object is
     *     {@link CxDateTime }
     *
     */
    public CxDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the value of the createdDate property.
     *
     * @param value
     *     allowed object is
     *     {@link CxDateTime }
     *
     */
    public void setCreatedDate(CxDateTime value) {
        this.createdDate = value;
    }

    /**
     * Gets the value of the isActivated property.
     *
     */
    public boolean isIsActivated() {
        return isActivated;
    }

    /**
     * Sets the value of the isActivated property.
     *
     */
    public void setIsActivated(boolean value) {
        this.isActivated = value;
    }

}
