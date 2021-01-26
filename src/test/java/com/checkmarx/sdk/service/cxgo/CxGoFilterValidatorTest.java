package com.checkmarx.sdk.service.cxgo;

import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.filtering.*;
import com.checkmarx.sdk.exception.CheckmarxRuntimeException;
import com.checkmarx.sdk.service.FilterValidator;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class CxGoFilterValidatorTest {
    private static final String STATUS_RECURRENT = "RECURRENT";
    private static final String STATUS_NEW = "NEW";
    private static final String STATE_URGENT_NAME = "URGENT";
    private static final String STATE_VERIFY_NAME = "TO_VERIFY";
    private static final String SEVERITY_HIGH = "HIGH";
    private static final String SEVERITY_MEDIUM = "MEDIUM";
    private static final String SEVERITY_LOW = "LOW";
    private static final String CATEGORY1 = "CROSS_SITE_HISTORY_MANIPULATION";
    private static final String CATEGORY2 = "CLIENT_POTENTIAL_XSS";
    private static final String CWE1 = "203";
    private static final String CWE2 = "611";
    private static final String PERFORMANCE_TEST_SCRIPT = "finding.severity == 'HIGH' || finding.severity == 'MEDIUM'";
    private static final Duration MAX_ALLOWED_DURATION = Duration.ofSeconds(20);

    @Test
    public void passesFilter_scriptTypicalExample() {
        String scriptText = "finding.severity == 'HIGH' || (finding.severity == 'MEDIUM' && finding.state == 'URGENT')";

        Script script = parse(scriptText);
        verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT_NAME, CATEGORY1, CWE1, true);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_URGENT_NAME, CATEGORY1, CWE1, true);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_NAME, CATEGORY1, CWE1, false);
        verifyScriptResult(script, SEVERITY_LOW, STATUS_NEW, STATE_URGENT_NAME, CATEGORY1, CWE1, false);
    }

    @Test
    public void passesFilter_allPropertiesInScript() {
        String scriptText = "finding.severity == 'MEDIUM' && finding.state == 'TO_VERIFY' && finding.status == 'NEW'" +
                "&& finding.cwe == '203' && finding.category == 'CROSS_SITE_HISTORY_MANIPULATION'";

        Script script = parse(scriptText);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_NAME, CATEGORY1, CWE1, true);
        verifyScriptResult(script, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY_NAME, CATEGORY1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_RECURRENT, STATE_VERIFY_NAME, CATEGORY1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_URGENT_NAME, CATEGORY1, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_NAME, CATEGORY2, CWE1, false);
        verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_NAME, CATEGORY1, CWE2, false);
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
            verifyScriptResult(script, SEVERITY_MEDIUM, STATUS_NEW, STATE_VERIFY_NAME, CATEGORY1, CWE1, true);
            verifyScriptResult(script, SEVERITY_LOW, STATUS_RECURRENT, STATE_URGENT_NAME, CATEGORY1, CWE1, false);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY_NAME, CATEGORY1, CWE1, true);
            verifyScriptResult(script, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT_NAME, CATEGORY1, CWE1, true);
        }
        long end = System.currentTimeMillis();

        Duration actualDuration = Duration.ofMillis(end - start);
        log.info("Evaluation took {}.", actualDuration);

        assertTrue(MAX_ALLOWED_DURATION.compareTo(actualDuration) >= 0,
                String.format("Filter evaluation took too long (more than %s).", MAX_ALLOWED_DURATION));
    }

    @Test
    public void passesFilter_allSimpleFilters() {
        Filter severity = Filter.builder().type(Filter.Type.SEVERITY).value(SEVERITY_HIGH).build();
        Filter cwe = Filter.builder().type(Filter.Type.CWE).value(CWE1).build();
        Filter type = Filter.builder().type(Filter.Type.TYPE).value(CATEGORY1).build();
        Filter status = Filter.builder().type(Filter.Type.STATUS).value(STATUS_NEW).build();
        Filter state = Filter.builder().type(Filter.Type.STATE).value(STATE_URGENT_NAME).build();
        List<Filter> filters = Arrays.asList(severity, cwe, type, status, state);

        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_NEW, STATE_URGENT_NAME, CATEGORY1, CWE1, true);
        verifySimpleFilterResult(filters, SEVERITY_MEDIUM, STATUS_NEW, STATE_URGENT_NAME, CATEGORY1, CWE1, false);
        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_RECURRENT, STATE_URGENT_NAME, CATEGORY1, CWE1, false);
        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_NEW, STATE_VERIFY_NAME, CATEGORY1, CWE1, false);
        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_NEW, STATE_URGENT_NAME, CATEGORY2, CWE1, false);
        verifySimpleFilterResult(filters, SEVERITY_HIGH, STATUS_NEW, STATE_URGENT_NAME, CATEGORY1, CWE2, false);
    }

    @Test
    public void passesFilter_score() {
        verifyScoreFilter(6.12, "6.1", true);
        verifyScoreFilter(6.12, "6.12", true);
        verifyScoreFilter(0.01, "0.01", true);
        verifyScoreFilter(3D, "5.7", false);
        verifyScoreFilter(0D, "0.02", false);
        verifyScoreFilter(null, "5.7", true);
        verifyScoreFilter(6.1, null, true);
        verifyScoreFilter(6.1, "I'm not a number", true);
        verifyScoreFilter(6.1, "", true);
    }

    private void verifyScoreFilter(Double valueToCheck, String valueFromFilter, boolean shouldPass) {
        Filter score = Filter.builder().type(Filter.Type.SCORE).value(valueFromFilter).build();

        EngineFilterConfiguration scaFilterConfig = EngineFilterConfiguration.builder()
                .simpleFilters(Collections.singletonList(score))
                .build();

        FilterInput input = FilterInput.builder().id("424").score(valueToCheck).build();

        String message = String.format("Unexpected score filter result (valueToCheck: %f, valueFromFilter: %s)",
                valueToCheck, valueFromFilter);

        boolean actuallyPassed = new FilterValidator().passesFilter(input, scaFilterConfig);

        Assert.assertEquals(message, shouldPass, actuallyPassed);
    }

    private void validateExpectedError(String scriptWithRuntimeError) {
        Script script = parse(scriptWithRuntimeError);

        FilterInput finding = createFilterInput(SEVERITY_LOW, CATEGORY1, STATUS_NEW, STATE_URGENT_NAME, CWE1);

        EngineFilterConfiguration filterConfiguration = createFilterConfiguration(script);
        FilterValidator validator = new FilterValidator();

        try {
            validator.passesFilter(finding, filterConfiguration);
        } catch (Exception e) {
            assertTrue(e instanceof CheckmarxRuntimeException, String.format("Expected %s to be thrown.", CheckmarxRuntimeException.class));
            assertTrue(e.getCause() instanceof GroovyRuntimeException, String.format("Expected exception cause to be %s", GroovyRuntimeException.class));
        }
    }

    private static FilterInput createFilterInput(String severity, String category, String status, String stateName, String cweId) {
        return FilterInput.builder()
                .id("9389081")
                .category(category)
                .cwe(cweId)
                .severity(severity)
                .status(status)
                .state(stateName)
                .build();
    }

    private static Script parse(String scriptText) {
        GroovyShell groovyShell = new GroovyShell();
        return groovyShell.parse(scriptText);
    }

    private static void verifyScriptResult(Script script,
                                           String severity,
                                           String status,
                                           String state,
                                           String category,
                                           String cweId,
                                           boolean expectedResult) {
        FilterInput finding = createFilterInput(severity, category, status, state, cweId);
        EngineFilterConfiguration filterConfiguration = createFilterConfiguration(script);

        FilterValidator validator = new FilterValidator();
        boolean actualResult = validator.passesFilter(finding, filterConfiguration);
        assertEquals(expectedResult, actualResult, "Unexpected script filtering result.");
    }

    private static void verifySimpleFilterResult(List<Filter> filters,
                                                 String severity,
                                                 String status,
                                                 String state,
                                                 String category,
                                                 String cweId,
                                                 boolean expectedResult) {
        FilterInput finding = createFilterInput(severity, category, status, state, cweId);
        FilterValidator filterValidator = new FilterValidator();
        FilterConfiguration filterConfiguration = FilterConfiguration.fromSimpleFilters(filters);
        boolean passes = filterValidator.passesFilter(finding, filterConfiguration.getSastFilters());
        assertEquals(expectedResult, passes, "Unexpected simple filtering result.");
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