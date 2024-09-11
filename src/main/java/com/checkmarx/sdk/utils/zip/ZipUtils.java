package com.checkmarx.sdk.utils.zip;

import com.checkmarx.sdk.utils.ScanUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtils {
    
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static void zipFile(String fileToZip, String zipFile, String excludePatterns)
            throws IOException {
        List<String> excludeList = null;
        log.info("Creating zip file {} from contents of path {}", zipFile, fileToZip);
        if(excludePatterns != null) {
            log.info("Applying exclusions: {}", excludePatterns);
        }

        if(!ScanUtils.empty(excludePatterns)) {
            excludeList = Arrays.asList(excludePatterns.split(","));
        }

        zipFile = FileSystems.getDefault().getPath(zipFile).toAbsolutePath().toString();
        log.debug("Zip Absolute path: {}", zipFile);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File srcFile = new File(fileToZip);
            if (srcFile.isDirectory()) {
                for (String fileName : Objects.requireNonNull(srcFile.list())) {
                    addToZip("", String.format("%s/%s", fileToZip, fileName), zipFile, zipOut, excludeList,fileToZip);
                }
            } else {
                addToZip("", fileToZip, zipFile, zipOut, excludeList,fileToZip);
            }
            zipOut.flush();
        }
        log.info("Successfully created {} ", zipFile);
    }

    private static void addToZip(String path, String srcFile, String zipFile, ZipOutputStream zipOut, List<String> excludePatterns,String rootDir)
            throws IOException {
        File file = new File(srcFile);
        String filePath = "".equals(path) ? file.getName() : String.format("%s/%s", path, file.getName());
        Path filePathObj = file.toPath();
        if (Files.isSymbolicLink(filePathObj)) {
            Path targetPath = Files.readSymbolicLink(filePathObj);
            String targetPathStr = targetPath.toAbsolutePath().toString();

            // Check if the symbolic link points within the directory being zipped
            if (targetPathStr.startsWith(new File(rootDir).getAbsolutePath())) {
                log.debug("#########Skipping symbolic link {} pointing to {}#########", filePath, targetPathStr);
                return;
            }

            // Add the symbolic link entry to the zip
            zipOut.putNextEntry(new ZipEntry(filePath));
            zipOut.write(targetPathStr.getBytes());
            zipOut.closeEntry();
        }
        if (file.isDirectory()) {
            for (String fileName : Objects.requireNonNull(file.list())) {
                addToZip(filePath, srcFile + "/" + fileName, zipFile, zipOut, excludePatterns,rootDir);
            }
        } else {
            String tmpPath = FileSystems.getDefault().getPath(srcFile).toAbsolutePath().toString();
            tmpPath = tmpPath.replace("/./","/"); //Linux FS
            tmpPath = tmpPath.replace("\\.\\","\\"); //Windows FS

            log.debug("@@@ {} | {} @@@", zipFile, tmpPath);
            if(tmpPath.equals(zipFile)){
                log.debug("#########Skipping the new zip file {}#########", zipFile);
                return;
            }
            if(excludePatterns == null || excludePatterns.isEmpty() || !anyMatches(excludePatterns, filePath)) {
                zipOut.putNextEntry(new ZipEntry(filePath));
                try (FileInputStream in = new FileInputStream(srcFile)) {
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, len);
                    }
                }
            }
        }
    }

    private static boolean anyMatches(List<String> patterns, String str){
        for(String pattern: patterns){
            pattern = pattern.trim();
            if(strMatches(pattern, str)) {
                log.debug("match: {}|{}", pattern, str);
                return true;
            }
        }
        return false;
    }

    private static boolean strMatches(String patternStr, String str){
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            return start == 0 && end == str.length();
        }
        return false;
    }
}