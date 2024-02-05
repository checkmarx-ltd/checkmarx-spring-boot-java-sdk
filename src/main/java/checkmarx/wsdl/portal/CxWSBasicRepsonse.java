//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-646
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.11.17 at 10:51:56 PM EST
//


package checkmarx.wsdl.portal;

import jakarta.xml.bind.annotation.*;


/**
 * <p>Java class for CxWSBasicRepsonse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CxWSBasicRepsonse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IsSuccesfull" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CxWSBasicRepsonse", propOrder = {
    "isSuccesfull",
    "errorMessage"
})
@XmlSeeAlso({
    CxWSResponseLicenseExpirationDateDetails.class,
    CxWSResponsProjectChartData.class,
    CxWSResponseGroupList.class,
    CXWSResponseResultSummary.class,
    CxWSResponseScanStatusArray.class,
    CxWSResponseProjectScannedDisplayData.class,
    CxWSResponceScanCompareResults.class,
    CxWSResponsePresetDetails.class,
    CxWSResponseScanStatus.class,
    CxTeamPathsResponseResponse.class,
    CxWSReportStatusResponse.class,
    CxWSResponseRunID.class,
    CxWSResponseFailedScansDisplayData.class,
    CxWSResponseBool.class,
    CxWSResponseDeleteScans.class,
    CxWSResponsePreset.class,
    CxWSResponseCustomFields.class,
    CxWSResponseHierarchyGroupNodes.class,
    CxWSResponseSaasPackage.class,
    CxWSResponseScansDisplayData.class,
    CxWSResponsePresetList.class,
    CxWSResponseTeamData.class,
    CxWSResponseProfileData.class,
    CxWSResponseProjectConfig.class,
    CxWSResponseCache.class,
    CxWSResponseScanCompareSummary.class,
    CxWSResponceScanResults.class,
    CxWSResponceResultPath.class,
    CxWSResponseQueriesCategories.class,
    CxWSResponseResultGraph.class,
    CxWSResponseUserData.class,
    CxWSImportQueriesRepsonse.class,
    CxWSResponseConfigSetList.class,
    CxWSResponceFileNames.class,
    CxWSCxVersionResponse.class,
    CxWSResponsePivotLayouts.class,
    CxWSResponseAssignUsers.class,
    CxWSResponseSingleUserData.class,
    CxWSResponsePendingUsersList.class,
    CxWSResponseQueryDescription.class,
    CxWSResponseProjectsScansList.class,
    CxWSResponseEngineServerId.class,
    CxWSResponseProjectsDisplayData.class,
    CxWSResponseScanLog.class,
    CxWSResponseResultStateList.class,
    CxWSResponseScanResults.class,
    CxWSResponseJSONData.class,
    CxWSResponseFileSystemLayer.class,
    CxWSCxMoveTeamResponse.class,
    CxQueryCollectionResponse.class,
    CxWSResponseInstallationSettings.class,
    CxWSResponseEngineServers.class,
    CxWSResponceQuerisForScan.class,
    CxWSResponseSystemLanguages.class,
    CxWSQueryVersionDetailsResponse.class,
    CxWSResponseNameList.class,
    CxWSResponseBasicScanData.class,
    CxWsResponseSystemSettings.class,
    CxWSUserPreferencesResponse.class,
    CxWSDataRetentionStatusResponse.class,
    CxWSResponseQueries.class,
    CxWSResponseScanSummary.class,
    CxWSResponseCustomFieldValues.class,
    CxWSResponseSourcesContent.class,
    CxWSResponseProjectsData.class,
    CxWSDataRetentionRequestResponse.class,
    CxWSResponseLDAPServersConfiguration.class,
    CxWSCreateReportResponse.class,
    CxWSResponseScanProperties.class,
    CxWSResponsePivotTable.class,
    CxWSResponseUserDirectories.class,
    CxWSResponseDomainUserList.class,
    CxWSResponsePredefinedCommands.class,
    CxWSResponseResultDescription.class,
    CxWSResponseQueueRunID.class,
    CxWSResponseShortQueryDescription.class,
    CxWSResponseCountLines.class,
    CxWSResponseResultCollection.class,
    CxWSResponseSourceActionList.class,
    CxWSResponseSourceID.class,
    CxWSResponseResultPaths.class,
    CxWSResponseResultStateUpdate.class,
    CxWSResponseLDAPServerGroups.class,
    CxWSResponseTransportedQueries.class,
    CxWSResponseDeleteProjects.class,
    CxWSResponseSourceContent.class,
    CxWSResponsProjectProperties.class,
    CxWSResponseIdNamePairList.class,
    CxWSResponseTeamLdapGroupMappingData.class,
    CxWSResponseSourceContainer.class,
    CxWSResponseSessionID.class,
    CxWSResponseUsersLicenseData.class,
    CxWSIssueTrackingSystemResponse.class
})
public class CxWSBasicRepsonse {

    @XmlElement(name = "IsSuccesfull")
    protected boolean isSuccesfull;
    @XmlElement(name = "ErrorMessage")
    protected String errorMessage;

    /**
     * Gets the value of the isSuccesfull property.
     *
     */
    public boolean isIsSuccesfull() {
        return isSuccesfull;
    }

    /**
     * Sets the value of the isSuccesfull property.
     *
     */
    public void setIsSuccesfull(boolean value) {
        this.isSuccesfull = value;
    }

    /**
     * Gets the value of the errorMessage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

}
