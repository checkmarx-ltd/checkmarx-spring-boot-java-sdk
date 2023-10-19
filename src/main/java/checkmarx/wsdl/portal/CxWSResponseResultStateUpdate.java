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
 * <p>Java class for CxWSResponseResultStateUpdate complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponseResultStateUpdate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="PathInfluenceExists" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="UpdatedResultState" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="InfluencedPathsDetails" type="{http://Checkmarx.com}ArrayOfCxWSResultPathDisplayDetails" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponseResultStateUpdate", propOrder = {
    "pathInfluenceExists",
    "updatedResultState",
    "influencedPathsDetails"
})
public class CxWSResponseResultStateUpdate
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "PathInfluenceExists")
    protected boolean pathInfluenceExists;
    @XmlElement(name = "UpdatedResultState")
    protected boolean updatedResultState;
    @XmlElement(name = "InfluencedPathsDetails")
    protected ArrayOfCxWSResultPathDisplayDetails influencedPathsDetails;

    /**
     * Gets the value of the pathInfluenceExists property.
     *
     */
    public boolean isPathInfluenceExists() {
        return pathInfluenceExists;
    }

    /**
     * Sets the value of the pathInfluenceExists property.
     *
     */
    public void setPathInfluenceExists(boolean value) {
        this.pathInfluenceExists = value;
    }

    /**
     * Gets the value of the updatedResultState property.
     *
     */
    public boolean isUpdatedResultState() {
        return updatedResultState;
    }

    /**
     * Sets the value of the updatedResultState property.
     *
     */
    public void setUpdatedResultState(boolean value) {
        this.updatedResultState = value;
    }

    /**
     * Gets the value of the influencedPathsDetails property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfCxWSResultPathDisplayDetails }
     *
     */
    public ArrayOfCxWSResultPathDisplayDetails getInfluencedPathsDetails() {
        return influencedPathsDetails;
    }

    /**
     * Sets the value of the influencedPathsDetails property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfCxWSResultPathDisplayDetails }
     *
     */
    public void setInfluencedPathsDetails(ArrayOfCxWSResultPathDisplayDetails value) {
        this.influencedPathsDetails = value;
    }

}
