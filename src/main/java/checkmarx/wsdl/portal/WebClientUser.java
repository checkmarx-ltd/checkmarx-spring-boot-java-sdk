//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WebClientUser complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="WebClientUser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserPreferedLanguageLCID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JobTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UPN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CellPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Skype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CompanyID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CompanyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="willExpireAfterDays" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="AuditUser" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="GroupList" type="{http://Checkmarx.com}ArrayOfGroup" minOccurs="0"/>
 *         &lt;element name="LastLoginDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="LimitAccessByIPAddress" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AllowedIPs" type="{http://Checkmarx.com}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebClientUser", propOrder = {
    "id",
    "firstName",
    "lastName",
    "userPreferedLanguageLCID",
    "password",
    "jobTitle",
    "email",
    "userName",
    "upn",
    "phone",
    "cellPhone",
    "skype",
    "companyID",
    "companyName",
    "willExpireAfterDays",
    "country",
    "dateCreated",
    "auditUser",
    "groupList",
    "lastLoginDate",
    "limitAccessByIPAddress",
    "allowedIPs"
})
@XmlSeeAlso({
    UserData.class
})
public class WebClientUser {

    @XmlElement(name = "ID")
    protected long id;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "UserPreferedLanguageLCID")
    protected int userPreferedLanguageLCID;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "JobTitle")
    protected String jobTitle;
    @XmlElement(name = "Email")
    protected String email;
    @XmlElement(name = "UserName")
    protected String userName;
    @XmlElement(name = "UPN")
    protected String upn;
    @XmlElement(name = "Phone")
    protected String phone;
    @XmlElement(name = "CellPhone")
    protected String cellPhone;
    @XmlElement(name = "Skype")
    protected String skype;
    @XmlElement(name = "CompanyID")
    protected String companyID;
    @XmlElement(name = "CompanyName")
    protected String companyName;
    protected String willExpireAfterDays;
    protected String country;
    @XmlElement(name = "DateCreated", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    @XmlElement(name = "AuditUser")
    protected boolean auditUser;
    @XmlElement(name = "GroupList")
    protected ArrayOfGroup groupList;
    @XmlElement(name = "LastLoginDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastLoginDate;
    @XmlElement(name = "LimitAccessByIPAddress")
    protected boolean limitAccessByIPAddress;
    @XmlElement(name = "AllowedIPs")
    protected ArrayOfString allowedIPs;

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
     * Gets the value of the userPreferedLanguageLCID property.
     *
     */
    public int getUserPreferedLanguageLCID() {
        return userPreferedLanguageLCID;
    }

    /**
     * Sets the value of the userPreferedLanguageLCID property.
     *
     */
    public void setUserPreferedLanguageLCID(int value) {
        this.userPreferedLanguageLCID = value;
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
     * Gets the value of the jobTitle property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the value of the jobTitle property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setJobTitle(String value) {
        this.jobTitle = value;
    }

    /**
     * Gets the value of the email property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmail(String value) {
        this.email = value;
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
     * Gets the value of the upn property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUPN() {
        return upn;
    }

    /**
     * Sets the value of the upn property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUPN(String value) {
        this.upn = value;
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
     * Gets the value of the cellPhone property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCellPhone() {
        return cellPhone;
    }

    /**
     * Sets the value of the cellPhone property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCellPhone(String value) {
        this.cellPhone = value;
    }

    /**
     * Gets the value of the skype property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSkype() {
        return skype;
    }

    /**
     * Sets the value of the skype property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSkype(String value) {
        this.skype = value;
    }

    /**
     * Gets the value of the companyID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCompanyID() {
        return companyID;
    }

    /**
     * Sets the value of the companyID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCompanyID(String value) {
        this.companyID = value;
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
     * Gets the value of the willExpireAfterDays property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWillExpireAfterDays() {
        return willExpireAfterDays;
    }

    /**
     * Sets the value of the willExpireAfterDays property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWillExpireAfterDays(String value) {
        this.willExpireAfterDays = value;
    }

    /**
     * Gets the value of the country property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the dateCreated property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the value of the dateCreated property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setDateCreated(XMLGregorianCalendar value) {
        this.dateCreated = value;
    }

    /**
     * Gets the value of the auditUser property.
     *
     */
    public boolean isAuditUser() {
        return auditUser;
    }

    /**
     * Sets the value of the auditUser property.
     *
     */
    public void setAuditUser(boolean value) {
        this.auditUser = value;
    }

    /**
     * Gets the value of the groupList property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfGroup }
     *
     */
    public ArrayOfGroup getGroupList() {
        return groupList;
    }

    /**
     * Sets the value of the groupList property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfGroup }
     *
     */
    public void setGroupList(ArrayOfGroup value) {
        this.groupList = value;
    }

    /**
     * Gets the value of the lastLoginDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Sets the value of the lastLoginDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastLoginDate(XMLGregorianCalendar value) {
        this.lastLoginDate = value;
    }

    /**
     * Gets the value of the limitAccessByIPAddress property.
     *
     */
    public boolean isLimitAccessByIPAddress() {
        return limitAccessByIPAddress;
    }

    /**
     * Sets the value of the limitAccessByIPAddress property.
     *
     */
    public void setLimitAccessByIPAddress(boolean value) {
        this.limitAccessByIPAddress = value;
    }

    /**
     * Gets the value of the allowedIPs property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getAllowedIPs() {
        return allowedIPs;
    }

    /**
     * Sets the value of the allowedIPs property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setAllowedIPs(ArrayOfString value) {
        this.allowedIPs = value;
    }

}
