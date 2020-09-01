package com.checkmarx.sdk.ShardManager;

import com.checkmarx.sdk.config.ShardProperties;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
        String scanID = getScanRequestID();
        if (!shardTracker.containsKey(scanID)) {
            ShardSession session = new ShardSession(ws);
            this.shardTracker.put(scanID, session);
        }
        return shardTracker.get(scanID);
    }

    public String getScanRequestID() {
        return captureScanRequestID(() -> {
            this.log.info("Find Scan ID.");
        });
    }

    private String captureScanRequestID(Runnable r) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = System.out;
        try {
            System.setOut(new PrintStream(baos, true, StandardCharsets.UTF_8.name()));
            r.run();
            String tokenStr = new String(baos.toByteArray());
            return tokenStr.substring(174, 183);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("End of the world, Java doesn't recognise UTF-8");
        } finally {
            System.setOut(out);
        }
    }
}
