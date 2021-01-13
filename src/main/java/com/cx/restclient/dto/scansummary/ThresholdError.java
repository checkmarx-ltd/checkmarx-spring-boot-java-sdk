package com.cx.restclient.dto.scansummary;

public class ThresholdError {
    private ErrorSource source;
    private Severity severity;
    private final int value;
    private final Integer threshold;

    public ThresholdError(ErrorSource source, Severity severity, int value, Integer threshold) {
        this.source = source;
        this.severity = severity;
        this.value = value;
        this.threshold = threshold;
    }

    public ErrorSource getSource() {
        return source;
    }

    public Severity getSeverity() {
        return severity;
    }

    public int getValue() {
        return value;
    }

    public Integer getThreshold() {
        return threshold;
    }
}
