package org.apache.oozie.jobs.api.workflow;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.action.Builder;

public class ParametersBuilder implements Builder<Parameters> {
    private final ImmutableList.Builder<Parameter> parameters;

    public ParametersBuilder() {
        this.parameters = new ImmutableList.Builder<>();
    }

    public ParametersBuilder withParameter(final String name, final String value) {
        return withParameter(name, value, null);
    }

    public ParametersBuilder withParameter(final String name, final String value, final String description) {
        parameters.add(new Parameter(name, value, description));
        return this;
    }

    @Override
    public Parameters build() {
        return new Parameters(parameters.build());
    }
}
