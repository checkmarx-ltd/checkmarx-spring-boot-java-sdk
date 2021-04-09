package com.checkmarx.sdk.utils.scanner.client.httpClient;


import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.exception.CxHTTPClientException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Galn on 06/02/2018.
 */
public abstract class HttpClientHelper {
    private HttpClientHelper() {
    }

    public static <T> T convertToObject(HttpResponse response, Class<T> responseType, boolean isCollection) throws IOException, ScannerRuntimeException {

        if (responseType != null && responseType.isInstance(response)) {
            return (T) response;
        }

        // If the caller is asking for the whole response, return the response (instead of just its entity),
        // no matter if the entity is empty.
        if (responseType != null && responseType.isAssignableFrom(response.getClass())) {
            return (T) response;
        }

        //No content
        if (responseType == null || response.getEntity() == null || response.getEntity().getContentLength() == 0) {
            return null;
        }
        ///convert to byte[]
        if (responseType.equals(byte[].class)) {
            return (T) IOUtils.toByteArray(response.getEntity().getContent());
        }
        //convert to List<T>
        if (isCollection) {
            return convertToCollectionObject(response, TypeFactory.defaultInstance().constructCollectionType(List.class, responseType));
        }

        //convert to T
        return convertToStrObject(response, responseType);
    }

    private static <T> T convertToStrObject(HttpResponse response, Class<T> valueType) throws ScannerRuntimeException {
        ObjectMapper mapper = getObjectMapper();
        try {
            if (response.getEntity() == null) {
                return null;
            }
            String json = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            if (valueType.equals(String.class)) {
                return (T) json;
            }
            return mapper.readValue(json, valueType);

        } catch (IOException e) {
            throw new ScannerRuntimeException("Failed to parse json response: " + e.getMessage());
        }
    }

    public static String convertToJson(Object o) throws ScannerRuntimeException {
        ObjectMapper mapper = getObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new ScannerRuntimeException("Failed convert object to json: " + e.getMessage());
        }
    }

    public static StringEntity convertToStringEntity(Object o) throws ScannerRuntimeException, UnsupportedEncodingException {
        return new StringEntity(convertToJson(o));
    }

    private static <T> T convertToCollectionObject(HttpResponse response, JavaType javaType) throws ScannerRuntimeException {
        ObjectMapper mapper = getObjectMapper();
        try {
            String json = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new ScannerRuntimeException("Failed to parse json response: " + e.getMessage(), e);
        }
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper result = new ObjectMapper();

        // Prevent UnrecognizedPropertyException if additional fields are added to API responses.
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return result;
    }

    public static void validateResponse(HttpResponse response, int expectedStatus, String message) throws ScannerRuntimeException {
        int actualStatusCode = response.getStatusLine().getStatusCode();
        if (actualStatusCode != expectedStatus) {
            String responseBody = extractResponseBody(response);
            String readableBody = responseBody.replace("{", "")
                    .replace("}", "")
                    .replace(System.getProperty("line.separator"), " ")
                    .replace("  ", "");

            String exceptionMessage = String.format("Status code: %d, message: '%s', response body: %s",
                    actualStatusCode, message, readableBody);

            throw new CxHTTPClientException(actualStatusCode, exceptionMessage, responseBody);
        }
    }

    public static String extractResponseBody(HttpResponse response) {
        try {
            return IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        } catch (Exception e) {
            return "";
        }
    }

}
