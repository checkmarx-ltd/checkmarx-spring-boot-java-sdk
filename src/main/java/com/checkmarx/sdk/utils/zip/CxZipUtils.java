package com.checkmarx.sdk.utils.zip;


import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.dto.PathFilter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;


/**
 * CxZipUtils generates the patterns used for zipping the workspace folder
 */
public abstract class CxZipUtils {

    public static final String TEMP_FILE_NAME_TO_ZIP = "zippedSource";
    public static final long MAX_ZIP_SIZE_BYTES = 2147483648L;
    
    public synchronized static byte[] getZippedSources(RestClientConfig config, PathFilter filter, String sourceDir, Logger log) throws IOException {
        byte[] zipFile = config.getZipFile() != null ? FileUtils.readFileToByteArray(config.getZipFile()) : null;
        if (zipFile == null) {
            log.debug("----------------------------------- Start zipping files :------------------------------------");
            Long maxZipSize = MAX_ZIP_SIZE_BYTES;

            CxZip cxZip = new CxZip(TEMP_FILE_NAME_TO_ZIP, maxZipSize, log);
            zipFile = cxZip.zipWorkspaceFolder(new File(sourceDir), filter);
            log.debug("----------------------------------- Finish zipping files :------------------------------------");
        }
        return zipFile;
    }

}

