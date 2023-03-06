package com.checkmarx.sdk.utils.scaResolver;
import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.sca.ScaConfig;
import com.checkmarx.sdk.exception.CxHTTPClientException;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ScaResolverUtils {

    public static final String SCA_RESOLVER_EXE = "\\" + "ScaResolver" + ".exe";
    public static final String SCA_RESOLVER_FOR_LINUX = "/" + "ScaResolver";
    public static final String OFFLINE = "offline";

    public static int runScaResolver(String pathToScaResolver, String mandatoryParameters , String scaResolverAddParams, String pathToResultJSONFile, Logger log, ScaConfig scaConfig, ScaProperties scaProperties,String custom)
            throws CxHTTPClientException {
        int exitCode = -100;
        String[] scaResolverCommand;

        List<String> arguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(mandatoryParameters);
        while (m.find())
            arguments.add(m.group(1));

        Matcher m1 = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(scaResolverAddParams);
        while (m1.find())
                arguments.add(m1.group(1));

        if (SystemUtils.IS_OS_UNIX)
        {
            if(custom!=null)
            {
                arguments.add(custom);
            }
        }


        /*
            As every time mandatoryParameters are getting added to start of the list looping from end till element.
            Checks if mandatoryParameters are added in Additional parameter, if exists remove extra.
         */
        for(int i=arguments.size()-1;i>=6;i=i-2)
        {
            if(arguments.get(i-1).equals("-s") ||arguments.get(i-1).equals("-r") || arguments.get(i-1).equals("-n") )
            {
                log.debug("-s, -r, -n are mandatory values, please provide any another additional parameters");
                arguments.remove(i);
                arguments.remove(i-1);
            }
        }

        //Code as Config Overriding
        if(scaConfig.getExpPathSastProjectName()!=null)
        {
            for(int i=0;i<arguments.size();i++)
            {
                if(arguments.get(i).equals("--cxprojectname"))
                {
                    log.debug("Overriding SAST project name");
                    if(arguments.size()-1==i)
                    {
                        arguments.add(scaConfig.getExpPathSastProjectName());
                    }
                    else {
                        arguments.set(i+1,scaConfig.getExpPathSastProjectName());
                    }
                }
            }
        }
        //Overridng sca properties project name params
        if(scaProperties.getScaResolverOverrideProjectName()!=null)
        {
            for(int i=0;i<arguments.size();i++)
            {
                if(arguments.get(i).equals("--cxprojectname"))
                {
                    log.debug("Overriding SAST project name");
                    if(arguments.size()-1==i)
                    {
                        arguments.add(scaProperties.getScaResolverOverrideProjectName());
                    }
                    else {
                        arguments.set(i+1,scaProperties.getScaResolverOverrideProjectName());
                    }
                }
            }
        }
		/*
		 Convert path and parameters into a single CMD command
		 */
        scaResolverCommand = new String[arguments.size() + 2];

        if (!SystemUtils.IS_OS_UNIX) {
            //Add "ScaResolver.exe" to cmd command on Windows
            pathToScaResolver = pathToScaResolver + SCA_RESOLVER_EXE;
        } else {
            //Add "/ScaResolver" command on Linux machines
            pathToScaResolver = pathToScaResolver + SCA_RESOLVER_FOR_LINUX;
        }

        log.debug("Starting build CMD command");
        scaResolverCommand[0] = pathToScaResolver;
        scaResolverCommand[1] = OFFLINE;

        for (int i = 0; i < arguments.size(); i++) {

            String arg = arguments.get(i);
            scaResolverCommand[i + 2] = arg;
            if (arg.equals("-r")) {
                while (pathToResultJSONFile.contains("\""))
                    pathToResultJSONFile = pathToResultJSONFile.replace("\"", "");
                scaResolverCommand[i + 3] = pathToResultJSONFile;
                i++;
            }
        }

        log.debug("Finished created CMD command");
        try {
            //log.info("Executing next command: " + Arrays.toString(scaResolverCommand));
            log.info("Executing SCA Resolver Command");
            Process process;
            if (!SystemUtils.IS_OS_UNIX) {
                log.debug("Executing cmd command on windows. ");
                process = Runtime.getRuntime().exec(scaResolverCommand);
            } else {
                String tempPermissionValidation = "ls " + pathToScaResolver + " -ltr";
                printExecCommandOutput(tempPermissionValidation, log);

                log.debug("Executing ScaResolver command.");
                process = Runtime.getRuntime().exec(scaResolverCommand);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                log.debug("******************sca resolver logs:**********************");
                while (reader.readLine() != null) {
                    log.debug(reader.readLine());
                }
            } catch (IOException e) {
                log.error("Error while trying write to the file: " + e.getMessage(), e.getStackTrace());
                throw new CxHTTPClientException(e);
            }
            exitCode = process.waitFor();

        } catch (IOException | InterruptedException e) {
            log.error("Failed to execute next command : " + scaResolverCommand, e.getMessage(), e.getStackTrace());
            Thread.currentThread().interrupt();
            if (Thread.interrupted()) {
                throw new CxHTTPClientException(e);
            }
        }
        return exitCode;
    }

    private static void printExecCommandOutput(String execCommand, Logger log) {
        try {
            log.debug("Checking that next file has -rwxrwxrwx permissions " + execCommand);
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(execCommand);
            BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = is.readLine()) != null) {
                log.debug(line);
            }
        } catch (Exception ex) {
            log.debug("Failed to run execute [%s] command ");
        }
    }

}
