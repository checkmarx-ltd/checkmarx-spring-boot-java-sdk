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
/// Utility functions follow
//
def createDbConnection() {
    def dbEngine = shardProperties.getDbEngine()
    def dbUsername = shardProperties.getDbUsername()
    def dbPassword = shardProperties.getDbPassword()
    def dbHost = shardProperties.getDbHost()
    def dbName = shardProperties.getDbName()
    if (dbEngine == "postgres") {
        try{
            def cn = Class.forName('org.postgresql.Driver').getDeclaredConstructor().newInstance()
            def dbUrl = "jdbc:postgresql://${dbHost}/${dbName}"
            def props = new Properties()
            props.setProperty("user", dbUsername)
            props.setProperty("password", dbPassword)
            Connection conn = cn.connect(dbUrl, props)
            cxFlowLog.info("connected to Postgres database.")
            return conn
        } catch(Exception e) {
            println(e)
            throw new Exception("Error connect to database.")
        }
    } else if (dbEngine == "mysql") {
        try {
            def cn = Class.forName('com.mysql.jdbc.Driver').getDeclaredConstructor().newInstance()
            def dbUrl = "jdbc:mysql://${dbHost}/${dbName}"
            def props = new Properties()
            props.setProperty("user", dbUsername)
            props.setProperty("password", dbPassword)
            Connection conn = cn.connect(dbUrl, props)
            cxFlowLog.info("Connected to MySql database.")
            return conn
        } catch(Exception e) {
            println(e)
            throw new Exception("Error connect to database.")
        }
    } else if (dbEngine == "mssql") {
        try {
            def cn = Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").getDeclaredConstructor().newInstance()
            def dbUrl = "jdbc:sqlserver://${dbHost}:1433;databaseName=${dbName}"
            def props = new Properties()
            props.setProperty("user", dbUsername)
            props.setProperty("password", dbPassword)
            Connection conn = cn.connect(dbUrl, props)
            cxFlowLog.info("Connected to Microsoft SQL Server database.")
            return conn
        } catch(Exception e) {
            println(e)
            throw new Exception("Error connect to database.")
        }
    } else {
        throw new Exception("Unknown database type!")
    }
}

def closeConnection(conn) {
    conn.close()
}

def addShardProject(conn, shardID, projectName, teamName) {
    cxFlowLog.info("Creating shard project for ${shardID} project ${projectName} and team ${teamName}")
    String insertShardProjectQuery = """        
        INSERT INTO shard_to_project ( 
            shard_id, 
            project_name, 
            team_name
        ) VALUES (             
            ${shardID},
            '${projectName}',
            '${teamName}'
        )
    """
    def stmt = conn.createStatement()
    stmt.executeUpdate(insertShardProjectQuery)
    stmt.close()
}

def updateShardProject(conn, sp) {
    cxFlowLog.info("Updating shard project for project ${sp.project_name} and team ${sp.team_name}")
    String updateShardProjectQry = """        
        UPDATE shard_to_project SET
            project_name = '${sp.project_name}',
            team_name = '${sp.team_name}'            
        WHERE
            id = ${sp.id}
    """
    def stmt = conn.createStatement()
    stmt.execute(updateShardProjectQry)
    stmt.close()
}

def getShardProjects(conn, shardId) {
    String getShardProjectQry = """
        SELECT * FROM shard_to_project WHERE shard_id=${shardId}
    """
    List<ShardProject> shardProjects = []
    def stmt = conn.createStatement()
    ResultSet rs = stmt.executeQuery(getShardProjectQry)
    while (rs.next()) {
        ShardProject sp = new ShardProject()
        sp.id = rs.getInt(1)
        sp.shardId = rs.getInt(2)
        sp.projectName = rs.getString(3)
        sp.teamName = rs.getString(4)
        shardProjects.add(sp)
    }
    rs.close()
    stmt.close()
    return shardProjects
}

def updateShard(conn, shard) {
    cxFlowLog.info("Updating shard ${shard.name}")
    String updateShardQry = """        
        UPDATE shard SET
            project_cnt = ${shard.projectCnt},
            team_cnt = ${shard.teamCnt}
        WHERE
            id = ${shard.id}
    """
    def stmt = conn.createStatement()
    stmt.execute(updateShardQry)
    stmt.close()
}

def getShardList(conn) {
    String getShardListQry = """
        SELECT * FROM shard WHERE is_disabled=0
    """
    def shardList = []
    def stmt = conn.createStatement()
    ResultSet rs = stmt.executeQuery(getShardListQry)
    while (rs.next()) {
        Shard s = new Shard()
        s.id = rs.getInt(1)
        s.name = rs.getString(2)
        s.url = rs.getString(3)
        s.isDisabled = rs.getInt(4)
        s.projectLimit = rs.getInt(5)
        s.teamLimit = rs.getInt(6)
        s.projectCnt = rs.getInt(7)
        s.teamCnt = rs.getInt(8)
        s.isCredentialOverride = rs.getInt(9)
        s.shardProjects = getShardProjects(conn, s.id)
        shardList.add(s)
    }
    rs.close()
    stmt.close()
    return shardList
}

//
/// Data structures for tracking Shard information
//
class Shard {
    Integer id = 0
    String name = ""
    String url = ""
    Integer isDisabled = 0
    Integer projectLimit = 0
    Integer projectCnt = 0
    Integer teamCnt = 0
    Integer teamLimit = 0
    Integer isCredentialOverride = 0
    List<ShardProject> shardProjects = []
}

class ShardProject {
    Integer id = 0
    Integer shardId = 0
    String projectName = ""
    String teamName = ""
}
