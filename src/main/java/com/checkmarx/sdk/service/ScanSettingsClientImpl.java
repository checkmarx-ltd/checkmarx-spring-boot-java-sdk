package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.Constants;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.cx.CxPreset;
import com.checkmarx.sdk.dto.cx.CxScanSettings;
import com.checkmarx.sdk.exception.CheckmarxException;
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
    private static final String PRESETS = "/sast/presets";
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

    @Override
    public Integer getPresetId(String presetName) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        int defaultPresetId = Constants.UNKNOWN_INT;
        try {
            log.info("Retrieving Cx presets");
            ResponseEntity<CxPreset[]> response = restTemplate.exchange(cxProperties.getUrl().concat(PRESETS), HttpMethod.GET, httpEntity, CxPreset[].class);
            CxPreset[] cxPresets = response.getBody();
            if (cxPresets == null) {
                throw new CheckmarxException("Error obtaining Team Id");
            }
            for (CxPreset cxPreset : cxPresets) {
                String currentPresetName = cxPreset.getName();
                int presetId = cxPreset.getId();
                if (currentPresetName.equalsIgnoreCase(presetName)) {
                    log.info("Found preset '{}' with ID {}", presetName, presetId);
                    return cxPreset.getId();
                }
                if (currentPresetName.equalsIgnoreCase(Constants.CX_DEFAULT_PRESET)) {
                    defaultPresetId = presetId;
                }
            }
            log.warn("No Preset was found for '{}'", presetName);
            log.warn("Default Preset {} with ID {} will be used instead", Constants.CX_DEFAULT_PRESET, defaultPresetId);
            return defaultPresetId;
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving presets");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error obtaining Preset Id");
        }
    }

    @Override
    public String getPresetName(int presetId) {
        HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());

        log.info("Retrieving preset name for preset Id {}", presetId);
        try {
            ResponseEntity<String> response = restTemplate.exchange(cxProperties.getUrl().concat(PRESETS.concat("/{id}")),
                    HttpMethod.GET,
                    requestEntity,
                    String.class,
                    presetId);

            if (response.getBody() == null) {
                return null;
            }
            JSONObject obj = new JSONObject(response.getBody());
            return obj.getString("name");
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving preset for preset id {}, http error {}", presetId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    public Integer getProjectPresetId(int projectId) {
        CxScanSettings scanSettings = getScanSettingsDto(projectId);
        if (scanSettings == null) {
            return Constants.UNKNOWN_INT;
        }
        return scanSettings.getPresetId();
    }
}
