package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.List;

public class Hive2ActionBuilder extends NodeBuilderBaseImpl<Hive2ActionBuilder> implements Builder<Hive2Action> {
    private final HiveActionBuilder delegate;
    private final ModifyOnce<String> jdbcUrl;
    private final ModifyOnce<String> password;

    public static Hive2ActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();
        final ModifyOnce<String> jdbcUrl = new ModifyOnce<>();
        final ModifyOnce<String> password = new ModifyOnce<>();
        final ModifyOnce<String> script = new ModifyOnce<>();
        final ModifyOnce<String> query = new ModifyOnce<>();
        final List<String> params = ImmutableList.of();

        return new Hive2ActionBuilder(
                null,
                builder,
                jdbcUrl,
                password,
                script,
                query,
                params);
    }

    public static Hive2ActionBuilder createFromExistingAction(final Hive2Action action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());
        final ModifyOnce<String> jdbcUrl = new ModifyOnce<>(action.getJdbcUrl());
        final ModifyOnce<String> password = new ModifyOnce<>(action.getPassword());
        final ModifyOnce<String> script = new ModifyOnce<>(action.getScript());
        final ModifyOnce<String> query = new ModifyOnce<>(action.getQuery());
        final List<String> params = ImmutableList.copyOf(action.getParams());

        return new Hive2ActionBuilder(action,
                builder,
                jdbcUrl,
                password,
                script,
                query,
                params);
    }

    Hive2ActionBuilder(final Hive2Action action,
                       final ActionAttributesBuilder attributesBuilder,
                       final ModifyOnce<String> jdbcUrl,
                       final ModifyOnce<String> password,
                       final ModifyOnce<String> script,
                       final ModifyOnce<String> query,
                       final List<String> params) {
        super(action);

        this.delegate = new HiveActionBuilder(action,
                attributesBuilder,
                script,
                query,
                params);

        this.jdbcUrl = jdbcUrl;
        this.password = password;
    }

    public Hive2ActionBuilder withJobTracker(final String jobTracker) {
        delegate.withJobTracker(jobTracker);
        return this;
    }

    public Hive2ActionBuilder withResourceManager(final String resourceManager) {
        delegate.withResourceManager(resourceManager);
        return this;
    }

    public Hive2ActionBuilder withNameNode(final String nameNode) {
        delegate.withNameNode(nameNode);
        return this;
    }

    public Hive2ActionBuilder withPrepare(final Prepare prepare) {
        delegate.withPrepare(prepare);
        return this;
    }

    public Hive2ActionBuilder withLauncher(final Launcher launcher) {
        delegate.withLauncher(launcher);
        return this;
    }

    public Hive2ActionBuilder withJobXml(final String jobXml) {
        delegate.withJobXml(jobXml);
        return this;
    }

    public Hive2ActionBuilder withoutJobXml(final String jobXml) {
        delegate.withoutJobXml(jobXml);
        return this;
    }

    public Hive2ActionBuilder clearJobXmls() {
        delegate.clearJobXmls();
        return this;
    }

    public Hive2ActionBuilder withConfigProperty(final String key, final String value) {
        delegate.withConfigProperty(key, value);
        return this;
    }

    public Hive2ActionBuilder withScript(final String script) {
        delegate.withScript(script);
        return this;
    }

    public Hive2ActionBuilder withQuery(final String query) {
        delegate.withQuery(query);
        return this;
    }

    public Hive2ActionBuilder withParam(final String param) {
        delegate.withParam(param);
        return this;
    }

    public Hive2ActionBuilder withoutParam(final String param) {
        delegate.withoutParam(param);
        return this;
    }

    public Hive2ActionBuilder clearParams() {
        delegate.clearParams();
        return this;
    }

    public Hive2ActionBuilder withArg(final String arg) {
        delegate.withArg(arg);
        return this;
    }

    public Hive2ActionBuilder withoutArg(final String arg) {
        delegate.withoutArg(arg);
        return this;
    }

    public Hive2ActionBuilder clearArgs() {
        delegate.clearArgs();
        return this;
    }

    public Hive2ActionBuilder withFile(final String file) {
        delegate.withFile(file);
        return this;
    }

    public Hive2ActionBuilder withoutFile(final String file) {
        delegate.withoutFile(file);
        return this;
    }

    public Hive2ActionBuilder clearFiles() {
        delegate.clearFiles();
        return this;
    }

    public Hive2ActionBuilder withArchive(final String archive) {
        delegate.withArchive(archive);
        return this;
    }

    public Hive2ActionBuilder withoutArchive(final String archive) {
        delegate.withoutArchive(archive);
        return this;
    }

    public Hive2ActionBuilder clearArchives() {
        delegate.clearArchives();
        return this;
    }

    public Hive2ActionBuilder withJdbcUrl(final String jdbcUrl) {
        this.jdbcUrl.set(jdbcUrl);
        return this;
    }

    public Hive2ActionBuilder withPassword(final String password) {
        this.password.set(password);
        return this;
    }

    @Override
    public Hive2Action build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final Hive2Action instance = new Hive2Action(
                constructionData,
                delegate.getAttributesBuilder().build(),
                jdbcUrl.get(),
                password.get(),
                delegate.getScript().get(),
                delegate.query.get(),
                ImmutableList.copyOf(delegate.getParams()));

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected Hive2ActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
