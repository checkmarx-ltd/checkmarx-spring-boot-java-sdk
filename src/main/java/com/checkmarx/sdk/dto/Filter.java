package com.checkmarx.sdk.dto;

import java.beans.ConstructorProperties;
import java.util.Objects;


public class Filter {
    private Type type;
    private String value;

    @ConstructorProperties({"type", "value"})
    public Filter(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public static FilterBuilder builder() {
        return new FilterBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Filter)) return false;
        Filter filter = (Filter) o;
        return getType().equals(filter.getType())  &&
                getValue().equalsIgnoreCase(filter.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getValue());
    }

    public Type getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "Filter(type=" + this.getType() + ", value=" + this.getValue() + ")";
    }

    public enum Type {
        SEVERITY("SEVERITY"),
        CWE("CWE"),
        OWASP("OWASP"),

        // Filter by vulnerability type aka category aka name.
        TYPE("TYPE"),

        STATUS("STATUS"),
        STATE("STATE");

        private final String typeName;

        Type(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    public enum Severity {
        CRITICAL("Critical"),
        HIGH("High"),
        MEDIUM("Medium"),
        LOW("Low"),
        INFO("Informational");

        private final String severityName;

        Severity(String severityName) {
            this.severityName = severityName;
        }

        public String getSeverityName() {
            return severityName;
        }
    }

    public static class FilterBuilder {
        private Type type;
        private String value;

        FilterBuilder() {
        }

        public Filter.FilterBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public Filter.FilterBuilder value(String value) {
            this.value = value;
            return this;
        }

        public Filter build() {
            return new Filter(type, value);
        }

        public String toString() {
            return "Filter.FilterBuilder(type=" + this.type + ", value=" + this.value + ")";
        }
    }
}
