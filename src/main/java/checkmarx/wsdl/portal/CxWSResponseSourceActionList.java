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
 * <p>Java class for CxWSResponseSourceActionList complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponseSourceActionList">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="ActionList" type="{http://Checkmarx.com}ArrayOfAction" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponseSourceActionList", propOrder = {
    "actionList"
})
public class CxWSResponseSourceActionList
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "ActionList")
    protected ArrayOfAction actionList;

    /**
     * Gets the value of the actionList property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfAction }
     *
     */
    public ArrayOfAction getActionList() {
        return actionList;
    }

    /**
     * Sets the value of the actionList property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfAction }
     *
     */
    public void setActionList(ArrayOfAction value) {
        this.actionList = value;
    }

}
