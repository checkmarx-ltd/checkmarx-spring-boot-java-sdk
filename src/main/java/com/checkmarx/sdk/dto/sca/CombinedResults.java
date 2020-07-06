package com.checkmarx.sdk.dto.sca;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedResults {

    private final SCAResults scaResults;

    public CombinedResults(SCAResults scaResults) {
        this.scaResults = scaResults;
    }
}
