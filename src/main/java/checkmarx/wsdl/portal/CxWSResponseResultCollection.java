//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.06.07 at 11:08:45 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CxWSResponseResultCollection complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponseResultCollection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="ResultCollection" type="{http://Checkmarx.com}AuditResultsCollection" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponseResultCollection", propOrder = {
    "resultCollection"
})
public class CxWSResponseResultCollection
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "ResultCollection")
    protected AuditResultsCollection resultCollection;

    /**
     * Gets the value of the resultCollection property.
     *
     * @return
     *     possible object is
     *     {@link AuditResultsCollection }
     *
     */
    public AuditResultsCollection getResultCollection() {
        return resultCollection;
    }

    /**
     * Sets the value of the resultCollection property.
     *
     * @param value
     *     allowed object is
     *     {@link AuditResultsCollection }
     *
     */
    public void setResultCollection(AuditResultsCollection value) {
        this.resultCollection = value;
    }

}
