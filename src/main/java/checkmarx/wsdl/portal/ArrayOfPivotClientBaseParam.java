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
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfPivotClientBaseParam complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ArrayOfPivotClientBaseParam">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PivotClientBaseParam" type="{http://Checkmarx.com}PivotClientBaseParam" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPivotClientBaseParam", propOrder = {
    "pivotClientBaseParam"
})
public class ArrayOfPivotClientBaseParam {

    @XmlElement(name = "PivotClientBaseParam", nillable = true)
    protected List<PivotClientBaseParam> pivotClientBaseParam;

    /**
     * Gets the value of the pivotClientBaseParam property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pivotClientBaseParam property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPivotClientBaseParam().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PivotClientBaseParam }
     *
     *
     */
    public List<PivotClientBaseParam> getPivotClientBaseParam() {
        if (pivotClientBaseParam == null) {
            pivotClientBaseParam = new ArrayList<PivotClientBaseParam>();
        }
        return this.pivotClientBaseParam;
    }

}
