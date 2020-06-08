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
    private static final String NAME1 = "Cross_Site_History_Manipulation";
    private static final String NAME2 = "Client_Potential_XSS";
    private static final String CWE1 = "203";
    private static final String CWE2 = "611";

    @Test
    public void passesFilter_typicalExample() {
        String scriptText = "finding.severity == 'HIGH' || (finding.severity == 'MEDIUM' && finding.status == 'URGENT')";

        Script script = parse(scriptText);
        verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT, NAME1, CWE1, true);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_URGENT, NAME1, CWE1, true);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY, NAME1, CWE1, false);
        verifyScriptResult(script, SEVERITY_LOW, STATUS_NEW, STATE_URGENT, NAME1, CWE1, false);
    }

    @Test
    public void passesFilter_allProperties() {
        String scriptText = "finding.severity == 'MEDIUM' && finding.status == 'TO VERIFY' && finding.cwe == '203' " +
                "&& finding.category == 'Cross_Site_History_Manipulation'";

        Script script = parse(scriptText);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_VERIFY, NAME1, CWE1, true);
        verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_VERIFY, NAME1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY, NAME1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_URGENT, NAME1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_VERIFY, NAME2, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_VERIFY, NAME1, CWE2, false);
    }

    /**
     * Make sure that filter script evaluation doesn't take too long.
     * Important because multiple findings may be provided.
     */
    @Test
    public void passesFilter_performance() {
        String scriptText = "finding.severity == 'HIGH' || finding.severity == 'MEDIUM'";
        Script script = verifyParsingPerformance(scriptText, Duration.ofSeconds(10));
        verifyEvaluationPerformance(script, Duration.ofSeconds(1));
    }

    private static Script parse(String scriptText) {
        GroovyShell groovyShell = new GroovyShell();
        return groovyShell.parse(scriptText);
    }

    private void verifyEvaluationPerformance(Script script, Duration maxAllowedDuration) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY, NAME1, CWE1, true);
            verifyScriptResult(script, SEVERITY_LOW, STATUS_RECURRENT, STATE_URGENT, NAME1, CWE1, false);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY, NAME1, CWE1, true);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT, NAME1, CWE1, true);
        }
        long end = System.currentTimeMillis();

        Duration actualDuration = Duration.ofMillis(end - start);
        log.info("Evaluation took {}.", actualDuration);

        assertTrue(maxAllowedDuration.compareTo(actualDuration) >= 0,
                "Filter evaluation took too long");
    }

    private static Script verifyParsingPerformance(String scriptText, Duration maxAllowedDuration) {
        long start = System.currentTimeMillis();
        Script script = parse(scriptText);
        long end = System.currentTimeMillis();

        Duration parseDuration = Duration.ofMillis(end - start);
        log.info("Parsing took {}.", parseDuration);

        assertTrue(maxAllowedDuration.compareTo(parseDuration) >= 0,
                "Script parsing took too long");
        return script;
    }

    private static void verifyScriptResult(Script script,
                                           String severity,
                                           String status,
                                           String state,
                                           String name,
                                           String cweId,
                                           boolean expectedResult) {
        ResultType finding = new ResultType();
        finding.setStatus(status);
        finding.setState(state);

        QueryType findingGroup = new QueryType();
        findingGroup.setSeverity(severity);
        findingGroup.setSeverity(severity);
        findingGroup.setName(name);
        findingGroup.setCweId(cweId);
        FilterConfiguration filterConfiguration = createFilterConfiguration(script);

        FilterValidatorImpl validator = new FilterValidatorImpl();
        boolean actualResult = validator.passesFilter(findingGroup, finding, filterConfiguration);
        assertEquals(expectedResult, actualResult, "Unexpected filtering result.");
    }

    private static FilterConfiguration createFilterConfiguration(Script script) {
        ScriptedFilter filter = ScriptedFilter.builder()
                .script(script)
                .build();

        return FilterConfiguration.builder()
                .scriptedFilter(filter)
                .build();
    }
}