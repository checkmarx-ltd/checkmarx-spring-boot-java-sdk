package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.filtering.ScriptedFilter;
import com.checkmarx.sdk.exception.CheckmarxRuntimeException;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class FilterValidatorImplTest {
    private static final String STATUS_RECURRENT = "Recurrent";
    private static final String STATUS_NEW = "New";
    private static final String STATE_URGENT_ID = "3";
    private static final String STATE_URGENT_NAME = "Urgent";
    private static final String STATE_VERIFY_ID = "0";
    private static final String SEVERITY_HIGH = "High";
    private static final String SEVERITY_MEDIUM = "Medium";
    private static final String SEVERITY_LOW = "Low";
    private static final String NAME1 = "Cross_Site_History_Manipulation";
    private static final String NAME2 = "Client_Potential_XSS";
    private static final String CWE1 = "203";
    private static final String CWE2 = "611";

    @Test
    public void passesFilter_scriptTypicalExample() {
        String scriptText = "finding.severity == 'HIGH' || (finding.severity == 'MEDIUM' && finding.state == 'URGENT')";

        Script script = parse(scriptText);
        verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT_ID, NAME1, CWE1, true);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_URGENT_ID, NAME1, CWE1, true);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE1, false);
        verifyScriptResult(script, SEVERITY_LOW, STATUS_NEW, STATE_URGENT_ID, NAME1, CWE1, false);
    }

    @Test
    public void passesFilter_allPropertiesInScript() {
        String scriptText = "finding.severity == 'MEDIUM' && finding.state == 'TO VERIFY' && finding.status == 'NEW'" +
                "&& finding.cwe == '203' && finding.category == 'CROSS_SITE_HISTORY_MANIPULATION'";

        Script script = parse(scriptText);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE1, true);
        verifyScriptResult(script, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_VERIFY_ID, NAME1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_URGENT_ID, NAME1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_ID, NAME2, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE2, false);
    }

    @Test
    public void passesFilter_scriptRuntimeError() {
        String unknownObject = "cry.of.surprise == 'present'";
        String unknownProperty = "finding.mystery == 'unsolvable'";

        validateExpectedError(unknownObject);
        validateExpectedError(unknownProperty);
    }

    /**
     * Make sure that filter script evaluation doesn't take too long.
     * Important because multiple findings may be provided.
     */
    @Test
    public void passesFilter_scriptPerformance() {
        String scriptText = "finding.severity == 'HIGH' || finding.severity == 'MEDIUM'";
        Script script = verifyParsingPerformance(scriptText, Duration.ofSeconds(10));
        verifyEvaluationPerformance(script, Duration.ofSeconds(1));
    }

    @Test
    public void passesFilter_allSimpleFilters() {
        Filter severity = Filter.builder().type(Filter.Type.SEVERITY).value(SEVERITY_HIGH).build();
        Filter cwe = Filter.builder().type(Filter.Type.CWE).value(CWE1).build();
        Filter type = Filter.builder().type(Filter.Type.TYPE).value(NAME1).build();
        Filter status = Filter.builder().type(Filter.Type.STATUS).value(STATUS_NEW).build();
        // Using state name to init the filter, and a corresponding state ID while creating a finding.
        Filter state = Filter.builder().type(Filter.Type.STATE).value(STATE_URGENT_NAME).build();
        List<Filter> filters = Arrays.asList(severity, cwe, type, status, state);

        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_NEW, STATE_URGENT_ID, NAME1, CWE1, true);
        verifySimpleFilterResult(filters, SEVERITY_MEDIUM, STATUS_NEW, STATE_URGENT_ID, NAME1, CWE1, false);
        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT_ID, NAME1, CWE1, false);
        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE1, false);
        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_NEW, STATE_URGENT_ID, NAME2, CWE1, false);
        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_NEW, STATE_URGENT_ID, NAME1, CWE2, false);
    }

    private void validateExpectedError(String scriptWithUnknownObject) {
        Script script = parse(scriptWithUnknownObject);
        QueryType findingGroup = createFindingGroup(SEVERITY_LOW, NAME1, CWE1);
        ResultType finding = createFinding(STATUS_NEW, STATE_URGENT_ID);
        FilterConfiguration filterConfiguration = createFilterConfiguration(script);

        FilterValidatorImpl validator = new FilterValidatorImpl();

        try {
            validator.passesFilter(findingGroup, finding, filterConfiguration);
        } catch (Exception e) {
            assertTrue(e instanceof CheckmarxRuntimeException, String.format("Expected %s to be thrown.", CheckmarxRuntimeException.class));
            assertTrue(e.getCause() instanceof GroovyRuntimeException, String.format("Expected exception cause to be %s", GroovyRuntimeException.class));
        }
    }

    private static Script parse(String scriptText) {
        GroovyShell groovyShell = new GroovyShell();
        return groovyShell.parse(scriptText);
    }

    private void verifyEvaluationPerformance(Script script, Duration maxAllowedDuration) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE1, true);
            verifyScriptResult(script, SEVERITY_LOW, STATUS_RECURRENT, STATE_URGENT_ID, NAME1, CWE1, false);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE1, true);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT_ID, NAME1, CWE1, true);
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
        ResultType finding = createFinding(status, state);
        QueryType findingGroup = createFindingGroup(severity, name, cweId);
        FilterConfiguration filterConfiguration = createFilterConfiguration(script);

        FilterValidatorImpl validator = new FilterValidatorImpl();
        boolean actualResult = validator.passesFilter(findingGroup, finding, filterConfiguration);
        assertEquals(expectedResult, actualResult, "Unexpected script filtering result.");
    }

    private static void verifySimpleFilterResult(List<Filter> filters,
                                                 String severity,
                                                 String status,
                                                 String state,
                                                 String name,
                                                 String cweId,
                                                 boolean expectedResult) {
        ResultType finding = createFinding(status, state);
        QueryType findingGroup = createFindingGroup(severity, name, cweId);
        FilterValidatorImpl filterValidator = new FilterValidatorImpl();
        FilterConfiguration filterConfiguration = FilterConfiguration.builder().simpleFilters(filters).build();
        boolean passes = filterValidator.passesFilter(findingGroup, finding, filterConfiguration);
        assertEquals(expectedResult, passes, "Unexpected simple filtering result.");
    }

    private static QueryType createFindingGroup(String severity, String name, String cweId) {
        QueryType findingGroup = new QueryType();
        findingGroup.setSeverity(severity);
        findingGroup.setName(name);
        findingGroup.setCweId(cweId);
        return findingGroup;
    }

    private static ResultType createFinding(String status, String state) {
        ResultType finding = new ResultType();
        finding.setStatus(status);
        finding.setState(state);
        return finding;
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