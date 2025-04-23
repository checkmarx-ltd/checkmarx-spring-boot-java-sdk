package com.checkmarx.sdk.dto.sca.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Info about a package that SCA retrieves by analyzing project dependencies.
 */
@Getter
public class Package implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Package.class);
    private String id;
    private String name;
    private String version;
    private List<String> licenses = new ArrayList<>();
    private String matchType;
    private String newestVersionReleaseDate;
    private String newestVersion;
    private String releaseDate;
    private String confidenceLevel;
    private PackageSeverity severity;
    private List<String> locations = new ArrayList<>();
    private List<DependencyPath> dependencyPaths = new ArrayList<>();
    private String packageRepository;
    private PackageUsage packageUsage;
    @Setter
    private int criticalVulnerabilityCount;
    @Setter
    private int highVulnerabilityCount;
    @Setter
    private int mediumVulnerabilityCount;
    @Setter
    private int lowVulnerabilityCount;
    @Setter
    private int ignoredVulnerabilityCount;
    @Setter
    private int numberOfVersionsSinceLastUpdate;
    @Setter
    private boolean outdated;
    @Setter
    private double riskScore;
    @Setter
    @JsonProperty("isDirectDependency")
    private boolean IsDirectDependency;
    @Setter
    @JsonProperty("isDevelopment")
    private boolean IsDevelopmentDependency;
    @Setter
    @JsonProperty("isTestDependency")
    private boolean IsTestDependency;

    public void setId(String id) {
        logIfNull("id", id);
        this.id = id;
    }

    public void setName(String name) {
        logIfNull("name", name);
        this.name = name;
    }

    public void setVersion(String version) {
        logIfNull("version", version);
        this.version = version;
    }

    public void setLicenses(List<String> licenses) {
        logIfNull("licenses", licenses);
        this.licenses = licenses;
    }

    public void setMatchType(String matchType) {
        logIfNull("matchType", matchType);
        this.matchType = matchType;
    }

    public void setNewestVersionReleaseDate(String newestVersionReleaseDate) {
        logIfNull("newestVersionReleaseDate", newestVersionReleaseDate);
        this.newestVersionReleaseDate = newestVersionReleaseDate;
    }

    public void setNewestVersion(String newestVersion) {
        logIfNull("newestVersion", newestVersion);
        this.newestVersion = newestVersion;
    }

    public void setReleaseDate(String releaseDate) {
        logIfNull("releaseDate", releaseDate);
        this.releaseDate = releaseDate;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        logIfNull("confidenceLevel", confidenceLevel);
        this.confidenceLevel = confidenceLevel;
    }

    public void setSeverity(PackageSeverity severity) {
        logIfNull("severity", severity);
        this.severity = severity;
    }

    public void setLocations(List<String> locations) {
        logIfNull("locations", locations);
        this.locations = locations;
    }

    public void setDependencyPaths(List<DependencyPath> dependencyPaths) {
        logIfNull("dependencyPaths", dependencyPaths);
        this.dependencyPaths = dependencyPaths;
    }

    public void setPackageRepository(String packageRepository) {
        logIfNull("packageRepository", packageRepository);
        this.packageRepository = packageRepository;
    }

    public void setPackageUsage(PackageUsage packageUsage) {
        logIfNull("packageUsage", packageUsage);
        this.packageUsage = packageUsage;
    }

    private void logIfNull(String fieldName, Object value) {
        if (value == null) {
            logger.warn("field '{}' in Package class with id={} and name={} has null value", fieldName,this.id,this.name);
        }
    }
}
