package com.cx.restclient.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: dorg.
 * Date: 4/12/2018.
 */
public abstract class ShragaUtils {
    public static Map<String, List<String>> generateIncludesExcludesPatternLists(String folderExclusions, String filterPattern, Logger log) {

        folderExclusions = removeSpaceAndNewLine(folderExclusions);
        filterPattern = removeSpaceAndNewLine(filterPattern);

        String excludeFoldersPattern = processExcludeFolders(folderExclusions, log);
        String combinedPatterns = "";

        if (StringUtils.isEmpty(filterPattern) && StringUtils.isEmpty(excludeFoldersPattern)) {
            combinedPatterns = "";
        } else if (!StringUtils.isEmpty(filterPattern) && StringUtils.isEmpty(excludeFoldersPattern)) {
            combinedPatterns = filterPattern;
        } else if (StringUtils.isEmpty(filterPattern) && !StringUtils.isEmpty(excludeFoldersPattern)) {
            combinedPatterns = excludeFoldersPattern;
        } else {
            combinedPatterns = filterPattern + "," + excludeFoldersPattern;
        }

        return convertPatternsToLists(combinedPatterns);
    }

    public static String removeSpaceAndNewLine(String string){
        if(string!=null){
            string = string.replace("\\s","").replace("\n", "").replace("\r", "").replace(" ","").replace("\t","");
        }
        return string;
    }

    public static String processExcludeFolders(String folderExclusions, Logger log) {
        if (StringUtils.isEmpty(folderExclusions)) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String[] patterns = StringUtils.split(folderExclusions, ",\n");
        for (String p : patterns) {
            p = p.trim();
            if (p.length() > 0) {
                result.append("!**/");
                result.append(p);
                result.append("/**,");
            }
        }

        log.info("Exclude folders converted to: '" + result.toString() + "'");
        return result.toString();
    }

    public static final String INCLUDES_LIST = "includes";
    public static final String EXCLUDES_LIST = "excludes";

    public static Map<String, List<String>> convertPatternsToLists(String filterPatterns) {
        filterPatterns = StringUtils.defaultString(filterPatterns);
        List<String> inclusions = new ArrayList<String>();
        List<String> exclusions = new ArrayList<String>();
        String[] filters = filterPatterns.replace("\n", "").replace("\r", "").split("\\s*,\\s*"); //split by comma and trim (spaces + newline)
        for (String filter : filters) {
            if (StringUtils.isNotEmpty(filter)) {
                if (!filter.startsWith("!")) {
                    inclusions.add(filter.trim());
                } else if (filter.length() > 1) {
                    filter = filter.substring(1); // Trim the "!"
                    exclusions.add(filter.trim());
                }
            }
        }

        Map<String, List<String>> ret = new HashMap<String, List<String>>();
        ret.put(INCLUDES_LIST, inclusions);
        ret.put(EXCLUDES_LIST, exclusions);

        return ret;
    }

    public static String formatDate(String date, String fromFormat, String toFormat) {
        SimpleDateFormat fromDate = new SimpleDateFormat(fromFormat);
        SimpleDateFormat toDate = new SimpleDateFormat(toFormat);
        String ret = "";
        try {
            ret = toDate.format(fromDate.parse(date));
        } catch (Exception ignored) {

        }
        return ret;
    }

    public static String getTimestampSince(long startTimeSec) {
        long elapsedSec = System.currentTimeMillis() / 1000 - startTimeSec;
        long hours = elapsedSec / 3600;
        long minutes = elapsedSec % 3600 / 60;
        long seconds = elapsedSec % 60;
        String hoursStr = (hours < 10) ? ("0" + hours) : (Long.toString(hours));
        String minutesStr = (minutes < 10) ? ("0" + minutes) : (Long.toString(minutes));
        String secondsStr = (seconds < 10) ? ("0" + seconds) : (Long.toString(seconds));
        return String.format("%s:%s:%s", hoursStr, minutesStr, secondsStr);
    }
}
