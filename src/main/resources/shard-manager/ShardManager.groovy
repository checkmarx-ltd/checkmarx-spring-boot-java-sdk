package com.checkmarx.shardmanager
Binding binding = new Binding();
binding.setProperty("shardProperties", shardProperties);
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
println "Shard Manager is attempting to find available shard for team: $teamName and project: $projectName"
println "Number of Shards to scan ${shards.size()}"
//
/// Start by checking if this team was already assigned to a shard
//
shards.eachWithIndex { shard, i ->
    println "\tExamining shardName: ${shard.name}, teamLimit: ${shard.teamLimit}, projectLimit: ${shard.projectLimit}"
    if (shard.isDisabled == 0) {
        shard.shardProjects.each { project ->
            println "\t\tFound assigned Project: ${project.projectName} for Team: ${project.teamName}"
            if (project.teamName == teamName && project.projectName == projectName) {
                println "\t\t\tMatched project with exiting shard!"
                shardFound = true
                projectShard = shard
            }
            // Keep track of first shard associated with the team with a free project slot, just in
            // case we need it later.
            if (project.teamName == teamName && shard.projectCnt < shard.projectLimit && availableShardForTeam == null) {
                println "\t\t\tFound team shard with available project slot."
                availableShardForTeam = shard
            }
            // Is the shard not assigned to the team but also has a free team and project slot?
            if (project.teamName != teamName && shard.teamCnt < shard.teamLimit && shard.projectCnt < shard.projectLimit && availableShard == null) {
                println "\t\t\tFound shard with available team and project slot."
                availableShard = shard
            }
        }
        println "\tprojectCnt: ${shard.projectCnt}, teamCnt: ${shard.teamCnt}"
        // Is shard completely empty?
        if (shard.projectCnt == 0 && shard.teamCnt == 0 && emptyShard == null) {
            println "\t\t\tFound empty shard."
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
    println "\tExisting project shard not found."
    if (availableShardForTeam != null) {
        println "\t\tUsing shard with with current team attached."
        projectShard = availableShardForTeam
        projectShard.projectCnt++
    } else if (emptyShard != null) {
        println "\t\tUsing empty shard without existing teams or projects."
        projectShard = emptyShard
        projectShard.projectCnt++
        projectShard.teamCnt++
    } else if (availableShard) {
        println "\t\tUsing shard other teams."
        projectShard = availableShard
        projectShard.projectCnt++
        projectShard.teamCnt++
    } else {
        println "\t\tERROR, Could not find shard for project!."
    }
    dbTools.addShardProject(conn, projectShard.id, projectName, teamName)
    dbTools.updateShard(conn, projectShard)
}
//
/// Now know the shard to work with. Simply send its URL back to the client
//
println "Returning results for ${projectShard.name}"
isCredentialOverride = (projectShard.isCredentialOverride == 0) ? false : true
shardName = projectShard.name
url = projectShard.url

