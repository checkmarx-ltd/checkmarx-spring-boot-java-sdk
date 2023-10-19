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
 * <p>Java class for CxWSQuery complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSQuery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="QueryId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Source" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Cwe" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="IsExecutable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsEncrypted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Severity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PackageId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Status" type="{http://Checkmarx.com}QueryStatus"/>
 *         &lt;element name="Type" type="{http://Checkmarx.com}CxWSQueryType"/>
 *         &lt;element name="Categories" type="{http://Checkmarx.com}ArrayOfCxQueryCategory" minOccurs="0"/>
 *         &lt;element name="CxDescriptionID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="QueryVersionCode" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="EngineMetadata" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSQuery", propOrder = {
    "name",
    "queryId",
    "source",
    "cwe",
    "isExecutable",
    "isEncrypted",
    "severity",
    "packageId",
    "status",
    "type",
    "categories",
    "cxDescriptionID",
    "queryVersionCode",
    "engineMetadata"
})
public class CxWSQuery {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "QueryId")
    protected long queryId;
    @XmlElement(name = "Source")
    protected String source;
    @XmlElement(name = "Cwe")
    protected long cwe;
    @XmlElement(name = "IsExecutable")
    protected boolean isExecutable;
    @XmlElement(name = "IsEncrypted")
    protected boolean isEncrypted;
    @XmlElement(name = "Severity")
    protected int severity;
    @XmlElement(name = "PackageId")
    protected long packageId;
    @XmlElement(name = "Status", required = true)
    protected QueryStatus status;
    @XmlElement(name = "Type", required = true)
    protected CxWSQueryType type;
    @XmlElement(name = "Categories")
    protected ArrayOfCxQueryCategory categories;
    @XmlElement(name = "CxDescriptionID")
    protected int cxDescriptionID;
    @XmlElement(name = "QueryVersionCode")
    protected long queryVersionCode;
    @XmlElement(name = "EngineMetadata")
    protected String engineMetadata;

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the queryId property.
     *
     */
    public long getQueryId() {
        return queryId;
    }

    /**
     * Sets the value of the queryId property.
     *
     */
    public void setQueryId(long value) {
        this.queryId = value;
    }

    /**
     * Gets the value of the source property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the cwe property.
     *
     */
    public long getCwe() {
        return cwe;
    }

    /**
     * Sets the value of the cwe property.
     *
     */
    public void setCwe(long value) {
        this.cwe = value;
    }

    /**
     * Gets the value of the isExecutable property.
     *
     */
    public boolean isIsExecutable() {
        return isExecutable;
    }

    /**
     * Sets the value of the isExecutable property.
     *
     */
    public void setIsExecutable(boolean value) {
        this.isExecutable = value;
    }

    /**
     * Gets the value of the isEncrypted property.
     *
     */
    public boolean isIsEncrypted() {
        return isEncrypted;
    }

    /**
     * Sets the value of the isEncrypted property.
     *
     */
    public void setIsEncrypted(boolean value) {
        this.isEncrypted = value;
    }

    /**
     * Gets the value of the severity property.
     *
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     *
     */
    public void setSeverity(int value) {
        this.severity = value;
    }

    /**
     * Gets the value of the packageId property.
     *
     */
    public long getPackageId() {
        return packageId;
    }

    /**
     * Sets the value of the packageId property.
     *
     */
    public void setPackageId(long value) {
        this.packageId = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link QueryStatus }
     *
     */
    public QueryStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value
     *     allowed object is
     *     {@link QueryStatus }
     *
     */
    public void setStatus(QueryStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link CxWSQueryType }
     *
     */
    public CxWSQueryType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link CxWSQueryType }
     *
     */
    public void setType(CxWSQueryType value) {
        this.type = value;
    }

    /**
     * Gets the value of the categories property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfCxQueryCategory }
     *
     */
    public ArrayOfCxQueryCategory getCategories() {
        return categories;
    }

    /**
     * Sets the value of the categories property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfCxQueryCategory }
     *
     */
    public void setCategories(ArrayOfCxQueryCategory value) {
        this.categories = value;
    }

    /**
     * Gets the value of the cxDescriptionID property.
     *
     */
    public int getCxDescriptionID() {
        return cxDescriptionID;
    }

    /**
     * Sets the value of the cxDescriptionID property.
     *
     */
    public void setCxDescriptionID(int value) {
        this.cxDescriptionID = value;
    }

    /**
     * Gets the value of the queryVersionCode property.
     *
     */
    public long getQueryVersionCode() {
        return queryVersionCode;
    }

    /**
     * Sets the value of the queryVersionCode property.
     *
     */
    public void setQueryVersionCode(long value) {
        this.queryVersionCode = value;
    }

    /**
     * Gets the value of the engineMetadata property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEngineMetadata() {
        return engineMetadata;
    }

    /**
     * Sets the value of the engineMetadata property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEngineMetadata(String value) {
        this.engineMetadata = value;
    }

}
