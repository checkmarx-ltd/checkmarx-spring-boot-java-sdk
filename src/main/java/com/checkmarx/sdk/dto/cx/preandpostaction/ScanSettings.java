package com.checkmarx.sdk.dto.cx.preandpostaction;

import com.checkmarx.sdk.dto.cx.CxEmailNotifications;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
//class EmailNotifications{
//    public ArrayList<String> failedScan;
//    public ArrayList<String> beforeScan;
//    public ArrayList<String> afterScan;
//}

 class EngineConfiguration{
    public int id;
    public Link link;
}

 class Link{
    public String rel;
    public String uri;
}

 class PostScanAction{
    public int id;
    public Link link;
}

 class Preset{
    public int id;
    public Link link;
}

 class Project{
    public int id;
    public Link link;
}

@JsonIgnoreProperties(value = { "postScanActionConditions","postScanActionArguments" })
public class ScanSettings{
    public Project project;
    public Preset preset;
    public EngineConfiguration engineConfiguration;
    public PostScanAction postScanAction;
    @Getter
    public CxEmailNotifications emailNotifications;
    public String postScanActionData;

    public ScanSettings() {
    }

    @Getter
    public String postScanActionName;


}



