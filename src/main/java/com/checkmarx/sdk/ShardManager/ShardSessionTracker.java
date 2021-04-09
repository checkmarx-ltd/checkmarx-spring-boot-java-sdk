package com.checkmarx.sdk.ShardManager;
import com.checkmarx.sdk.config.ShardProperties;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import java.util.HashMap;

@Component
public class ShardSessionTracker {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ShardManagerHTTPInterceptor.class);
    private WebServiceTemplate ws;
    private ShardProperties shardProperties;
    // Tracks shards assigned to current scan sessions
    private HashMap<String, ShardSession> shardTracker;

    public ShardSessionTracker(ShardProperties shardProperties, WebServiceTemplate ws) {
        this.ws = ws;
        this.shardTracker = new HashMap<>();
        this.shardProperties = shardProperties;
    }

    public ShardSession getShardSession() {
        String scanID = MDC.get("cx");
        if (!shardTracker.containsKey(scanID)) {
            ShardSession session = new ShardSession(ws);
            this.shardTracker.put(scanID, session);
        }
        return shardTracker.get(scanID);
    }
}
