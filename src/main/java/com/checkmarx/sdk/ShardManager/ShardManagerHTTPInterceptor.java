package com.checkmarx.sdk.ShardManager;
import com.checkmarx.sdk.config.ShardProperties;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShardManagerHTTPInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ShardManagerHTTPInterceptor.class);
    private ShardProperties shardProperties;
    private ShardSessionTracker sessionTracker;
    //
    /// predefined URI path strings
    //
    private static String authReq = "/cxrestapi/auth/identity/connect/token";

    public ShardManagerHTTPInterceptor(ShardProperties shardProperties,
                                       ShardSessionTracker sessionTracker) {
        this.shardProperties = shardProperties;
        this.sessionTracker = sessionTracker;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        ShardSession shard = sessionTracker.getShardSession();
        if (!shard.getShardFound() && request.getURI().getPath().equals(authReq)) {
            runShardManager(sessionTracker.getScanRequestID(), shard);
            body = overrideCredentials(request, body, shard);
            ClientHttpResponse clientResp = execution.execute(new ShardRequestWrapper(request, shard.getUrl()), body);
            return clientResp;
        }
        else {
            return execution.execute(new ShardRequestWrapper(request, shard.getUrl()), body);
        }
    }

    //
    /// This will only change the payload if the request path indicates an auth request
    /// ADD isIncrementalOverride is turned on for the current shard.
    //
    public byte[] overrideCredentials(HttpRequest request, byte []body, ShardSession session) {
        if (request.getURI().getPath().equals(authReq) && session.getIsCredentialOverride()) {
            try {
                // Breakdown the payload and then recompose it with the new credentials
                String bodyStr = new String(body, "UTF-8");
                String[] tokens = bodyStr.split("&");
                String newBody = "";
                for (int x=0; x < tokens.length; x++) {
                    if (x == 0) {
                        String usernameEnc = URLEncoder.encode(session.getUsername(), "UTF-8");
                        newBody += ("username=" + usernameEnc);
                    }
                    if (x == 1) {
                        String passwordEnc = URLEncoder.encode(session.getPassword(), "UTF-8");
                        newBody += ("&password=" + passwordEnc);
                    }
                    if (x > 1) {
                        newBody += ("&" + tokens[x]);
                    }
                }
                return newBody.getBytes();
            } catch(UnsupportedEncodingException e) {
                log.error("Error decoding request body, returning original content - {}", ExceptionUtils.getMessage(e), e);
                return body;
            }
        }
        else {
            return body;
        }
    }

    public void runShardManager(String scanID, ShardSession shard) {
        Binding bindings = new Binding();
        bindings.setProperty("shardProperties", shardProperties);
        bindings.setProperty("cxFlowLog", log);
        bindings.setVariable("teamName", shard.getTeam());
        bindings.setVariable("projectName", shard.getProject());
        try {
            String scriptName = shardProperties.getScriptName();
            String scriptDir = shardProperties.getScriptPath();
            String[] roots = new String[] { scriptDir };
            GroovyScriptEngine gse = new GroovyScriptEngine(roots);
            gse.run(scriptName, bindings);
            shard.setShardFound(true);
            shard.setUrl((String)bindings.getVariable("url"));
            shard.setName((String)bindings.getVariable("shardName"));
            shard.setIsCredentialOverride((boolean)bindings.getVariable("isCredentialOverride"));
            // Examin the local shard list for username/password values
            List<ShardConfig> shardProps = shardProperties.getShardConfig();
            for(int i=0; i < shardProps.size(); i++) {
                ShardConfig shardProp = shardProps.get(i);
                if (shardProp.getName().equals(shard.getName()) ) {
                    shard.setUsername(shardProp.getUsername());
                    shard.setPassword(shardProp.getPassword());
                }
            }
        } catch (GroovyRuntimeException | IOException | ResourceException | ScriptException e) {
            log.error("Error occurred while executing Shard Manager, returning null - {}", ExceptionUtils.getMessage(e), e);
        }
    }

    //
    /// You cannot directly alter the URI associated with an HttpRequest and the URI needs to altered based on the
    /// picked shard. This simply handles the rewrite process.
    //
    private class ShardRequestWrapper extends HttpRequestWrapper {
        HttpRequest request;
        String shardAddress;

        public ShardRequestWrapper(HttpRequest request, String shardAddress) {
            super(request);
            this.request = request;
            this.shardAddress = shardAddress;
        }

        @Override
        public String getMethodValue() { return super.getMethodValue(); }

        @Override
        public HttpHeaders getHeaders() {
            return this.request.getHeaders();
        }

        @Override
        public URI getURI() {
            try {
                String rewrittenURI = shardAddress + super.getURI().getPath();
                if (super.getURI().getQuery() != null) {
                    rewrittenURI = rewrittenURI + "?" + super.getURI().getQuery();
                }
                return new URI(rewrittenURI);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}