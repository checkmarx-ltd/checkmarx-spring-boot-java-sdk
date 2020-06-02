package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.filtering.ScriptedFilter;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class FilterValidatorImplTest {
    private static final String STATUS_RECURRENT = "Recurrent";
    private static final String STATUS_NEW = "New";
    private static final String STATE_URGENT = "3";
    private static final String STATE_VERIFY = "0";
    private static final String SEVERITY_HIGH = "High";
    private static final String SEVERITY_MEDIUM = "Medium";
    private static final String SEVERITY_LOW = "Low";

    @Test
    public void passesScriptedFilter_sanity() {
        String scriptText = "finding.severity == 'HIGH' || (finding.severity == 'MEDIUM' && finding.status == 'URGENT')";

        GroovyShell groovyShell = new GroovyShell();
        Script script = groovyShell.parse(scriptText);
        verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT, true);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_URGENT, true);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY, false);
        verifyScriptResult(script, SEVERITY_LOW, STATUS_NEW, STATE_URGENT, false);
    }

    @Test
    public void passesScriptedFilter_performance() {
        String scriptText = "finding.severity == 'HIGH' || finding.severity == 'MEDIUM'";
        Script script = verifyParsingPerformance(scriptText, Duration.ofSeconds(10));
        verifyEvaluationPerformance(script, Duration.ofSeconds(1));
    }

    private void verifyEvaluationPerformance(Script script, Duration maxAllowedDuration) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY, true);
            verifyScriptResult(script, SEVERITY_LOW, STATUS_RECURRENT, STATE_URGENT, false);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY, true);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT, true);
        }
        long end = System.currentTimeMillis();

        Duration actualDuration = Duration.ofMillis(end - start);
        log.info("Evaluation took {}.", actualDuration);

        assertTrue(maxAllowedDuration.compareTo(actualDuration) >= 0,
                "Filter evaluation took too long");
    }

    private Script verifyParsingPerformance(String scriptText, Duration maxAllowedDuration) {
        long start = System.currentTimeMillis();

        GroovyShell groovyShell = new GroovyShell();
        Script script = groovyShell.parse(scriptText);

        long end = System.currentTimeMillis();

        Duration parseDuration = Duration.ofMillis(end - start);
        log.info("Parsing took {}.", parseDuration);

        assertTrue(maxAllowedDuration.compareTo(parseDuration) >= 0,
                "Script parsing took too long");
        return script;
    }

    private void verifyScriptResult(Script script,
                                    String severity,
                                    String status,
                                    String state,
                                    boolean expectedResult) {
        FilterValidatorImpl validator = new FilterValidatorImpl();
        ResultType finding = new ResultType();
        finding.setStatus(status);
        finding.setState(state);

        QueryType findingGroup = new QueryType();
        findingGroup.setSeverity(severity);

        ScriptedFilter filter = ScriptedFilter.builder()
                .script(script)
                .build();

        FilterConfiguration filterConfiguration = FilterConfiguration.builder()
                .scriptedFilter(filter)
                .build();

        boolean actualResult = validator.passesFilter(findingGroup, finding, filterConfiguration);
        assertEquals(expectedResult, actualResult, "Unexpected filtering result.");
    }
}