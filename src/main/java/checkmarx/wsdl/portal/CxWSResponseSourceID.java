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
 * <p>Java class for CxWSResponseSourceID complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponseSourceID">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="ProjectID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="SourceID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponseSourceID", propOrder = {
    "projectID",
    "sourceID"
})
public class CxWSResponseSourceID
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "ProjectID")
    protected long projectID;
    @XmlElement(name = "SourceID")
    protected String sourceID;

    /**
     * Gets the value of the projectID property.
     *
     */
    public long getProjectID() {
        return projectID;
    }

    /**
     * Sets the value of the projectID property.
     *
     */
    public void setProjectID(long value) {
        this.projectID = value;
    }

    /**
     * Gets the value of the sourceID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSourceID() {
        return sourceID;
    }

    /**
     * Sets the value of the sourceID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSourceID(String value) {
        this.sourceID = value;
    }

}
