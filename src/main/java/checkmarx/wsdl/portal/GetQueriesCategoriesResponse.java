//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetQueriesCategoriesResult" type="{http://Checkmarx.com}CxWSResponseQueriesCategories" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getQueriesCategoriesResult"
})
@XmlRootElement(name = "GetQueriesCategoriesResponse")
public class GetQueriesCategoriesResponse {

    @XmlElement(name = "GetQueriesCategoriesResult")
    protected CxWSResponseQueriesCategories getQueriesCategoriesResult;

    /**
     * Gets the value of the getQueriesCategoriesResult property.
     *
     * @return
     *     possible object is
     *     {@link CxWSResponseQueriesCategories }
     *
     */
    public CxWSResponseQueriesCategories getGetQueriesCategoriesResult() {
        return getQueriesCategoriesResult;
    }

    /**
     * Sets the value of the getQueriesCategoriesResult property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWSResponseQueriesCategories }
     *
     */
    public void setGetQueriesCategoriesResult(CxWSResponseQueriesCategories value) {
        this.getQueriesCategoriesResult = value;
    }

}
