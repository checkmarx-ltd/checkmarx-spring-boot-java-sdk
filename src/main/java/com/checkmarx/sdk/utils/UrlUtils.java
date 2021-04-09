package com.checkmarx.sdk.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shaulv on 6/14/2018.
 */
public class UrlUtils {

    private UrlUtils() {

    }

    public boolean isValidURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return true;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }

    public String parseURLToString(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return "true";
        }
        catch (MalformedURLException e) {
            return "false";
        }
    }

    public static String parseURLToString(String hostname, String spec) throws MalformedURLException {
        String rootUri = "";
        try {
            rootUri = (new URL(new URL(hostname), spec)).toString();
        }
        catch (MalformedURLException e) {
            throw new MalformedURLException("Connection failed. Please recheck the hostname and credentials you provided and try again.");
        }
        return rootUri;
    }

}
