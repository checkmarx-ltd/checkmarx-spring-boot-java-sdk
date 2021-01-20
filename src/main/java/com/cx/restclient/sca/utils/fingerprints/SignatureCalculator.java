package com.cx.restclient.sca.utils.fingerprints;

import com.checkmarx.sdk.exception.ASTRuntimeException;

import java.io.IOException;


public interface SignatureCalculator {
    CxSCAFileSignature calculateSignature(byte[] content) throws IOException, ASTRuntimeException;

}
