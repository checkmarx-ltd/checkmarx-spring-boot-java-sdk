package com.cx.restclient.sca.utils.fingerprints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class FingerprintCollector {

    private static final String DEFAULT_FINGERPRINT_FILENAME = "CxSCAFingerprints.json";
    private final SignatureCalculator sha1SignatureCalculator;
    private final Logger log;

    public FingerprintCollector(Logger log){
        this.log = log;
        sha1SignatureCalculator = new Sha1SignatureCalculator();
    }

    public CxSCAScanFingerprints collectFingerprints(String baseDir,
                                                     List<String> files) {
        log.info(String.format("Started fingerprint collection on %s", baseDir));

        CxSCAScanFingerprints scanFingerprints = new CxSCAScanFingerprints();


        for (String filePath : files) {

            Path fullFilePath = Paths.get(baseDir, filePath);
            try  {
                log.debug(String.format("Calculating signatures for file %s", fullFilePath));
                byte[] fileContent = Files.readAllBytes(fullFilePath);
                CxSCAFileFingerprints fingerprints = new CxSCAFileFingerprints(filePath, Files.size(fullFilePath));

                fingerprints.addFileSignature(sha1SignatureCalculator.calculateSignature(fileContent));

                scanFingerprints.addFileFingerprints(fingerprints);
            } catch (IOException e) {
                log.error(String.format("Failed calculating file signature: %s",fullFilePath.toString() ), e);
            }
        }
        log.info(String.format("Calculated fingerprints for %d files", scanFingerprints.getFingerprints().size()));
        scanFingerprints.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return scanFingerprints;
    }

    public void writeScanFingerprintsFile(CxSCAScanFingerprints scanFingerprints, String path) throws IOException {

        long fingerprintCount = scanFingerprints.getFingerprints().size();
        if (fingerprintCount == 0){
            log.info("No supported files for fingerprinting found in this scan");
        }
        String fingerprintFilePath = path;
        File targetLocation = new File(path);
        if (targetLocation.isDirectory()){
            fingerprintFilePath = Paths.get(path, DEFAULT_FINGERPRINT_FILENAME).toString();
        }

        log.info(String.format("Writing %d file signatures to fingerprint file: %s", fingerprintCount, fingerprintFilePath));
        ObjectMapper objectMapper = new ObjectMapper();
        File fingerprintFile = new File(fingerprintFilePath);
        objectMapper.writeValue(fingerprintFile, scanFingerprints);

    }


    public static String getFingerprintsAsJsonString(CxSCAScanFingerprints scanFingerprints) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(scanFingerprints);
    }


}
