package com.checkmarx.sdk.service.sast;

import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.filtering.EngineFilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterInput;
import com.checkmarx.sdk.dto.filtering.ScriptedFilter;
import com.checkmarx.sdk.exception.CheckmarxRuntimeException;
import com.checkmarx.sdk.service.FilterInputFactory;
import com.checkmarx.sdk.service.FilterValidator;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class FilterValidatorTest {
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
    private static final String PERFORMANCE_TEST_SCRIPT = "finding.severity == 'HIGH' || finding.severity == 'MEDIUM'";
    private static final Duration MAX_ALLOWED_DURATION = Duration.ofSeconds(20);

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
     * Parsing normally occurs only once during automation flow.
     * However, it takes much longer than script evaluation.
     */
    @Test
    public void passesFilter_parsingPerformance() {
        long start = System.currentTimeMillis();
        parse(PERFORMANCE_TEST_SCRIPT);
        long end = System.currentTimeMillis();

        Duration parseDuration = Duration.ofMillis(end - start);
        log.info("Parsing took {}.", parseDuration);

        assertTrue(MAX_ALLOWED_DURATION.compareTo(parseDuration) >= 0,
                String.format("Script parsing took too long (more than %s).", MAX_ALLOWED_DURATION));
    }

    /**
     * Make sure that filter script evaluation doesn't take too long.
     * Important because multiple findings may be provided.
     */
    @Test
    public void passesFilter_evaluationPerformance() {
        final int EVALUATION_COUNT = 10000;
        Script script = parse(PERFORMANCE_TEST_SCRIPT);
        long start = System.currentTimeMillis();

        for (int i = 0; i < EVALUATION_COUNT; i++) {
            verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE1, true);
            verifyScriptResult(script, SEVERITY_LOW, STATUS_RECURRENT, STATE_URGENT_ID, NAME1, CWE1, false);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY_ID, NAME1, CWE1, true);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT_ID, NAME1, CWE1, true);
        }
        long end = System.currentTimeMillis();

        Duration actualDuration = Duration.ofMillis(end - start);
        log.info("Evaluation took {}.", actualDuration);

        assertTrue(MAX_ALLOWED_DURATION.compareTo(actualDuration) >= 0,
                String.format("Filter evaluation took too long (more than %s).", MAX_ALLOWED_DURATION));    }

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
        EngineFilterConfiguration filterConfiguration = createFilterConfiguration(script);

        FilterValidator validator = new FilterValidator();

        try {
            FilterInputFactory filterInputFactory = new FilterInputFactory();
            FilterInput filterInput = filterInputFactory.createFilterInputForCxSast(findingGroup, finding);
            validator.passesFilter(filterInput, filterConfiguration);
        } catch (Exception e) {
            assertTrue(e instanceof CheckmarxRuntimeException, String.format("Expected %s to be thrown.", CheckmarxRuntimeException.class));
            assertTrue(e.getCause() instanceof GroovyRuntimeException, String.format("Expected exception cause to be %s", GroovyRuntimeException.class));
        }
    }

    private static Script parse(String scriptText) {
        GroovyShell groovyShell = new GroovyShell();
        return groovyShell.parse(scriptText);
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
        EngineFilterConfiguration filterConfiguration = createFilterConfiguration(script);

        FilterValidator validator = new FilterValidator();
        FilterInputFactory filterInputFactory = new FilterInputFactory();
        FilterInput filterInput = filterInputFactory.createFilterInputForCxSast(findingGroup, finding);
        boolean actualResult = validator.passesFilter(filterInput, filterConfiguration);
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
        FilterValidator filterValidator = new FilterValidator();
        EngineFilterConfiguration filterConfiguration = EngineFilterConfiguration.builder()
                .simpleFilters(filters)
                .build();

        FilterInputFactory filterInputFactory = new FilterInputFactory();
        FilterInput filterInput = filterInputFactory.createFilterInputForCxSast(findingGroup, finding);
        boolean passes = filterValidator.passesFilter(filterInput, filterConfiguration);
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
        finding.setNodeId("test");
        finding.setStatus(status);
        finding.setState(state);
        return finding;
    }

    private static EngineFilterConfiguration createFilterConfiguration(Script script) {
        ScriptedFilter filter = ScriptedFilter.builder()
                .script(script)
                .build();

        return EngineFilterConfiguration.builder()
                .scriptedFilter(filter)
                .build();
    }
}