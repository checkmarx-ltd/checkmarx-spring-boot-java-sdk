package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.Constants;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.cx.CxScanSettings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScanSettingsClientImpl implements ScanSettingsClient {
    private static final String SCAN_SETTINGS = "/sast/scanSettings";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final CxProperties cxProperties;
    private final CxAuthClient authClient;

    @Override
    public int createScanSettings(int projectId, int presetId, int engineConfigId) {
        CxScanSettings scanSettings = CxScanSettings.builder()
                .projectId(projectId)
                .engineConfigurationId(engineConfigId)
                .presetId(presetId)
                .build();
        HttpEntity<CxScanSettings> requestEntity = new HttpEntity<>(scanSettings, authClient.createAuthHeaders());

        log.info("Creating ScanSettings for project Id {}", projectId);
        try {
            String response = restTemplate.postForObject(cxProperties.getUrl().concat(SCAN_SETTINGS), requestEntity, String.class);
            JSONObject obj = new JSONObject(response);
            String id = obj.get("id").toString();
            return Integer.parseInt(id);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while creating ScanSettings for project {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Constants.UNKNOWN_INT;
    }

    @Override
    public String getScanSettings(int projectId) {
        HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());

        log.info("Retrieving ScanSettings for project Id {}", projectId);
        try {
            String url = cxProperties.getUrl().concat(SCAN_SETTINGS.concat("/{id}"));
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, projectId);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving ScanSettings for project {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    public CxScanSettings getScanSettingsDto(int projectId) {
        String jsonResponse = getScanSettings(projectId);
        CxScanSettings result = null;
        try {
            JsonNode response = objectMapper.readTree(jsonResponse);

            result = CxScanSettings.builder()
                    .projectId(projectId)
                    .presetId(response.at("/preset/id").asInt())
                    .engineConfigurationId(response.at("/engineConfiguration/id").asInt())
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Error parsing scan settings response.", e);
        }
        return result;
    }
}
