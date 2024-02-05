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
 * <p>Java class for ResultLabelTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ResultLabelTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IgnorePath"/>
 *     &lt;enumeration value="Remark"/>
 *     &lt;enumeration value="Severity"/>
 *     &lt;enumeration value="State"/>
 *     &lt;enumeration value="Assign"/>
 *     &lt;enumeration value="IssueTracking"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ResultLabelTypeEnum")
@XmlEnum
public enum ResultLabelTypeEnum {

    @XmlEnumValue("IgnorePath")
    IGNORE_PATH("IgnorePath"),
    @XmlEnumValue("Remark")
    REMARK("Remark"),
    @XmlEnumValue("Severity")
    SEVERITY("Severity"),
    @XmlEnumValue("State")
    STATE("State"),
    @XmlEnumValue("Assign")
    ASSIGN("Assign"),
    @XmlEnumValue("IssueTracking")
    ISSUE_TRACKING("IssueTracking");
    private final String value;

    ResultLabelTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResultLabelTypeEnum fromValue(String v) {
        for (ResultLabelTypeEnum c: ResultLabelTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
