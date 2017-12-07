package org.apache.oozie.jobs.api.workflow;

import com.google.common.collect.ImmutableList;

public class Credential {
    private final String name;
    private final String type;
    private final ImmutableList<ConfigurationEntry> configurationEntries;

    public Credential(final String name, final String type, final ImmutableList<ConfigurationEntry> configurationEntries) {
        this.name = name;
        this.type = type;
        this.configurationEntries = configurationEntries;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ImmutableList<ConfigurationEntry> getConfigurationEntries() {
        return configurationEntries;
    }
}
