package com.checkmarx.sdk.dto.sca.report;

import com.checkmarx.sdk.dto.scansummary.Severity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This entity is called vulnerability in SCA API, but here it is called Finding for consistency.
 * Indicates a specific type of vulnerability detected in a specific package.
 */
@Getter
public class Finding implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Finding.class);
    private String id;
    private String cveName;
    private double score;
    private Severity severity;
    private String publishDate;
    private List<String> references = new ArrayList<>();
    private String description;
    private String recommendations;
    private String packageId;
    private String similarityId;
    private String fixResolutionText;
    @JsonProperty("isIgnored")
    private boolean isIgnored;
    @JsonProperty("isViolatingPolicy")
    private boolean isViolatingPolicy;

    // Setters with null check logging
    public void setId(String id) {
        logIfNull("id", id);
        this.id = id;
    }

    public void setCveName(String cveName) {
        logIfNull("cveName", cveName);
        this.cveName = cveName;
    }

    public void setSeverity(Severity severity) {
        logIfNull("severity", severity);
        this.severity = severity;
    }

    public void setPublishDate(String publishDate) {
        logIfNull("publishDate", publishDate);
        this.publishDate = publishDate;
    }

    public void setReferences(List<String> references) {
        logIfNull("references", references);
        this.references = references;
    }

    public void setDescription(String description) {
        logIfNull("description", description);
        this.description = description;
    }

    public void setRecommendations(String recommendations) {
        logIfNull("recommendations", recommendations);
        this.recommendations = recommendations;
    }

    public void setPackageId(String packageId) {
        logIfNull("packageId", packageId);
        this.packageId = packageId;
    }

    public void setSimilarityId(String similarityId) {
        logIfNull("similarityId", similarityId);
        this.similarityId = similarityId;
    }

    public void setFixResolutionText(String fixResolutionText) {
        logIfNull("fixResolutionText", fixResolutionText);
        this.fixResolutionText = fixResolutionText;
    }

    // no null check needed for primitives
    public void setScore(double score) {
        this.score = score;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

    public void setViolatingPolicy(boolean violatingPolicy) {
        isViolatingPolicy = violatingPolicy;
    }

    private void logIfNull(String fieldName, Object value) {
        if (value == null) {
            logger.warn("field '{}' in Finding class with id={} has null value", fieldName,this.id);
        }
    }
}

