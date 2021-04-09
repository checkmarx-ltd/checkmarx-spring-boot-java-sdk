package com.checkmarx.sdk.utils.zip;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;


public class NewCxZipFile implements Closeable {

    private static final double AVERAGE_ZIP_COMPRESSION_RATIO = 4.0D;

    private final Logger log;
    private final long maxSize;
    private final ZipListener listener;
    private final OutputStream outputStream;
    private final ZipOutputStream zipOutputStream;
    private long fileCount;
    private long compressedSize;

    public NewCxZipFile(File zipFile, long maxZipSizeInBytes, Logger log) throws IOException {
        this.log = log;
        this.maxSize = maxZipSizeInBytes;
        this.fileCount = 0;
        this.compressedSize = 0;
        this.listener = (fileName, size) -> {
            fileCount++;
            if (log.isInfoEnabled())
                log.info("Zipping ( {} ): {}", FileUtils.byteCountToDisplaySize(size), fileName);
        };
        outputStream = new FileOutputStream(zipFile);
        zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.setEncoding("UTF8");
    }


    public void zipContentAsFile(String pathInZip, byte[] content) throws IOException {

        validateNextFileWillNotReachMaxCompressedSize((double) content.length);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            compressedSize += InsertZipEntry(zipOutputStream, pathInZip, inputStream);
            if (listener != null) {
                listener.updateProgress(pathInZip, compressedSize);
            }
        } catch (IOException ioException) {
            log.warn(String.format("Failed to add file to archive: %s", pathInZip), ioException);
        }
    }

    public void close() {
        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(outputStream);
    }

    public void addMultipleFilesToArchive(File baseDir, List<String> relativePaths) throws IOException {
        assert baseDir != null : "baseDir must not be null";
        assert outputStream != null : "outputStream must not be null";

        int len$ = relativePaths.size();

        for (int i$ = 0; i$ < len$; ++i$) {
            String fileName = relativePaths.get(i$);

            File file = new File(baseDir, fileName);
            if (!file.canRead()) {
                log.warn("Skipping unreadable file: {}", file);
                continue;
            }
            validateNextFileWillNotReachMaxCompressedSize((double) file.length());

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                compressedSize += InsertZipEntry(zipOutputStream, fileName, fileInputStream);
                if (listener != null) {
                    listener.updateProgress(fileName, compressedSize);
                }
            } catch (IOException ioException) {
                log.warn(String.format("Failed to add file to archive: %s", fileName), ioException);
            }
        }
        zipOutputStream.flush();
    }

    private void validateNextFileWillNotReachMaxCompressedSize(double uncompressedSize) throws IOException {
        if (maxSize > 0L && (double) compressedSize + uncompressedSize / AVERAGE_ZIP_COMPRESSION_RATIO > (double) maxSize) {
            log.info("Maximum zip file size reached. Zip size: {} bytes Limit: {} bytes", compressedSize, maxSize);
            throw new MaxZipSizeReached(compressedSize, maxSize);
        }
    }

    private long InsertZipEntry(ZipOutputStream zipOutputStream, String fileName, InputStream inputStream) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(zipEntry);
        IOUtils.copy(inputStream, zipOutputStream);
        zipOutputStream.closeEntry();
        return zipEntry.getCompressedSize();
    }

    private DirectoryScanner createDirectoryScanner(File baseDir, String[] filterIncludePatterns, String[] filterExcludePatterns) {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(baseDir);
        ds.setCaseSensitive(false);
        ds.setFollowSymlinks(true);
        ds.setErrorOnMissingDir(false);
        if (filterIncludePatterns != null && filterIncludePatterns.length > 0) {
            ds.setIncludes(filterIncludePatterns);
        }

        if (filterExcludePatterns != null && filterExcludePatterns.length > 0) {
            ds.setExcludes(filterExcludePatterns);
        }

        return ds;
    }

    public long getFileCount() {
        return fileCount;
    }

    public static class MaxZipSizeReached extends IOException {
        private long compressedSize;
        private long maxZipSize;

        public MaxZipSizeReached(long compressedSize, long maxZipSize) {
            super("Zip compressed size reached a limit of " + maxZipSize + " bytes");
        }

        public long getCompressedSize() {
            return this.compressedSize;
        }

        public long getMaxZipSize() {
            return this.maxZipSize;
        }
    }

}