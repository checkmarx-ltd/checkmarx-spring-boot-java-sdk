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
 * <p>Java class for CxWSResponseScanResults complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponseScanResults">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="ScanResults" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="containsAllResults" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponseScanResults", propOrder = {
    "scanResults",
    "containsAllResults"
})
public class CxWSResponseScanResults
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "ScanResults")
    protected byte[] scanResults;
    protected boolean containsAllResults;

    /**
     * Gets the value of the scanResults property.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getScanResults() {
        return scanResults;
    }

    /**
     * Sets the value of the scanResults property.
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setScanResults(byte[] value) {
        this.scanResults = ((byte[]) value);
    }

    /**
     * Gets the value of the containsAllResults property.
     *
     */
    public boolean isContainsAllResults() {
        return containsAllResults;
    }

    /**
     * Sets the value of the containsAllResults property.
     *
     */
    public void setContainsAllResults(boolean value) {
        this.containsAllResults = value;
    }

}
