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
 * <p>Java class for CxWSItemAndCRUD complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSItemAndCRUD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://Checkmarx.com}CxWSItemTypeEnum"/>
 *         &lt;element name="CRUDActionList" type="{http://Checkmarx.com}ArrayOfCxWSEnableCRUDAction" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSItemAndCRUD", propOrder = {
    "type",
    "crudActionList"
})
public class CxWSItemAndCRUD {

    @XmlElement(name = "Type", required = true)
    protected CxWSItemTypeEnum type;
    @XmlElement(name = "CRUDActionList")
    protected ArrayOfCxWSEnableCRUDAction crudActionList;

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link CxWSItemTypeEnum }
     *
     */
    public CxWSItemTypeEnum getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWSItemTypeEnum }
     *
     */
    public void setType(CxWSItemTypeEnum value) {
        this.type = value;
    }

    /**
     * Gets the value of the crudActionList property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfCxWSEnableCRUDAction }
     *
     */
    public ArrayOfCxWSEnableCRUDAction getCRUDActionList() {
        return crudActionList;
    }

    /**
     * Sets the value of the crudActionList property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfCxWSEnableCRUDAction }
     *
     */
    public void setCRUDActionList(ArrayOfCxWSEnableCRUDAction value) {
        this.crudActionList = value;
    }

}
