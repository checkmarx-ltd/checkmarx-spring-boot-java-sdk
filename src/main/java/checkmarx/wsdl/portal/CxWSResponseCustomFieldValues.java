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
 * <p>Java class for CxWSResponseCustomFieldValues complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponseCustomFieldValues">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="Values" type="{http://Checkmarx.com}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponseCustomFieldValues", propOrder = {
    "values"
})
public class CxWSResponseCustomFieldValues
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "Values")
    protected ArrayOfString values;

    /**
     * Gets the value of the values property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *
     */
    public ArrayOfString getValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *
     */
    public void setValues(ArrayOfString value) {
        this.values = value;
    }

}
