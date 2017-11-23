package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.List;

public class HiveActionBuilder extends NodeBuilderBaseImpl<HiveActionBuilder> implements Builder<HiveAction> {
    protected final ActionAttributesBuilder attributesBuilder;
    protected final ModifyOnce<String> script;
    protected final ModifyOnce<String> query;
    protected final List<String> params;

    public static HiveActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();
        final ModifyOnce<String> script = new ModifyOnce<>();
        final ModifyOnce<String> query = new ModifyOnce<>();
        final List<String> params = ImmutableList.of();

        return new HiveActionBuilder(
                null,
                builder,
                script,
                query,
                params);
    }

    public static HiveActionBuilder createFromExistingAction(final HiveAction action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());
        final ModifyOnce<String> script = new ModifyOnce<>(action.getScript());
        final ModifyOnce<String> query = new ModifyOnce<>(action.getQuery());
        final List<String> params = ImmutableList.copyOf(action.getParams());

        return new HiveActionBuilder(action,
                builder,
                script,
                query,
                params);
    }

    HiveActionBuilder(final HiveAction action,
                      final ActionAttributesBuilder attributesBuilder,
                      final ModifyOnce<String> script,
                      final ModifyOnce<String> query,
                      final List<String> params) {
        super(action);

        this.attributesBuilder = attributesBuilder;
        this.script = script;
        this.query = query;
        this.params = params;
    }

    public HiveActionBuilder withJobTracker(final String jobTracker) {
        this.attributesBuilder.withJobTracker(jobTracker);
        return this;
    }

    public HiveActionBuilder withResourceManager(final String resourceManager) {
        this.attributesBuilder.withResourceManager(resourceManager);
        return this;
    }

    public HiveActionBuilder withNameNode(final String nameNode) {
        this.attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public HiveActionBuilder withPrepare(final Prepare prepare) {
        this.attributesBuilder.withPrepare(prepare);
        return this;
    }

    public HiveActionBuilder withLauncher(final Launcher launcher) {
        this.attributesBuilder.withLauncher(launcher);
        return this;
    }

    public HiveActionBuilder withJobXml(final String jobXml) {
        this.attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public HiveActionBuilder withoutJobXml(final String jobXml) {
        this.attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public HiveActionBuilder clearJobXmls() {
        this.attributesBuilder.clearJobXmls();
        return this;
    }

    public HiveActionBuilder withConfigProperty(final String key, final String value) {
        this.attributesBuilder.withConfigProperty(key, value);
        return this;
    }

    public HiveActionBuilder withScript(final String script) {
        this.script.set(script);
        return this;
    }

    public HiveActionBuilder withQuery(final String query) {
        this.script.set(query);
        return this;
    }

    public HiveActionBuilder withParam(final String param) {
        this.params.add(param);
        return this;
    }

    public HiveActionBuilder withoutParam(final String param) {
        this.params.remove(param);
        return this;
    }

    public HiveActionBuilder clearParams() {
        this.params.clear();
        return this;
    }

    public HiveActionBuilder withArg(final String arg) {
        this.attributesBuilder.withArg(arg);
        return this;
    }

    public HiveActionBuilder withoutArg(final String arg) {
        this.attributesBuilder.withoutArg(arg);
        return this;
    }

    public HiveActionBuilder clearArgs() {
        this.attributesBuilder.clearArgs();
        return this;
    }

    public HiveActionBuilder withFile(final String file) {
        this.attributesBuilder.withFile(file);
        return this;
    }

    public HiveActionBuilder withoutFile(final String file) {
        this.attributesBuilder.withoutFile(file);
        return this;
    }

    public HiveActionBuilder clearFiles() {
        this.attributesBuilder.clearFiles();
        return this;
    }

    public HiveActionBuilder withArchive(final String archive) {
        this.attributesBuilder.withArchive(archive);
        return this;
    }

    public HiveActionBuilder withoutArchive(final String archive) {
        this.attributesBuilder.withoutArchive(archive);
        return this;
    }

    public HiveActionBuilder clearArchives() {
        this.attributesBuilder.clearArchives();
        return this;
    }

    @Override
    public HiveAction build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final HiveAction instance = new HiveAction(
                constructionData,
                attributesBuilder.build(),
                script.get(),
                query.get(),
                ImmutableList.copyOf(params));

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected HiveActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
