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
 * <p>Java class for CxWSResponsProjectChartData complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponsProjectChartData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="ScansRiskLevelList" type="{http://Checkmarx.com}ArrayOfProjectScansRiskLevel" minOccurs="0"/>
 *         &lt;element name="ScanResultSummaryList" type="{http://Checkmarx.com}ArrayOfProjectScansResultSummary" minOccurs="0"/>
 *         &lt;element name="LastStatisticsCalcDate" type="{http://Checkmarx.com}CxDateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponsProjectChartData", propOrder = {
    "scansRiskLevelList",
    "scanResultSummaryList",
    "lastStatisticsCalcDate"
})
public class CxWSResponsProjectChartData
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "ScansRiskLevelList")
    protected ArrayOfProjectScansRiskLevel scansRiskLevelList;
    @XmlElement(name = "ScanResultSummaryList")
    protected ArrayOfProjectScansResultSummary scanResultSummaryList;
    @XmlElement(name = "LastStatisticsCalcDate")
    protected CxDateTime lastStatisticsCalcDate;

    /**
     * Gets the value of the scansRiskLevelList property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfProjectScansRiskLevel }
     *
     */
    public ArrayOfProjectScansRiskLevel getScansRiskLevelList() {
        return scansRiskLevelList;
    }

    /**
     * Sets the value of the scansRiskLevelList property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfProjectScansRiskLevel }
     *
     */
    public void setScansRiskLevelList(ArrayOfProjectScansRiskLevel value) {
        this.scansRiskLevelList = value;
    }

    /**
     * Gets the value of the scanResultSummaryList property.
     *
     * @return
     *     possible object is
     *     {@link ArrayOfProjectScansResultSummary }
     *
     */
    public ArrayOfProjectScansResultSummary getScanResultSummaryList() {
        return scanResultSummaryList;
    }

    /**
     * Sets the value of the scanResultSummaryList property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayOfProjectScansResultSummary }
     *
     */
    public void setScanResultSummaryList(ArrayOfProjectScansResultSummary value) {
        this.scanResultSummaryList = value;
    }

    /**
     * Gets the value of the lastStatisticsCalcDate property.
     *
     * @return
     *     possible object is
     *     {@link CxDateTime }
     *
     */
    public CxDateTime getLastStatisticsCalcDate() {
        return lastStatisticsCalcDate;
    }

    /**
     * Sets the value of the lastStatisticsCalcDate property.
     *
     * @param value
     *     allowed object is
     *     {@link CxDateTime }
     *
     */
    public void setLastStatisticsCalcDate(CxDateTime value) {
        this.lastStatisticsCalcDate = value;
    }

}
