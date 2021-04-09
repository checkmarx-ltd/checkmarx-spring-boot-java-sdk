package com.checkmarx.sdk.utils.sca.fingerprints;

import com.checkmarx.sdk.exception.ScannerRuntimeException;

import java.io.IOException;


public interface SignatureCalculator {
    CxSCAFileSignature calculateSignature(byte[] content) throws IOException, ScannerRuntimeException;

}
