package com.checkmarx.sdk.dto;

import com.checkmarx.sdk.utils.SdkUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class PathFilter {
    private String[] includes;
    private String[] excludes;

    public PathFilter(String folderExclusions, String filterPattern, Logger log) {
        Map<String, List<String>> stringListMap = SdkUtils.generateIncludesExcludesPatternLists(folderExclusions, filterPattern, log);
        includes = getArray(stringListMap, SdkUtils.INCLUDES_LIST);
        excludes = getArray(stringListMap, SdkUtils.EXCLUDES_LIST);
    }

    public String[] getIncludes() {
        return includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    private static String[] getArray(Map<String, List<String>> map, String key){
        return map.get(key).toArray(new String[0]);
    }

    public void addToIncludes(String element) {
        includes = ArrayUtils.add(includes, element);
    }
}
