package org.apache.oozie.jobs.api.workflow;

import org.apache.oozie.jobs.api.action.ActionAttributesBuilder;
import org.apache.oozie.jobs.api.action.Builder;
import org.apache.oozie.jobs.api.action.HasAttributes;
import org.apache.oozie.jobs.api.action.Launcher;

public class GlobalBuilder implements Builder<Global> {
    private final ActionAttributesBuilder attributesBuilder;

    public static GlobalBuilder create() {
        return new GlobalBuilder(ActionAttributesBuilder.create());
    }

    public static GlobalBuilder createFromExisting(final HasAttributes hasAttributes) {
        return new GlobalBuilder(ActionAttributesBuilder.createFromExisting(hasAttributes.getAttributes()));
    }

    private GlobalBuilder(final ActionAttributesBuilder attributesBuilder) {
        this.attributesBuilder = attributesBuilder;
    }

    public GlobalBuilder withJobTracker(final String jobTracker) {
        attributesBuilder.withJobTracker(jobTracker);
        return this;
    }

    public GlobalBuilder withResourceManager(final String resourceManager) {
        attributesBuilder.withResourceManager(resourceManager);
        return this;
    }

    public GlobalBuilder withNameNode(final String nameNode) {
        attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public GlobalBuilder withLauncher(final Launcher launcher) {
        attributesBuilder.withLauncher(launcher);
        return this;
    }

    public GlobalBuilder withJobXml(final String jobXml) {
        attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public GlobalBuilder withoutJobXml(final String jobXml) {
        attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public GlobalBuilder clearJobXmls() {
        attributesBuilder.clearJobXmls();
        return this;
    }

    public GlobalBuilder withConfigProperty(final String key, final String value) {
        attributesBuilder.withConfigProperty(key, value);
        return this;
    }

    @Override
    public Global build() {
        return new Global(attributesBuilder.build());
    }
}
