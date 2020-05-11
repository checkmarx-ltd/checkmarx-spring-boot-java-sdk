package com.checkmarx.sdk.dto.sca;

import com.cx.restclient.sca.dto.report.DependencyPath;
import com.cx.restclient.sca.dto.report.PackageUsage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Package {
    public String id;
    public String name;
    public String version;
    public List<String> licenses = new ArrayList<>();
    public String matchType;
    public int highVulnerabilityCount;
    public int mediumVulnerabilityCount;
    public int lowVulnerabilityCount;
    public int ignoredVulnerabilityCount;
    public int numberOfVersionsSinceLastUpdate;
    public String newestVersionReleaseDate;
    public String newestVersion;
    public boolean outdated;
    public String releaseDate;
    public String confidenceLevel;
    public double riskScore;
    public String severity;
    public List<String> locations = new ArrayList<>();
    public List<DependencyPath> dependencyPaths = new ArrayList<>();
    public String packageRepository;
    public boolean isDirectDependency;
    public boolean isDevelopment;
    public PackageUsage packageUsage;
}
