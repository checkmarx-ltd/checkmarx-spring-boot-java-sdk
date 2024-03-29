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
 * <p>Java class for CxWSResponseScanStatus complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSResponseScanStatus">
 *   &lt;complexContent>
 *     &lt;extension base="{http://Checkmarx.com}CxWSBasicRepsonse">
 *       &lt;sequence>
 *         &lt;element name="CurrentStage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="QueuePosition" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TotalPercent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CurrentStagePercent" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StageName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StageMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StepMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StepDetails" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResultId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ScanId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ProjectId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="TaskId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="TaskName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProjectName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RunId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SourceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LOC" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="TimeScheduled" type="{http://Checkmarx.com}CxDateTime" minOccurs="0"/>
 *         &lt;element name="ElapsedTime" type="{http://Checkmarx.com}CxDateTime" minOccurs="0"/>
 *         &lt;element name="TimeFinished" type="{http://Checkmarx.com}CxDateTime" minOccurs="0"/>
 *         &lt;element name="TimeBeginWorking" type="{http://Checkmarx.com}CxDateTime" minOccurs="0"/>
 *         &lt;element name="CurrentStatus" type="{http://Checkmarx.com}CurrentStatusEnum"/>
 *         &lt;element name="ServerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Origin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsPublic" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PartialResults" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSResponseScanStatus", propOrder = {
    "currentStage",
    "queuePosition",
    "totalPercent",
    "currentStagePercent",
    "owner",
    "stageName",
    "stageMessage",
    "stepMessage",
    "stepDetails",
    "resultId",
    "scanId",
    "projectId",
    "taskId",
    "taskName",
    "projectName",
    "runId",
    "sourceId",
    "loc",
    "timeScheduled",
    "elapsedTime",
    "timeFinished",
    "timeBeginWorking",
    "currentStatus",
    "serverName",
    "origin",
    "isPublic",
    "partialResults"
})
public class CxWSResponseScanStatus
    extends CxWSBasicRepsonse
{

    @XmlElement(name = "CurrentStage")
    protected int currentStage;
    @XmlElement(name = "QueuePosition")
    protected int queuePosition;
    @XmlElement(name = "TotalPercent")
    protected int totalPercent;
    @XmlElement(name = "CurrentStagePercent")
    protected int currentStagePercent;
    @XmlElement(name = "Owner")
    protected String owner;
    @XmlElement(name = "StageName")
    protected String stageName;
    @XmlElement(name = "StageMessage")
    protected String stageMessage;
    @XmlElement(name = "StepMessage")
    protected String stepMessage;
    @XmlElement(name = "StepDetails")
    protected String stepDetails;
    @XmlElement(name = "ResultId")
    protected long resultId;
    @XmlElement(name = "ScanId")
    protected long scanId;
    @XmlElement(name = "ProjectId")
    protected long projectId;
    @XmlElement(name = "TaskId")
    protected long taskId;
    @XmlElement(name = "TaskName")
    protected String taskName;
    @XmlElement(name = "ProjectName")
    protected String projectName;
    @XmlElement(name = "RunId")
    protected String runId;
    @XmlElement(name = "SourceId")
    protected String sourceId;
    @XmlElement(name = "LOC")
    protected long loc;
    @XmlElement(name = "TimeScheduled")
    protected CxDateTime timeScheduled;
    @XmlElement(name = "ElapsedTime")
    protected CxDateTime elapsedTime;
    @XmlElement(name = "TimeFinished")
    protected CxDateTime timeFinished;
    @XmlElement(name = "TimeBeginWorking")
    protected CxDateTime timeBeginWorking;
    @XmlElement(name = "CurrentStatus", required = true)
    protected CurrentStatusEnum currentStatus;
    @XmlElement(name = "ServerName")
    protected String serverName;
    @XmlElement(name = "Origin")
    protected String origin;
    @XmlElement(name = "IsPublic")
    protected boolean isPublic;
    @XmlElement(name = "PartialResults")
    protected boolean partialResults;

    /**
     * Gets the value of the currentStage property.
     *
     */
    public int getCurrentStage() {
        return currentStage;
    }

    /**
     * Sets the value of the currentStage property.
     *
     */
    public void setCurrentStage(int value) {
        this.currentStage = value;
    }

    /**
     * Gets the value of the queuePosition property.
     *
     */
    public int getQueuePosition() {
        return queuePosition;
    }

    /**
     * Sets the value of the queuePosition property.
     *
     */
    public void setQueuePosition(int value) {
        this.queuePosition = value;
    }

    /**
     * Gets the value of the totalPercent property.
     *
     */
    public int getTotalPercent() {
        return totalPercent;
    }

    /**
     * Sets the value of the totalPercent property.
     *
     */
    public void setTotalPercent(int value) {
        this.totalPercent = value;
    }

    /**
     * Gets the value of the currentStagePercent property.
     *
     */
    public int getCurrentStagePercent() {
        return currentStagePercent;
    }

    /**
     * Sets the value of the currentStagePercent property.
     *
     */
    public void setCurrentStagePercent(int value) {
        this.currentStagePercent = value;
    }

    /**
     * Gets the value of the owner property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    /**
     * Gets the value of the stageName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStageName() {
        return stageName;
    }

    /**
     * Sets the value of the stageName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStageName(String value) {
        this.stageName = value;
    }

    /**
     * Gets the value of the stageMessage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStageMessage() {
        return stageMessage;
    }

    /**
     * Sets the value of the stageMessage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStageMessage(String value) {
        this.stageMessage = value;
    }

    /**
     * Gets the value of the stepMessage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStepMessage() {
        return stepMessage;
    }

    /**
     * Sets the value of the stepMessage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStepMessage(String value) {
        this.stepMessage = value;
    }

    /**
     * Gets the value of the stepDetails property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStepDetails() {
        return stepDetails;
    }

    /**
     * Sets the value of the stepDetails property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStepDetails(String value) {
        this.stepDetails = value;
    }

    /**
     * Gets the value of the resultId property.
     *
     */
    public long getResultId() {
        return resultId;
    }

    /**
     * Sets the value of the resultId property.
     *
     */
    public void setResultId(long value) {
        this.resultId = value;
    }

    /**
     * Gets the value of the scanId property.
     *
     */
    public long getScanId() {
        return scanId;
    }

    /**
     * Sets the value of the scanId property.
     *
     */
    public void setScanId(long value) {
        this.scanId = value;
    }

    /**
     * Gets the value of the projectId property.
     *
     */
    public long getProjectId() {
        return projectId;
    }

    /**
     * Sets the value of the projectId property.
     *
     */
    public void setProjectId(long value) {
        this.projectId = value;
    }

    /**
     * Gets the value of the taskId property.
     *
     */
    public long getTaskId() {
        return taskId;
    }

    /**
     * Sets the value of the taskId property.
     *
     */
    public void setTaskId(long value) {
        this.taskId = value;
    }

    /**
     * Gets the value of the taskName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets the value of the taskName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaskName(String value) {
        this.taskName = value;
    }

    /**
     * Gets the value of the projectName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the value of the projectName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProjectName(String value) {
        this.projectName = value;
    }

    /**
     * Gets the value of the runId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRunId() {
        return runId;
    }

    /**
     * Sets the value of the runId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRunId(String value) {
        this.runId = value;
    }

    /**
     * Gets the value of the sourceId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * Sets the value of the sourceId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSourceId(String value) {
        this.sourceId = value;
    }

    /**
     * Gets the value of the loc property.
     *
     */
    public long getLOC() {
        return loc;
    }

    /**
     * Sets the value of the loc property.
     *
     */
    public void setLOC(long value) {
        this.loc = value;
    }

    /**
     * Gets the value of the timeScheduled property.
     *
     * @return
     *     possible object is
     *     {@link CxDateTime }
     *
     */
    public CxDateTime getTimeScheduled() {
        return timeScheduled;
    }

    /**
     * Sets the value of the timeScheduled property.
     *
     * @param value
     *     allowed object is
     *     {@link CxDateTime }
     *
     */
    public void setTimeScheduled(CxDateTime value) {
        this.timeScheduled = value;
    }

    /**
     * Gets the value of the elapsedTime property.
     *
     * @return
     *     possible object is
     *     {@link CxDateTime }
     *
     */
    public CxDateTime getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the value of the elapsedTime property.
     *
     * @param value
     *     allowed object is
     *     {@link CxDateTime }
     *
     */
    public void setElapsedTime(CxDateTime value) {
        this.elapsedTime = value;
    }

    /**
     * Gets the value of the timeFinished property.
     *
     * @return
     *     possible object is
     *     {@link CxDateTime }
     *
     */
    public CxDateTime getTimeFinished() {
        return timeFinished;
    }

    /**
     * Sets the value of the timeFinished property.
     *
     * @param value
     *     allowed object is
     *     {@link CxDateTime }
     *
     */
    public void setTimeFinished(CxDateTime value) {
        this.timeFinished = value;
    }

    /**
     * Gets the value of the timeBeginWorking property.
     *
     * @return
     *     possible object is
     *     {@link CxDateTime }
     *
     */
    public CxDateTime getTimeBeginWorking() {
        return timeBeginWorking;
    }

    /**
     * Sets the value of the timeBeginWorking property.
     *
     * @param value
     *     allowed object is
     *     {@link CxDateTime }
     *
     */
    public void setTimeBeginWorking(CxDateTime value) {
        this.timeBeginWorking = value;
    }

    /**
     * Gets the value of the currentStatus property.
     *
     * @return
     *     possible object is
     *     {@link CurrentStatusEnum }
     *
     */
    public CurrentStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Sets the value of the currentStatus property.
     *
     * @param value
     *     allowed object is
     *     {@link CurrentStatusEnum }
     *
     */
    public void setCurrentStatus(CurrentStatusEnum value) {
        this.currentStatus = value;
    }

    /**
     * Gets the value of the serverName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the value of the serverName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setServerName(String value) {
        this.serverName = value;
    }

    /**
     * Gets the value of the origin property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Sets the value of the origin property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrigin(String value) {
        this.origin = value;
    }

    /**
     * Gets the value of the isPublic property.
     *
     */
    public boolean isIsPublic() {
        return isPublic;
    }

    /**
     * Sets the value of the isPublic property.
     *
     */
    public void setIsPublic(boolean value) {
        this.isPublic = value;
    }

    /**
     * Gets the value of the partialResults property.
     *
     */
    public boolean isPartialResults() {
        return partialResults;
    }

    /**
     * Sets the value of the partialResults property.
     *
     */
    public void setPartialResults(boolean value) {
        this.partialResults = value;
    }

}
