//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CxWSResponseScanStatusArray complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponseScanStatusArray">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="statusArr" type="{http://Checkmarx.com}ArrayOfCxWSResponseScanStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponseScanStatusArray", propOrder = {
    "statusArr"
})
public class CxWSResponseScanStatusArray
    extends CxWSBasicRepsonse
{

    protected ArrayOfCxWSResponseScanStatus statusArr;

    /**
     * Gets the value of the statusArr property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfCxWSResponseScanStatus }
     *
     */
    public ArrayOfCxWSResponseScanStatus getStatusArr() {
        return statusArr;
    }

    /**
     * Sets the value of the statusArr property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfCxWSResponseScanStatus }
     *
     */
    public void setStatusArr(ArrayOfCxWSResponseScanStatus value) {
        this.statusArr = value;
    }

}
