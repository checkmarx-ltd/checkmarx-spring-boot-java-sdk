package com.checkmarx.shardmanager
@Grapes([
  @Grab('org.postgresql:postgresql:42.2.16'),
  @Grab('mysql:mysql-connector-java:5.1.39'),
  //
  /// NOTE: you need to load the correct SQL Server driver for the JRE you're using
  //
  @Grab('com.microsoft.sqlserver:mssql-jdbc:8.4.1.jre11')
  //@Grab('com.microsoft.sqlserver:mssql-jdbc:8.4.1.jre8')
])
import java.sql.*

//
/// Setup the working environment
//
Binding binding = new Binding();
binding.setProperty("shardProperties", shardProperties);
binding.setProperty("cxFlowLog", cxFlowLog);
cxFlowLog.info("Trying to CxFlow thing")

GroovyShell shell = new GroovyShell(binding)
String scriptDir = shardProperties.getScriptPath();
cxFlowLog.info("Running Shard Setup Script.")
def dbTools = shell.parse(new File("${scriptDir}/dbtools.groovy"))
def shardConfig = shardProperties.getShardConfig()
def conn = dbTools.createDbConnection()
//
/// Verify the required tables exist and create them if needed
//
if (!doesShardTableExist(conn)) createShardsTable(conn)
if (!doesShardProjectTableExist(conn)) createShardProjectTable(conn)

//
/// Import or update shard data configurations.
//
Integer shardCnt = 0
for (; shardCnt < shardConfig.size(); shardCnt++) {
    if (doesShardExist(conn, shardConfig[shardCnt].getName())) {
        if (shardConfig[shardCnt].getForceSettingReload() == 1) {
            updateShard(conn, shardConfig[shardCnt])
        }
    }
    else {
        createShard(conn, shardConfig[shardCnt])
    }
}

//
/// Cleanup after a successful run
//
dbTools.closeConnection(conn)

output = "Success"

//
/// Utility functions follow
//
def doesShardProjectExist(conn, shardID) {
    String mySqlQueryShard = """
        SELECT count(*) FROM shard_to_project WHERE shard_id='${shardID}'
    """
    def exists = false;
    def stmt = conn.createStatement()
    ResultSet rs = stmt.executeQuery(mySqlQueryShard)
    if (rs.next()) {
        if (rs.getInt(1)) exists = true
    }
    rs.close()
    stmt.close()
    return exists
}

def doesShardProjectTableExist(conn) {
    String existsQry = """
        SELECT 1 as table_found FROM information_schema.tables WHERE TABLE_NAME = 'shard_to_project'
    """
    def exists = false
    def stmt = conn.createStatement()
    ResultSet rs = stmt.executeQuery(existsQry)
    if (rs.next()) {
        if (rs.getInt(1) == 1) exists = true
    }
    rs.close()
    stmt.close()
    return exists
}

def createShardProjectTable(conn) {
    cxFlowLog.info("CxFlow Shards project table does not exist, creating it!")
    def dbEngine = shardProperties.getDbEngine()
    String msSqlCreateShardProjectTable = """
        CREATE TABLE shard_to_project (
          id int IDENTITY(1,1) PRIMARY KEY,
          shard_id int NOT NULL,
          project_name varchar(MAX) DEFAULT NULL,
          team_name varchar(MAX) DEFAULT NULL
        )
    """
    String mySqlCreateShardProjectTable = """
        CREATE TABLE `shard_to_project` (
          `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
          `shard_id` int(11) unsigned NOT NULL,
          `project_name` text DEFAULT NULL,
          `team_name` text DEFAULT NULL,
          PRIMARY KEY (`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
    """
    String pgCreateShardProjectTable = """
        CREATE SEQUENCE public.shard_to_project_id_seq
            INCREMENT 1
            START 1
            MINVALUE 1
            MAXVALUE 2147483647
            CACHE 1;
        CREATE TABLE public.shard_to_project (
            id integer NOT NULL DEFAULT nextval('shard_to_project_id_seq'::regclass),
            shard_id integer NOT NULL,
            project_name text COLLATE pg_catalog."default",
            team_name text COLLATE pg_catalog."default",            
            CONSTRAINT shard_project_pkey PRIMARY KEY (id)
        )
    """
    def stmt = conn.createStatement()
    if (dbEngine == "postgres") stmt.execute(pgCreateShardProjectTable)
    if (dbEngine == "mysql") stmt.execute(mySqlCreateShardProjectTable)
    if (dbEngine == "mssql") stmt.execute(msSqlCreateShardProjectTable)
    stmt.close()
}

def doesShardExist(conn, shardName) {
    String countShardQuery = """
        SELECT count(*) FROM shard WHERE shard_name='${shardName}'
    """
    def exists = false;
    def stmt = conn.createStatement()
    ResultSet rs = stmt.executeQuery(countShardQuery)
    if (rs.next()) {
        if (rs.getInt(1)) exists = true
    }
    rs.close()
    stmt.close()
    return exists
}

def createShard(conn, opts) {
    cxFlowLog.info("Creating shard ${opts.getName()}")
    def dbEngine = shardProperties.getDbEngine()
    String insertShardQuery = """        
        INSERT INTO shard ( 
            shard_name, 
            url, 
            is_disabled, 
            project_limit, 
            team_limit, 
            is_credential_override)
        VALUES ( 
            '${opts.getName()}',
            '${opts.getUrl()}',             
            ${opts.getIsDisabled()}, 
            ${opts.getProjectLimit()}, 
            ${opts.getTeamLimit()}, 
            ${opts.getIsCredentialOverride()});
    """
    def stmt = conn.createStatement()
    stmt.execute(insertShardQuery)
    stmt.close()
}

def updateShard(conn, opts) {
    cxFlowLog.info("Updating shard ${opts.getName()}")
    String updateShardQry = """        
        UPDATE shard SET
            url = '${opts.getUrl()}',
            is_disabled = ${opts.getIsDisabled()},
            project_limit = ${opts.getProjectLimit()},
            team_limit = ${opts.getTeamLimit()},
            is_credential_override = ${opts.getIsCredentialOverride()}
        WHERE
            shard_name = '${opts.getName()}'
    """
    def stmt = conn.createStatement()
    stmt.execute(updateShardQry)
    stmt.close()
}

def doesShardTableExist(conn) {
    String existsQry = """
        SELECT 1 as table_found FROM information_schema.tables WHERE TABLE_NAME = 'shard'
    """
    def exists = false
    def stmt = conn.createStatement()
    ResultSet rs = stmt.executeQuery(existsQry)
    if (rs.next()) {
        if (rs.getInt(1) == 1) exists = true
    }
    rs.close()
    stmt.close()
    return exists
}

def createShardsTable(conn) {
    cxFlowLog.info("CxFlow Shards table does not exist, creating it!")
    def dbEngine = shardProperties.getDbEngine()
    String msSqlCreateShardTable = """
        CREATE TABLE shard (
          id int IDENTITY(1,1) PRIMARY KEY,
          shard_name varchar(MAX) DEFAULT NULL,
          url varchar(MAX) DEFAULT NULL,
          is_disabled int DEFAULT 0,
          project_limit int DEFAULT 1,
          team_limit int DEFAULT 1,
          project_cnt int DEFAULT 0,
          team_cnt int DEFAULT 0,
          is_credential_override int DEFAULT 0
        ) 
    """
    String mySqlCreateShardTable = """
        CREATE TABLE `shard` (
          `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
          `shard_name` text DEFAULT NULL,
          `url` text DEFAULT NULL,
          `is_disabled` int(11) DEFAULT 0,
          `project_limit` int(11) DEFAULT 1,
          `team_limit` int(11) DEFAULT 1,
          `project_cnt` int(11) DEFAULT 0,
          `team_cnt` int(11) DEFAULT 0,
          `is_credential_override` int(11) DEFAULT 0,
          PRIMARY KEY (`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
    """
    String pgCreateShardTable = """
        CREATE SEQUENCE public.shard_id_seq
            INCREMENT 1
            START 1
            MINVALUE 1
            MAXVALUE 2147483647
            CACHE 1;
        CREATE TABLE public.shard (
            id integer NOT NULL DEFAULT nextval('shard_id_seq'::regclass),
            shard_name text COLLATE pg_catalog."default",
            url text COLLATE pg_catalog."default",           
            is_disabled integer NOT NULL DEFAULT 0,
            project_limit integer NOT NULL DEFAULT 1,
            team_limit integer NOT NULL DEFAULT 1,
            project_cnt integer NOT NULL DEFAULT 0,
            team_cnt integer NOT NULL DEFAULT 0,
            is_credential_override integer NOT NULL DEFAULT 0,         
            CONSTRAINT shard_pkey PRIMARY KEY (id)
        )
    """
    def stmt = conn.createStatement()
    if (dbEngine == "postgres") stmt.execute(pgCreateShardTable)
    if (dbEngine == "mysql") stmt.execute(mySqlCreateShardTable)
    if (dbEngine == "mssql") stmt.execute(msSqlCreateShardTable)
    stmt.close()
}
