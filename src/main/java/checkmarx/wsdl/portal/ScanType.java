//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ScanType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ScanType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UNKNOWN"/>
 *     &lt;enumeration value="ALLSCANS"/>
 *     &lt;enumeration value="REGULAR"/>
 *     &lt;enumeration value="SUBSET"/>
 *     &lt;enumeration value="PARTIAL"/>
 *     &lt;enumeration value="RUNNING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ScanType")
@XmlEnum
public enum ScanType {

    UNKNOWN,
    ALLSCANS,
    REGULAR,
    SUBSET,
    PARTIAL,
    RUNNING;

    public String value() {
        return name();
    }

    public static ScanType fromValue(String v) {
        return valueOf(v);
    }

}
