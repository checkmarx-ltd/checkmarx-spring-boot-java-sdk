package com.checkmarx.sdk.dto.cx.preandpostaction;


import lombok.Getter;

public class CustomTaskByName{
    @Getter
    public int id;
    public String name;
    public String type;
    public String data;
    public Link link;

    public CustomTaskByName() {
    }
}


