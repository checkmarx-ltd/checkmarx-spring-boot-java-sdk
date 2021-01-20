package com.cx.restclient.sca.utils;

import com.cx.restclient.dto.PathFilter;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;

public class CxSCAFileSystemUtils {

    public static String[] scanAndGetIncludedFiles(String baseDir, PathFilter filter) {
        DirectoryScanner ds = createDirectoryScanner(new File(baseDir), filter.getIncludes(), filter.getExcludes());
        ds.setFollowSymlinks(true);
        ds.scan();
        return ds.getIncludedFiles();
    }

    private static DirectoryScanner createDirectoryScanner(File baseDir, String[] filterIncludePatterns, String[] filterExcludePatterns) {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(baseDir);
        ds.setCaseSensitive(false);
        ds.setFollowSymlinks(false);
        ds.setErrorOnMissingDir(false);

        if (filterIncludePatterns != null && filterIncludePatterns.length > 0) {
            ds.setIncludes(filterIncludePatterns);
        }

        if (filterExcludePatterns != null && filterExcludePatterns.length > 0) {
            ds.setExcludes(filterExcludePatterns);
        }

        return ds;
    }

}
