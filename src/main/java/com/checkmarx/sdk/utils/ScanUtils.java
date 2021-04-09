package com.checkmarx.sdk.utils;

import com.checkmarx.sdk.dto.sast.CxConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ScanUtils {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ScanUtils.class);

    public ScanUtils() {
    }

    /**
     * Function used to determine if file extension of full filename is preset in list
     *
     * @param list
     * @param value - extension of file, or full filename
     * @return
     */
    protected static boolean fileListContains(List<String> list, String value){
        for(String s: list){
            if(s.endsWith(value)){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if string is empty or null
     * @param str
     * @return
     */
    public static boolean empty(String str) {
        return str == null || str.isEmpty();
    }

    protected static boolean anyEmpty(String ...str){
        for(String s : str)
            if (empty(s)) {
                return true;
            }
        return false;
    }
    /**
     * Check if list is empty or null
     * @param list
     * @return
     */
    public static boolean empty(List list) {
        if (list == null) {
            return true;
        } else return list.isEmpty();
    }

    public static boolean emptyObj(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof List) {
            return ScanUtils.empty((List)object);
        }
        else if (object instanceof String) {
            return ScanUtils.empty((String)object);
        }
        return false;
    }

    public static String cleanStringUTF8(String dirty){
        log.debug(""+dirty.length());
        return new String(dirty.getBytes(), 0, dirty.length(), UTF_8);
    }

    public static String cleanStringUTF8_2(String dirty){
        return new String(dirty.getBytes(), UTF_8);
    }

    public static void writeByte(String filename, byte[] bytes) {
        try {
            OutputStream os = new FileOutputStream(new File(filename));
            os.write(bytes);
            os.close();
        }
        catch (IOException e) {
            log.error("Error while writing file {} - {}", filename, ExceptionUtils.getMessage(e));
        }
    }
    /**
     * Returns the protocol, host and port from given url.
     *
     * @param url url to process
     * @return  host with protocol and port
     */
    public static String getHostWithProtocol(String url) {
        String hostWithProtocol = null;
        try {
            URI uri = new URI(url);
            int port = uri.getPort();
            hostWithProtocol = uri.getScheme() + "://"  + uri.getHost() + (port > 0 ? ":" + port : "");
        } catch (URISyntaxException e) {
            log.debug("Could not parse given URL" + url, e);
        }
        return hostWithProtocol;
    }


    public static CxConfig getConfigAsCode(File jsonConfig){
        try {
            String config = new String(Files.readAllBytes(jsonConfig.toPath()));
            return getConfigAsCode(config);
        } catch (IOException e) {
           log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static CxConfig getConfigAsCode(String jsonConfig){
        log.debug("Loading CxConfig: {}", jsonConfig);
        CxConfig cxConfig = null;
        ObjectMapper mapper = new ObjectMapper();
        //if override is provided, check if chars are more than 20 in length, implying base64 encoded json
        try {
            cxConfig = mapper.readValue(jsonConfig, CxConfig.class);
        }catch (IOException e){
            log.warn("Error parsing CxConfig file: {}", ExceptionUtils.getRootCauseMessage(e));
        }
        return cxConfig;
    }
}
