package org.apache.oozie.jobs.api.workflow;

import com.google.common.collect.ImmutableList;

public class Parameters {
    private final ImmutableList<Parameter> parameters;

    public Parameters(final ImmutableList<Parameter> parameters) {
        this.parameters = parameters;
    }

    public ImmutableList<Parameter> getParameters() {
        return parameters;
    }
}
