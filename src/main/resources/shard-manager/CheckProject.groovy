//@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )
//import groovyx.net.http.HTTPBuilder
import com.checkmarx.flow.dto.ScanRequest
import com.checkmarx.flow.utils.ScanUtils
import groovy.json.JsonSlurper

println("------------- Groovy script execution started --------------------")
println("Checking 'request' object for details and determine if scan is applicable for this branch (target or current)")

//define the custom list of protected branches to compare against the branch in request
def protectedBranchList = ['agehring-.*','remediate-.*','security']

String branch = request.getBranch();
String targetBranch = request.getMergeTargetBranch();
if(!ScanUtils.empty(targetBranch)) { //if targetBranch is set, it is a merge request
    branch = targetBranch;
}
println("This is the branch from request: " + branch)
def Boolean branchMatched = false;
protectedBranchList.each { //iterate over each of the protected branches
    if((it.toString()).equals(branch))
    {
       branchMatched = true;
    }
}
println("Is branch valid for scan ?: "+ branchMatched)
println("-------- Groovy script execution ended -------------")
if(branchMatched) {
    return true;
}
else
{
    return false
};

//for accessing the original payload
String WEB_HOOK_PAYLOAD = "web-hook-payload";
def payload = request.getAdditionalMetadata(WEB_HOOK_PAYLOAD)
JsonSlurper jsonParser = new JsonSlurper()
Map parsedJson =  jsonParser.parseText(payload)
println(parsedJson.toString())

//if you want to add something to the scanRequest object
request.putAdditionalMetadata("moreData","Value");
