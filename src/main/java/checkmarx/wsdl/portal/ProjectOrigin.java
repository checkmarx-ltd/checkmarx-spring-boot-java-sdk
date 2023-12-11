//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.06.07 at 11:08:45 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProjectOrigin.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProjectOrigin">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LocalPath"/>
 *     &lt;enumeration value="SharedPath"/>
 *     &lt;enumeration value="TFS"/>
 *     &lt;enumeration value="External"/>
 *     &lt;enumeration value="SVN"/>
 *     &lt;enumeration value="CVS"/>
 *     &lt;enumeration value="GIT"/>
 *     &lt;enumeration value="Perforce"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ProjectOrigin")
@XmlEnum
public enum ProjectOrigin {

    @XmlEnumValue("LocalPath")
    LOCAL_PATH("LocalPath"),
    @XmlEnumValue("SharedPath")
    SHARED_PATH("SharedPath"),
    TFS("TFS"),
    @XmlEnumValue("External")
    EXTERNAL("External"),
    SVN("SVN"),
    CVS("CVS"),
    GIT("GIT"),
    @XmlEnumValue("Perforce")
    PERFORCE("Perforce");
    private final String value;

    ProjectOrigin(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProjectOrigin fromValue(String v) {
        for (ProjectOrigin c: ProjectOrigin.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
