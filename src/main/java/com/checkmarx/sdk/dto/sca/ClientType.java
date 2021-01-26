package com.checkmarx.sdk.dto.sca;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ClientType {

    public static final ClientType RESOURCE_OWNER = new ClientType("resource_owner_client",
            "sast_rest_api cxarm_api",
            "014DF517-39D1-4453-B7B3-9930C563627C",
            null);

    public static final ClientType CLI = new ClientType("cli_client",
            "sast_rest_api offline_access",
            "B9D84EA8-E476-4E83-A628-8A342D74D3BD",
            null);

    private String clientId;
    private String scopes;
    private String clientSecret;
    private String grantType;
}
