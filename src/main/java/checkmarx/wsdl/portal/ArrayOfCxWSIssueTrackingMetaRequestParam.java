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
 * <p>Java class for ArrayOfCxWSIssueTrackingMetaRequestParam complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ArrayOfCxWSIssueTrackingMetaRequestParam">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CxWSIssueTrackingMetaRequestParam" type="{http://Checkmarx.com}CxWSIssueTrackingMetaRequestParam" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCxWSIssueTrackingMetaRequestParam", propOrder = {
    "cxWSIssueTrackingMetaRequestParam"
})
public class ArrayOfCxWSIssueTrackingMetaRequestParam {

    @XmlElement(name = "CxWSIssueTrackingMetaRequestParam", nillable = true)
    protected List<CxWSIssueTrackingMetaRequestParam> cxWSIssueTrackingMetaRequestParam;

    /**
     * Gets the value of the cxWSIssueTrackingMetaRequestParam property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cxWSIssueTrackingMetaRequestParam property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCxWSIssueTrackingMetaRequestParam().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CxWSIssueTrackingMetaRequestParam }
     *
     *
     */
    public List<CxWSIssueTrackingMetaRequestParam> getCxWSIssueTrackingMetaRequestParam() {
        if (cxWSIssueTrackingMetaRequestParam == null) {
            cxWSIssueTrackingMetaRequestParam = new ArrayList<CxWSIssueTrackingMetaRequestParam>();
        }
        return this.cxWSIssueTrackingMetaRequestParam;
    }

}
