package com.checkmarx.sdk.config;
import com.checkmarx.sdk.ShardManager.ShardConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "shardmanager")
public class ShardProperties {
    private Double version = 8.9;
    private String dbEngine = "postgres";
    private String dbHost = "";
    private String dbName = "";
    private String dbUsername = "";
    private String dbPassword = "";
    private String scriptPath = "";
    private String scriptName = "ScriptManager.groovy";
    private String scriptSetup = "ShardSetup.groovy";
    private List<ShardConfig> shardConfig;

    public List<ShardConfig> getShardConfig() {
        return this.shardConfig;
    }

    public void setShardConfig(List<ShardConfig> shardConfig) {
        this.shardConfig = shardConfig;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public String getDbEngine() { return this.dbEngine; }

    public void setDbEngine(String dbEngine) { this.dbEngine = dbEngine; }

    public String getDbHost() { return this.dbHost; }

    public void setDbHost(String dbHost) { this.dbHost = dbHost; }

    public String getDbName() { return this.dbName; }

    public void setDbName(String dbName) { this.dbName = dbName; }

    public String getDbUsername() { return this.dbUsername; }

    public void setDbUsername(String dbUsername) { this.dbUsername = dbUsername; }

    public String getDbPassword() { return this.dbPassword; }

    public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScriptSetup() {
        return this.scriptSetup;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getScriptPath() {
        return this.scriptPath;
    }

    public void setScriptSetup(String scriptSetup) {
        this.scriptSetup = scriptSetup;
    }
}
