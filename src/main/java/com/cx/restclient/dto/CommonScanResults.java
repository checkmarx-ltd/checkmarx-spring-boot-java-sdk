package com.cx.restclient.dto;


import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sca.AstScaResults;
//import com.cx.restclient.osa.dto.OSAResults;
//import com.cx.restclient.sast.dto.SASTResults;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public class CommonScanResults extends Results implements Serializable {
    private final Map<ScannerType, Results> resultsMap = new EnumMap<>(ScannerType.class);
//
//    private Exception generalException = null;

    public Map<ScannerType, Results> getResults(){
        return resultsMap;
    }
    
    public void put(ScannerType type, Results results) {
        if(resultsMap.containsKey(type)){
            throw new ASTRuntimeException("Results already contain type " + type);
        }
        resultsMap.put(type, results);
    }

    public Map<ScannerType, Results> getResultsMap() {
        return resultsMap;
    }

    public Results get(ScannerType type) {
        return resultsMap.get(type);
    }

//
//    public OSAResults getOsaResults() {
//        return (OSAResults)resultsMap.get(ScannerType.OSA);
//    }

    public AstSastResults getAstResults() {
        return (AstSastResults)resultsMap.get(ScannerType.AST_SAST);
    }

    public AstScaResults getScaResults() {
        return (AstScaResults)resultsMap.get(ScannerType.AST_SCA);
    }


//    public SASTResults getSastResults() {
//        return (SASTResults)resultsMap.get(ScannerType.SAST);
//
//    }
//
//    public Exception getGeneralException() {
//        return generalException;
//    }
//
//    public void setGeneralException(Exception generalException) {
//        this.generalException = generalException;
//    }

}
