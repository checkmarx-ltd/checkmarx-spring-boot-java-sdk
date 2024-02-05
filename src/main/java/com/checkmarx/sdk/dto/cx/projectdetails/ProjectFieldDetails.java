
package com.checkmarx.sdk.dto.cx.projectdetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Generated;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "teamId",
    "name",
    "isPublic",
    "customFields",
    "links",
    "owner",
    "isDeprecated",
    "projectQueueSettings",
    "isBranched",
    "originalProjectId",
    "branchedOnScanId",
    "relatedProjects"
})
@Generated("jsonschema2pojo")
public class ProjectFieldDetails {

    @JsonProperty("id")
    @JsonIgnore
    private Integer id;
    @JsonProperty("teamId")
    @JsonIgnore
    private Integer teamId;
    @JsonProperty("name")
    @JsonIgnore
    private String name;
    @JsonProperty("isPublic")
    @JsonIgnore
    private Boolean isPublic;
    @JsonProperty("customFields")
    private List<CustomField> customFields;
    @JsonProperty("links")
    @JsonIgnore
    private List<Link> links;
    @JsonProperty("owner")
    @JsonIgnore
    private String owner;
    @JsonProperty("isDeprecated")
    @JsonIgnore
    private Boolean isDeprecated;
    @JsonProperty("projectQueueSettings")
    @JsonIgnore
    private ProjectQueueSettings projectQueueSettings;
    @JsonProperty("isBranched")
    @JsonIgnore
    private Boolean isBranched;
    @JsonProperty("originalProjectId")
    @JsonIgnore
    private String originalProjectId;
    @JsonProperty("branchedOnScanId")
    @JsonIgnore
    private String branchedOnScanId;
    @JsonProperty("relatedProjects")
    @JsonIgnore
    private List<Object> relatedProjects;


    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("teamId")
    public Integer getTeamId() {
        return teamId;
    }

    @JsonProperty("teamId")
    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("isPublic")
    public Boolean getIsPublic() {
        return isPublic;
    }

    @JsonProperty("isPublic")
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @JsonProperty("customFields")
    public List<CustomField> getCustomFields() {
        return customFields;
    }

    @JsonProperty("customFields")
    public void setCustomFields(List<CustomField> customFields) {
        this.customFields = customFields;
    }

    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty("owner")
    public String getOwner() {
        return owner;
    }

    @JsonProperty("owner")
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @JsonProperty("isDeprecated")
    public Boolean getIsDeprecated() {
        return isDeprecated;
    }

    @JsonProperty("isDeprecated")
    public void setIsDeprecated(Boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

    @JsonProperty("projectQueueSettings")
    public ProjectQueueSettings getProjectQueueSettings() {
        return projectQueueSettings;
    }

    @JsonProperty("projectQueueSettings")
    public void setProjectQueueSettings(ProjectQueueSettings projectQueueSettings) {
        this.projectQueueSettings = projectQueueSettings;
    }

    @JsonProperty("isBranched")
    public Boolean getIsBranched() {
        return isBranched;
    }

    @JsonProperty("isBranched")
    public void setIsBranched(Boolean isBranched) {
        this.isBranched = isBranched;
    }

    @JsonProperty("originalProjectId")
    public String getOriginalProjectId() {
        return originalProjectId;
    }

    @JsonProperty("originalProjectId")
    public void setOriginalProjectId(String originalProjectId) {
        this.originalProjectId = originalProjectId;
    }

    @JsonProperty("branchedOnScanId")
    public String getBranchedOnScanId() {
        return branchedOnScanId;
    }

    @JsonProperty("branchedOnScanId")
    public void setBranchedOnScanId(String branchedOnScanId) {
        this.branchedOnScanId = branchedOnScanId;
    }

    @JsonProperty("relatedProjects")
    public List<Object> getRelatedProjects() {
        return relatedProjects;
    }

    @JsonProperty("relatedProjects")
    public void setRelatedProjects(List<Object> relatedProjects) {
        this.relatedProjects = relatedProjects;
    }


}
