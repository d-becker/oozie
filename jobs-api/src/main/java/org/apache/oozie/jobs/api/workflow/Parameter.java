package org.apache.oozie.jobs.api.workflow;

public class Parameter {
    private final String name;
    private final String value;
    private final String description;

    public Parameter(final String name, final String value, final String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
