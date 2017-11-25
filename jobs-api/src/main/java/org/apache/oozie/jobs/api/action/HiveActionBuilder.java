package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public class HiveActionBuilder extends NodeBuilderBaseImpl<HiveActionBuilder> implements Builder<HiveAction> {
    private final PigActionBuilder delegate;
    protected final ModifyOnce<String> query;

    public static HiveActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();
        final ModifyOnce<String> script = new ModifyOnce<>();
        final ModifyOnce<String> query = new ModifyOnce<>();
        final List<String> params = new ArrayList<>();

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
        final List<String> params = new ArrayList<>(action.getParams());

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

        this.delegate = new PigActionBuilder(action,
                                             attributesBuilder,
                                             script,
                                             params);

        this.query = query;
    }

    public HiveActionBuilder withJobTracker(final String jobTracker) {
        this.delegate.withJobTracker(jobTracker);
        return this;
    }

    public HiveActionBuilder withResourceManager(final String resourceManager) {
        this.delegate.withResourceManager(resourceManager);
        return this;
    }

    public HiveActionBuilder withNameNode(final String nameNode) {
        this.delegate.withNameNode(nameNode);
        return this;
    }

    public HiveActionBuilder withPrepare(final Prepare prepare) {
        this.delegate.withPrepare(prepare);
        return this;
    }

    public HiveActionBuilder withLauncher(final Launcher launcher) {
        this.delegate.withLauncher(launcher);
        return this;
    }

    public HiveActionBuilder withJobXml(final String jobXml) {
        this.delegate.withJobXml(jobXml);
        return this;
    }

    public HiveActionBuilder withoutJobXml(final String jobXml) {
        this.delegate.withoutJobXml(jobXml);
        return this;
    }

    public HiveActionBuilder clearJobXmls() {
        this.delegate.clearJobXmls();
        return this;
    }

    public HiveActionBuilder withConfigProperty(final String key, final String value) {
        this.delegate.withConfigProperty(key, value);
        return this;
    }

    public HiveActionBuilder withScript(final String script) {
        this.delegate.withScript(script);
        return this;
    }

    public HiveActionBuilder withQuery(final String query) {
        this.query.set(query);
        return this;
    }

    public HiveActionBuilder withParam(final String param) {
        this.delegate.withParam(param);
        return this;
    }

    public HiveActionBuilder withoutParam(final String param) {
        this.delegate.withoutParam(param);
        return this;
    }

    public HiveActionBuilder clearParams() {
        this.delegate.clearParams();
        return this;
    }

    public HiveActionBuilder withArg(final String arg) {
        this.delegate.withArg(arg);
        return this;
    }

    public HiveActionBuilder withoutArg(final String arg) {
        this.delegate.withoutArg(arg);
        return this;
    }

    public HiveActionBuilder clearArgs() {
        this.delegate.clearArgs();
        return this;
    }

    public HiveActionBuilder withFile(final String file) {
        this.delegate.withFile(file);
        return this;
    }

    public HiveActionBuilder withoutFile(final String file) {
        this.delegate.withoutFile(file);
        return this;
    }

    public HiveActionBuilder clearFiles() {
        this.delegate.clearFiles();
        return this;
    }

    public HiveActionBuilder withArchive(final String archive) {
        this.delegate.withArchive(archive);
        return this;
    }

    public HiveActionBuilder withoutArchive(final String archive) {
        this.delegate.withoutArchive(archive);
        return this;
    }

    public HiveActionBuilder clearArchives() {
        this.delegate.clearArchives();
        return this;
    }

    ActionAttributesBuilder getAttributesBuilder() {
        return delegate.attributesBuilder;
    }

    ModifyOnce<String> getScript() {
        return delegate.script;
    }

    List<String> getParams() {
        return delegate.params;
    }

    @Override
    public HiveAction build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final HiveAction instance = new HiveAction(
                constructionData,
                getAttributesBuilder().build(),
                getScript().get(),
                query.get(),
                ImmutableList.copyOf(getParams()));

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected HiveActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
