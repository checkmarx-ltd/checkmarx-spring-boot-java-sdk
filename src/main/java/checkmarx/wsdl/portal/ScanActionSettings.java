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
 * <p>Java class for ScanActionSettings complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ScanActionSettings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ScanActionList" type="{http://Checkmarx.com}ArrayOfScanAction" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScanActionSettings", propOrder = {
    "scanActionList"
})
public class ScanActionSettings {

    @XmlElement(name = "ScanActionList")
    protected ArrayOfScanAction scanActionList;

    /**
     * Gets the value of the scanActionList property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfScanAction }
     *
     */
    public ArrayOfScanAction getScanActionList() {
        return scanActionList;
    }

    /**
     * Sets the value of the scanActionList property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfScanAction }
     *
     */
    public void setScanActionList(ArrayOfScanAction value) {
        this.scanActionList = value;
    }

}
