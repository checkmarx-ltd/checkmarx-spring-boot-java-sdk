package com.cx.restclient.sca.utils.fingerprints;

import java.util.ArrayList;
import java.util.List;

public class CxSCAScanFingerprints {

    private String version;
    private String time;
    private List<CxSCAFileFingerprints> fingerprints = new ArrayList<>();


    public CxSCAScanFingerprints(){
        version = "1.0.0";
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<CxSCAFileFingerprints> getFingerprints() {
        return fingerprints;
    }

    public void addFileFingerprints(CxSCAFileFingerprints fileFingerprints){
        fingerprints.add(fileFingerprints);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
