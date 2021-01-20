package com.cx.restclient.ast.dto.sca.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Info about a package that SCA retrieves by analyzing project dependencies.
 */
@Getter
@Setter
public class Package implements Serializable {
    private String id;
    private String name;
    private String version;
    private List<String> licenses = new ArrayList<>();

    /**
     * The current values are [Filename, Sha1]. Not considered an enum in SCA API.
     */
    private String matchType;

    private int highVulnerabilityCount;
    private int mediumVulnerabilityCount;
    private int lowVulnerabilityCount;
    private int ignoredVulnerabilityCount;
    private int numberOfVersionsSinceLastUpdate;
    private String newestVersionReleaseDate;
    private String newestVersion;
    private boolean outdated;
    private String releaseDate;
    private String confidenceLevel;
    private double riskScore;
    private PackageSeverity severity;
    private List<String> locations = new ArrayList<>();
    private List<DependencyPath> dependencyPaths = new ArrayList<>();
    private String packageRepository;
    private boolean isDirectDependency;
    private boolean isDevelopment;
    private PackageUsage packageUsage;
}
