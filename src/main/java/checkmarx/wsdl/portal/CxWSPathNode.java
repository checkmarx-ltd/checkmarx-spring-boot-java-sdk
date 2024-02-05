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
 * <p>Java class for CxWSPathNode complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSPathNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Column" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Length" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Line" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DOM_Id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="MethodLine" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PathNodeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSPathNode", propOrder = {
    "column",
    "fullName",
    "fileName",
    "length",
    "line",
    "name",
    "domId",
    "methodLine",
    "pathNodeId"
})
public class CxWSPathNode {

    @XmlElement(name = "Column")
    protected int column;
    @XmlElement(name = "FullName")
    protected String fullName;
    @XmlElement(name = "FileName")
    protected String fileName;
    @XmlElement(name = "Length")
    protected int length;
    @XmlElement(name = "Line")
    protected int line;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "DOM_Id")
    protected int domId;
    @XmlElement(name = "MethodLine")
    protected int methodLine;
    @XmlElement(name = "PathNodeId")
    protected int pathNodeId;

    /**
     * Gets the value of the column property.
     *
     */
    public int getColumn() {
        return column;
    }

    /**
     * Sets the value of the column property.
     *
     */
    public void setColumn(int value) {
        this.column = value;
    }

    /**
     * Gets the value of the fullName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the fileName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the length property.
     *
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     *
     */
    public void setLength(int value) {
        this.length = value;
    }

    /**
     * Gets the value of the line property.
     *
     */
    public int getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     *
     */
    public void setLine(int value) {
        this.line = value;
    }

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
     * Gets the value of the domId property.
     *
     */
    public int getDOMId() {
        return domId;
    }

    /**
     * Sets the value of the domId property.
     *
     */
    public void setDOMId(int value) {
        this.domId = value;
    }

    /**
     * Gets the value of the methodLine property.
     *
     */
    public int getMethodLine() {
        return methodLine;
    }

    /**
     * Sets the value of the methodLine property.
     *
     */
    public void setMethodLine(int value) {
        this.methodLine = value;
    }

    /**
     * Gets the value of the pathNodeId property.
     *
     */
    public int getPathNodeId() {
        return pathNodeId;
    }

    /**
     * Sets the value of the pathNodeId property.
     *
     */
    public void setPathNodeId(int value) {
        this.pathNodeId = value;
    }

}
