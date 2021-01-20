package com.cx.restclient.dto;

/**
 * Created by: Dorg.
 * Date: 06/10/2016.
 */
public enum Status {

    IN_PROGRESS("In progress"),
    SUCCEEDED("Succeeded"),
    FAILED("Failed");


    private String value;

    public String value() {
        return value;
    }

    Status(String value) {
        this.value = value;
    }
}
