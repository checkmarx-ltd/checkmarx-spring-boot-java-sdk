//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.06.07 at 11:08:45 PM EST
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
 *         &lt;element name="GetResultGraphForQueryResult" type="{http://Checkmarx.com}CxWSResponseResultGraph" minOccurs="0"/>
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
    "getResultGraphForQueryResult"
})
@XmlRootElement(name = "GetResultGraphForQueryResponse")
public class GetResultGraphForQueryResponse {

    @XmlElement(name = "GetResultGraphForQueryResult")
    protected CxWSResponseResultGraph getResultGraphForQueryResult;

    /**
     * Gets the value of the getResultGraphForQueryResult property.
     *
     * @return
     *     possible object is
     *     {@link CxWSResponseResultGraph }
     *
     */
    public CxWSResponseResultGraph getGetResultGraphForQueryResult() {
        return getResultGraphForQueryResult;
    }

    /**
     * Sets the value of the getResultGraphForQueryResult property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWSResponseResultGraph }
     *
     */
    public void setGetResultGraphForQueryResult(CxWSResponseResultGraph value) {
        this.getResultGraphForQueryResult = value;
    }

}
