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
 * <p>Java class for SourceControlProtocolType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SourceControlProtocolType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="WindowsAuthentication"/>
 *     &lt;enumeration value="SSL"/>
 *     &lt;enumeration value="SSH"/>
 *     &lt;enumeration value="PasswordServer"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "SourceControlProtocolType")
@XmlEnum
public enum SourceControlProtocolType {

    @XmlEnumValue("WindowsAuthentication")
    WINDOWS_AUTHENTICATION("WindowsAuthentication"),
    SSL("SSL"),
    SSH("SSH"),
    @XmlEnumValue("PasswordServer")
    PASSWORD_SERVER("PasswordServer");
    private final String value;

    SourceControlProtocolType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SourceControlProtocolType fromValue(String v) {
        for (SourceControlProtocolType c: SourceControlProtocolType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
