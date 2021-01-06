package com.checkmarx.shardmanager
Binding binding = new Binding();
binding.setProperty("shardProperties", shardProperties);
binding.setProperty("cxFlowLog", cxFlowLog);
GroovyShell shell = new GroovyShell(binding)
String scriptDir = shardProperties.getScriptPath();
def dbTools = shell.parse(new File("${scriptDir}/dbtools.groovy"))
def conn = dbTools.createDbConnection()
def shards = dbTools.getShardList(conn)
def shardFound = false
def projectShard = null
def emptyShard = null
def availableShardForTeam = null
def availableShard = null

cxFlowLog.info("Shard Manager is attempting to find available shard for team: $teamName and project: $projectName")
cxFlowLog.info("Number of Shards to scan ${shards.size()}")
//
/// Start by checking if this team was already assigned to a shard
//
shards.eachWithIndex { shard, i ->
    cxFlowLog.info("\tExamining shardName: ${shard.name}, teamLimit: ${shard.teamLimit}, projectLimit: ${shard.projectLimit}, projectCnt: ${shard.projectCnt}, teamCnt: ${shard.teamCnt}")
    if (shard.isDisabled == 0) {
        shard.shardProjects.each { project ->
            // I stopped showing currently assigned projects because it too verbos
            //cxFlowLog.info("\t\tFound assigned Project: ${project.projectName} for Team: ${project.teamName}")
            if (project.teamName == teamName && project.projectName == projectName) {
                cxFlowLog.info("\t\t\tMatched project with exiting shard, we're done!")
                shardFound = true
                projectShard = shard
            } else {
                // Keep track of first shard associated with the team with a free project slot, just in
                // case we need it later.
                if (project.teamName == teamName &&
                        shard.projectCnt < shard.projectLimit &&
                        availableShardForTeam == null) {
                    cxFlowLog.info("\t\t\tFound team shard with available project slot.")
                    availableShardForTeam = shard
                }
                // Is the shard not assigned to the team but also has a free team and project slot?
                if (project.teamName != teamName &&
                        shard.teamCnt < shard.teamLimit &&
                        shard.projectCnt < shard.projectLimit &&
                        availableShard == null) {
                    cxFlowLog.info("\t\t\tFound shard with available team and project slot.")
                    availableShard = shard
                }
            }
        }
        // Is shard completely empty?
        if (shard.projectCnt == 0 && shard.teamCnt == 0 && emptyShard == null) {
            cxFlowLog.info("\t\t\tFound empty shard.")
            emptyShard = shard
        }
    }
}
//
/// If shard hasn't been found for the project check if a shard with a free project
/// currently assigned to the team was found. If that fails then hopefully there's
/// a free shard without a team and project assigned. If all else fails hopefully
/// theres a shard with another team assigned.
//
if (!shardFound) {
    cxFlowLog.info("\tExisting project shard not found, trying to find available shard.")
    if (availableShardForTeam != null) {
        cxFlowLog.info("\t\tUsing shard with with current team attached.")
        projectShard = availableShardForTeam
        cxFlowLog.info("Updating shard tracking information.")
        dbTools.incShardProjectCnt(conn, projectShard)
        dbTools.addShardProject(conn, projectShard.id, projectName, teamName)
    } else if (emptyShard != null) {
        cxFlowLog.info("\t\tUsing empty shard without existing teams or projects.")
        projectShard = emptyShard
        cxFlowLog.info("Updating shard tracking information.")
        dbTools.incShardProjectCnt(conn, projectShard)
        dbTools.incShardTeamCnt(conn, projectShard)
        dbTools.addShardProject(conn, projectShard.id, projectName, teamName)
    } else if (availableShard) {
        cxFlowLog.info("\t\tUsing shard other teams.")
        projectShard = availableShard
        cxFlowLog.info("Updating shard tracking information.")
        dbTools.incShardProjectCnt(conn, projectShard)
        dbTools.incShardTeamCnt(conn, projectShard)
        dbTools.addShardProject(conn, projectShard.id, projectName, teamName)
    } else {
        cxFlowLog.info("\t\tERROR, All shards are out of project slots. This should be corrected but falling back to round robbin mode until it is.")
        shards.eachWithIndex { shard, i ->
            if (shard.isDisabled == 0) {
                if(projectShard == null) {
                    projectShard = shard
                } else if(shard.projectCnt < projectShard.projectCnt) {
                    projectShard = shard
                }
            }
        }
        // OK, lets update the project shard for whatever one that was picked.
        cxFlowLog.info("Updating shard tracking information.")
        dbTools.incShardProjectCnt(conn, projectShard)
        // Check if the team is already on the project
        def teamFound = false
        projectShard.shardProjects.each { project ->
            if (project.teamName == teamName) {
                teamFound = true
            }
        }
        if (!teamFound) {
            dbTools.incShardTeamCnt(conn, projectShard)
        }
        dbTools.addShardProject(conn, projectShard.id, projectName, teamName)
    }
}
//
/// Now know the shard to work with. Simply send its URL back to the client
//
cxFlowLog.info("Returning results for ${projectShard.name}")
isCredentialOverride = (projectShard.isCredentialOverride == 0) ? false : true
shardName = projectShard.name
url = projectShard.url

