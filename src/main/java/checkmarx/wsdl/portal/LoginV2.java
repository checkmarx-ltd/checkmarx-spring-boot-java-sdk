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
 *         &lt;element name="applicationCredentials" type="{http://Checkmarx.com}Credentials" minOccurs="0"/>
 *         &lt;element name="lcid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="useExistingSession" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "applicationCredentials",
    "lcid",
    "useExistingSession"
})
@XmlRootElement(name = "LoginV2")
public class LoginV2 {

    protected Credentials applicationCredentials;
    protected int lcid;
    protected boolean useExistingSession;

    /**
     * Gets the value of the applicationCredentials property.
     *
     * @return
     *     possible object is
     *     {@link Credentials }
     *
     */
    public Credentials getApplicationCredentials() {
        return applicationCredentials;
    }

    /**
     * Sets the value of the applicationCredentials property.
     *
     * @param value
     *     allowed object is
     *     {@link Credentials }
     *
     */
    public void setApplicationCredentials(Credentials value) {
        this.applicationCredentials = value;
    }

    /**
     * Gets the value of the lcid property.
     *
     */
    public int getLcid() {
        return lcid;
    }

    /**
     * Sets the value of the lcid property.
     *
     */
    public void setLcid(int value) {
        this.lcid = value;
    }

    /**
     * Gets the value of the useExistingSession property.
     *
     */
    public boolean isUseExistingSession() {
        return useExistingSession;
    }

    /**
     * Sets the value of the useExistingSession property.
     *
     */
    public void setUseExistingSession(boolean value) {
        this.useExistingSession = value;
    }

}
