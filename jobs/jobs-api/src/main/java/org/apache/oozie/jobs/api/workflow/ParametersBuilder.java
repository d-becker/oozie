package org.apache.oozie.jobs.api.workflow;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.action.Builder;

public class ParametersBuilder implements Builder<Parameters> {
    private final ImmutableList.Builder<Parameter> parameters;

    public static ParametersBuilder create() {
        return new ParametersBuilder(new ImmutableList.Builder<Parameter>());
    }

    public static ParametersBuilder createFromExisting(final Parameters parameters) {
        return new ParametersBuilder(new ImmutableList.Builder<Parameter>().addAll(parameters.getParameters()));
    }

    ParametersBuilder(final ImmutableList.Builder<Parameter> parameters) {
        this.parameters = parameters;
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
