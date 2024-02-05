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
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="i_SearchPattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="searchPatternOption" type="{http://Checkmarx.com}CxWSSearchPatternOption"/>
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
    "domain",
    "iSearchPattern",
    "searchPatternOption"
})
@XmlRootElement(name = "GetAllUsersFromUserDirectory")
public class GetAllUsersFromUserDirectory {

    protected String sessionID;
    protected String domain;
    @XmlElement(name = "i_SearchPattern")
    protected String iSearchPattern;
    @XmlElement(required = true)
    protected CxWSSearchPatternOption searchPatternOption;

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
     * Gets the value of the domain property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the iSearchPattern property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getISearchPattern() {
        return iSearchPattern;
    }

    /**
     * Sets the value of the iSearchPattern property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setISearchPattern(String value) {
        this.iSearchPattern = value;
    }

    /**
     * Gets the value of the searchPatternOption property.
     *
     * @return
     *     possible object is
     *     {@link CxWSSearchPatternOption }
     *
     */
    public CxWSSearchPatternOption getSearchPatternOption() {
        return searchPatternOption;
    }

    /**
     * Sets the value of the searchPatternOption property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWSSearchPatternOption }
     *
     */
    public void setSearchPatternOption(CxWSSearchPatternOption value) {
        this.searchPatternOption = value;
    }

}
