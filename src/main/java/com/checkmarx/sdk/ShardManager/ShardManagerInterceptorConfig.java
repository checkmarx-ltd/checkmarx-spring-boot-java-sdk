package com.checkmarx.sdk.ShardManager;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.config.ShardProperties;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ShardManagerInterceptorConfig {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ShardManagerHTTPInterceptor.class);
    private CxProperties cxProperties;
    private ShardProperties shardProperties;
    private ShardSessionTracker sessionTracker;

    public ShardManagerInterceptorConfig(ShardProperties shardProperties,
                                         CxProperties cxProperties,
                                         ShardSessionTracker sessionTracker) {
        this.shardProperties = shardProperties;
        this.cxProperties = cxProperties;
        this.sessionTracker = sessionTracker;
    }

    @Bean
    public RestTemplate restTemplate(@Qualifier("cxRestTemplate") RestTemplate restTemplate,
                                     ShardProperties shardProperties) {
        if(cxProperties.getEnableShardManager()) {
            loadShardSettings();
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            if (CollectionUtils.isEmpty(interceptors)) {
                interceptors = new ArrayList<>();
            }
            interceptors.add(new ShardManagerHTTPInterceptor(shardProperties, sessionTracker));
            restTemplate.setInterceptors(interceptors);
        }
        return restTemplate;
    }

    public String loadShardSettings() {
        Binding binding = new Binding();
        binding.setProperty("shardProperties", shardProperties);
        binding.setProperty("cxFlowLog", log);
        try {
            String scriptName = shardProperties.getScriptSetup();
            String scriptDir = shardProperties.getScriptPath();
            String[] roots = new String[] { scriptDir };
            GroovyScriptEngine gse = new GroovyScriptEngine(roots);
            gse.run(scriptName, binding);
            return (String)binding.getVariable("output");
        } catch (GroovyRuntimeException | IOException | ResourceException | ScriptException e) {
            log.error("Error occurred while executing Shard Manager Setup, returning null - {}", ExceptionUtils.getMessage(e), e);
            return null;
        }
    }
}
