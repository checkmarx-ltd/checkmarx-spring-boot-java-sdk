package com.cx.restclient.sca.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CxSCAResolvingConfiguration implements Serializable {
    private List<String> Manifests = new ArrayList<>();
    private List<String> Fingerprints = new ArrayList<>();

    public String getManifestsIncludePattern(){
        return String.join(",", Manifests);
    }

    public String getFingerprintsIncludePattern(){
        return String.join(",", Fingerprints);
    }
}
