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
 * <p>Java class for SystemSettings complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SystemSettings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReportFolder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResultFolder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExecutablesFolder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMTPHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMTPPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="EMailFromAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMTPUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMTPPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMTPUseDefaultCredentials" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="SmtpOverSsl" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="SMTPEncryption" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MaxScans" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CompletedScanShowTimeInMin" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="commands" type="{http://Checkmarx.com}ArrayOfCxPredefinedCommand" minOccurs="0"/>
 *         &lt;element name="WebServer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DefaultLanguage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="AllowAutoSignIn" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="DefaultGitHubEventThreshold" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NotifyLicenseExpiration" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="GitExePath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PerforceExePath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SystemSettings", propOrder = {
    "reportFolder",
    "resultFolder",
    "executablesFolder",
    "smtpHost",
    "smtpPort",
    "eMailFromAddress",
    "smtpUserName",
    "smtpPassword",
    "smtpUseDefaultCredentials",
    "smtpOverSsl",
    "smtpEncryption",
    "maxScans",
    "completedScanShowTimeInMin",
    "commands",
    "webServer",
    "defaultLanguage",
    "allowAutoSignIn",
    "defaultGitHubEventThreshold",
    "notifyLicenseExpiration",
    "gitExePath",
    "perforceExePath"
})
public class SystemSettings {

    @XmlElement(name = "ReportFolder")
    protected String reportFolder;
    @XmlElement(name = "ResultFolder")
    protected String resultFolder;
    @XmlElement(name = "ExecutablesFolder")
    protected String executablesFolder;
    @XmlElement(name = "SMTPHost")
    protected String smtpHost;
    @XmlElement(name = "SMTPPort")
    protected int smtpPort;
    @XmlElement(name = "EMailFromAddress")
    protected String eMailFromAddress;
    @XmlElement(name = "SMTPUserName")
    protected String smtpUserName;
    @XmlElement(name = "SMTPPassword")
    protected String smtpPassword;
    @XmlElement(name = "SMTPUseDefaultCredentials")
    protected boolean smtpUseDefaultCredentials;
    @XmlElement(name = "SmtpOverSsl")
    protected boolean smtpOverSsl;
    @XmlElement(name = "SMTPEncryption")
    protected String smtpEncryption;
    @XmlElement(name = "MaxScans")
    protected int maxScans;
    @XmlElement(name = "CompletedScanShowTimeInMin")
    protected int completedScanShowTimeInMin;
    protected ArrayOfCxPredefinedCommand commands;
    @XmlElement(name = "WebServer")
    protected String webServer;
    @XmlElement(name = "DefaultLanguage")
    protected int defaultLanguage;
    @XmlElement(name = "AllowAutoSignIn")
    protected boolean allowAutoSignIn;
    @XmlElement(name = "DefaultGitHubEventThreshold")
    protected int defaultGitHubEventThreshold;
    @XmlElement(name = "NotifyLicenseExpiration")
    protected boolean notifyLicenseExpiration;
    @XmlElement(name = "GitExePath")
    protected String gitExePath;
    @XmlElement(name = "PerforceExePath")
    protected String perforceExePath;

    /**
     * Gets the value of the reportFolder property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReportFolder() {
        return reportFolder;
    }

    /**
     * Sets the value of the reportFolder property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReportFolder(String value) {
        this.reportFolder = value;
    }

    /**
     * Gets the value of the resultFolder property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getResultFolder() {
        return resultFolder;
    }

    /**
     * Sets the value of the resultFolder property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResultFolder(String value) {
        this.resultFolder = value;
    }

    /**
     * Gets the value of the executablesFolder property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExecutablesFolder() {
        return executablesFolder;
    }

    /**
     * Sets the value of the executablesFolder property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExecutablesFolder(String value) {
        this.executablesFolder = value;
    }

    /**
     * Gets the value of the smtpHost property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSMTPHost() {
        return smtpHost;
    }

    /**
     * Sets the value of the smtpHost property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSMTPHost(String value) {
        this.smtpHost = value;
    }

    /**
     * Gets the value of the smtpPort property.
     *
     */
    public int getSMTPPort() {
        return smtpPort;
    }

    /**
     * Sets the value of the smtpPort property.
     *
     */
    public void setSMTPPort(int value) {
        this.smtpPort = value;
    }

    /**
     * Gets the value of the eMailFromAddress property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEMailFromAddress() {
        return eMailFromAddress;
    }

    /**
     * Sets the value of the eMailFromAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEMailFromAddress(String value) {
        this.eMailFromAddress = value;
    }

    /**
     * Gets the value of the smtpUserName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSMTPUserName() {
        return smtpUserName;
    }

    /**
     * Sets the value of the smtpUserName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSMTPUserName(String value) {
        this.smtpUserName = value;
    }

    /**
     * Gets the value of the smtpPassword property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSMTPPassword() {
        return smtpPassword;
    }

    /**
     * Sets the value of the smtpPassword property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSMTPPassword(String value) {
        this.smtpPassword = value;
    }

    /**
     * Gets the value of the smtpUseDefaultCredentials property.
     *
     */
    public boolean isSMTPUseDefaultCredentials() {
        return smtpUseDefaultCredentials;
    }

    /**
     * Sets the value of the smtpUseDefaultCredentials property.
     *
     */
    public void setSMTPUseDefaultCredentials(boolean value) {
        this.smtpUseDefaultCredentials = value;
    }

    /**
     * Gets the value of the smtpOverSsl property.
     *
     */
    public boolean isSmtpOverSsl() {
        return smtpOverSsl;
    }

    /**
     * Sets the value of the smtpOverSsl property.
     *
     */
    public void setSmtpOverSsl(boolean value) {
        this.smtpOverSsl = value;
    }

    /**
     * Gets the value of the smtpEncryption property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSMTPEncryption() {
        return smtpEncryption;
    }

    /**
     * Sets the value of the smtpEncryption property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSMTPEncryption(String value) {
        this.smtpEncryption = value;
    }

    /**
     * Gets the value of the maxScans property.
     *
     */
    public int getMaxScans() {
        return maxScans;
    }

    /**
     * Sets the value of the maxScans property.
     *
     */
    public void setMaxScans(int value) {
        this.maxScans = value;
    }

    /**
     * Gets the value of the completedScanShowTimeInMin property.
     *
     */
    public int getCompletedScanShowTimeInMin() {
        return completedScanShowTimeInMin;
    }

    /**
     * Sets the value of the completedScanShowTimeInMin property.
     *
     */
    public void setCompletedScanShowTimeInMin(int value) {
        this.completedScanShowTimeInMin = value;
    }

    /**
     * Gets the value of the commands property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfCxPredefinedCommand }
     *
     */
    public ArrayOfCxPredefinedCommand getCommands() {
        return commands;
    }

    /**
     * Sets the value of the commands property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfCxPredefinedCommand }
     *
     */
    public void setCommands(ArrayOfCxPredefinedCommand value) {
        this.commands = value;
    }

    /**
     * Gets the value of the webServer property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWebServer() {
        return webServer;
    }

    /**
     * Sets the value of the webServer property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWebServer(String value) {
        this.webServer = value;
    }

    /**
     * Gets the value of the defaultLanguage property.
     *
     */
    public int getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Sets the value of the defaultLanguage property.
     *
     */
    public void setDefaultLanguage(int value) {
        this.defaultLanguage = value;
    }

    /**
     * Gets the value of the allowAutoSignIn property.
     *
     */
    public boolean isAllowAutoSignIn() {
        return allowAutoSignIn;
    }

    /**
     * Sets the value of the allowAutoSignIn property.
     *
     */
    public void setAllowAutoSignIn(boolean value) {
        this.allowAutoSignIn = value;
    }

    /**
     * Gets the value of the defaultGitHubEventThreshold property.
     *
     */
    public int getDefaultGitHubEventThreshold() {
        return defaultGitHubEventThreshold;
    }

    /**
     * Sets the value of the defaultGitHubEventThreshold property.
     *
     */
    public void setDefaultGitHubEventThreshold(int value) {
        this.defaultGitHubEventThreshold = value;
    }

    /**
     * Gets the value of the notifyLicenseExpiration property.
     *
     */
    public boolean isNotifyLicenseExpiration() {
        return notifyLicenseExpiration;
    }

    /**
     * Sets the value of the notifyLicenseExpiration property.
     *
     */
    public void setNotifyLicenseExpiration(boolean value) {
        this.notifyLicenseExpiration = value;
    }

    /**
     * Gets the value of the gitExePath property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGitExePath() {
        return gitExePath;
    }

    /**
     * Sets the value of the gitExePath property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGitExePath(String value) {
        this.gitExePath = value;
    }

    /**
     * Gets the value of the perforceExePath property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPerforceExePath() {
        return perforceExePath;
    }

    /**
     * Sets the value of the perforceExePath property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPerforceExePath(String value) {
        this.perforceExePath = value;
    }

}
