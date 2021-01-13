package com.cx.restclient.sca.utils.fingerprints;

import java.util.ArrayList;
import java.util.List;

public class CxSCAFileFingerprints {
    private String path;
    private long size;
    private List<CxSCAFileSignature> signatures = new ArrayList<>();


    public CxSCAFileFingerprints(String path, long size, List<CxSCAFileSignature> sig) {
        this.path = path;
        this.size = size;
        this.signatures = sig;
    }

    public CxSCAFileFingerprints(String path, long size) {
        this.path = path;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public List<CxSCAFileSignature> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<CxSCAFileSignature> signatures) {
        this.signatures = signatures;
    }

    public void addFileSignature(CxSCAFileSignature signature){
        this.signatures.add(signature);
    }
}
