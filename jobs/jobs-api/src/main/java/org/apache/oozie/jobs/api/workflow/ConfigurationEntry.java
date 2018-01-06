package org.apache.oozie.jobs.api.workflow;

public class ConfigurationEntry {
    private final String name;
    private final String value;
    private final String description;

    public ConfigurationEntry(final String name, final String description) {
        this(name, description, null);
    }

    public ConfigurationEntry(final String name, final String value, final String description) {
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
