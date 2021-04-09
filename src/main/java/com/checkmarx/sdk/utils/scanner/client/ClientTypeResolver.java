package com.checkmarx.sdk.utils.scanner.client;

import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.dto.sca.ClientType;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.utils.scanner.client.httpClient.CxHttpClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.checkmarx.sdk.config.ContentType.CONTENT_TYPE_APPLICATION_JSON_V1;

@Slf4j
public class ClientTypeResolver {
    private static final String WELL_KNOWN_CONFIG_PATH = "identity/.well-known/openid-configuration";
    private static final String SCOPES_JSON_PROP = "scopes_supported";

    private static final Set<String> scopesForCloudAuth = new HashSet<>(Arrays.asList("sca_api", "offline_access"));
    private static final Set<String> scopesForOnPremAuth = new HashSet<>(Arrays.asList("sast_rest_api", "cxarm_api"));

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CxHttpClient httpClient;

    private RestClientConfig config;

    public ClientTypeResolver(RestClientConfig config) {
        this.config = config;
    }

    /**
     * Determines which scopes and client secret must be used for SCA login.
     *
     * @param accessControlServerBaseUrl used to determine scopes supported by this server.
     * @return client settings for the provided AC server.
     */
    public ClientType determineClientType(String accessControlServerBaseUrl) {
        JsonNode response = getConfigResponse(accessControlServerBaseUrl);
        Set<String> supportedScopes = getSupportedScopes(response);
        Set<String> scopesToUse = getScopesForAuth(supportedScopes);

        String clientSecret = scopesToUse.equals(scopesForOnPremAuth) ? ClientType.RESOURCE_OWNER.getClientSecret() : "";

        String scopesForRequest = String.join(" ", scopesToUse);

        return ClientType.builder().clientId(ClientType.RESOURCE_OWNER.getClientId())
                .scopes(scopesForRequest)
                .clientSecret(clientSecret)
                .build();
    }

    private Set<String> getScopesForAuth(Set<String> supportedScopes) {
        Set<String> result;
        if (supportedScopes.containsAll(scopesForCloudAuth)) {
            result = scopesForCloudAuth;
        } else if (supportedScopes.containsAll(scopesForOnPremAuth)) {
            result = scopesForOnPremAuth;
        } else {
            String message = String.format("Access control server doesn't support the necessary scopes (either %s or %s)." +
                            " It only supports the following scopes: %s.",
                    scopesForCloudAuth,
                    scopesForOnPremAuth,
                    supportedScopes);

            throw new ScannerRuntimeException(message);
        }
        log.debug(String.format("Using scopes: %s", result));
        return result;
    }

    private JsonNode getConfigResponse(String accessControlServerBaseUrl) {
        try {
            String res = getHttpClient(accessControlServerBaseUrl).getRequest(WELL_KNOWN_CONFIG_PATH, CONTENT_TYPE_APPLICATION_JSON_V1, String.class, 200, "Get openId configuration", false);
            return objectMapper.readTree(res);
        } catch (Exception e) {
            throw new ScannerRuntimeException("Error getting OpenID config response.", e);
        }
    }

    private CxHttpClient getHttpClient(String acBaseUrl) {
        if (httpClient == null) {
            httpClient = new CxHttpClient(
                    StringUtils.appendIfMissing(acBaseUrl, "/"),
                    config.isDisableCertificateValidation(),
                    log);
        }
        return httpClient;
    }

    private static Set<String> getSupportedScopes(JsonNode response) {
        Set<String> result = null;
        if (response != null) {
            TypeReference<Set<String>> typeRef = new TypeReference<Set<String>>() {
            };
            result = objectMapper.convertValue(response.get(SCOPES_JSON_PROP), typeRef);
        }
        return Optional.ofNullable(result).orElse(new HashSet<>());
    }

}
