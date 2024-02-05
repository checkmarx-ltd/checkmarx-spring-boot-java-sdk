//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CxClientType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CxClientType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="WebPortal"/>
 *     &lt;enumeration value="CLI"/>
 *     &lt;enumeration value="Eclipse"/>
 *     &lt;enumeration value="VS"/>
 *     &lt;enumeration value="InteliJ"/>
 *     &lt;enumeration value="Audit"/>
 *     &lt;enumeration value="SDK"/>
 *     &lt;enumeration value="Jenkins"/>
 *     &lt;enumeration value="TFSBuild"/>
 *     &lt;enumeration value="Importer"/>
 *     &lt;enumeration value="Other"/>
 *     &lt;enumeration value="Sonar"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "CxClientType")
@XmlEnum
public enum CxClientType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("WebPortal")
    WEB_PORTAL("WebPortal"),
    CLI("CLI"),
    @XmlEnumValue("Eclipse")
    ECLIPSE("Eclipse"),
    VS("VS"),
    @XmlEnumValue("InteliJ")
    INTELI_J("InteliJ"),
    @XmlEnumValue("Audit")
    AUDIT("Audit"),
    SDK("SDK"),
    @XmlEnumValue("Jenkins")
    JENKINS("Jenkins"),
    @XmlEnumValue("TFSBuild")
    TFS_BUILD("TFSBuild"),
    @XmlEnumValue("Importer")
    IMPORTER("Importer"),
    @XmlEnumValue("Other")
    OTHER("Other"),
    @XmlEnumValue("Sonar")
    SONAR("Sonar");
    private final String value;

    CxClientType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CxClientType fromValue(String v) {
        for (CxClientType c: CxClientType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
