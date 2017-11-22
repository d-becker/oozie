package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.List;

public class Hive2ActionBuilder extends NodeBuilderBaseImpl<Hive2ActionBuilder> implements Builder<Hive2Action> {
    private final ActionAttributesBuilder attributesBuilder;
    private final ModifyOnce<String> jdbcUrl;
    private final ModifyOnce<String> password;
    private final ModifyOnce<String> script;
    private final ModifyOnce<String> query;
    private final List<String> params;

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

        this.attributesBuilder = attributesBuilder;
        this.jdbcUrl = jdbcUrl;
        this.password = password;
        this.script = script;
        this.query = query;
        this.params = params;
    }

    public Hive2ActionBuilder withJobTracker(final String jobTracker) {
        this.attributesBuilder.withJobTracker(jobTracker);
        return this;
    }

    public Hive2ActionBuilder withResourceManager(final String resourceManager) {
        this.attributesBuilder.withResourceManager(resourceManager);
        return this;
    }

    public Hive2ActionBuilder withNameNode(final String nameNode) {
        this.attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public Hive2ActionBuilder withPrepare(final Prepare prepare) {
        this.attributesBuilder.withPrepare(prepare);
        return this;
    }

    public Hive2ActionBuilder withLauncher(final Launcher launcher) {
        this.attributesBuilder.withLauncher(launcher);
        return this;
    }

    public Hive2ActionBuilder withJobXml(final String jobXml) {
        this.attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public Hive2ActionBuilder withoutJobXml(final String jobXml) {
        this.attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public Hive2ActionBuilder clearJobXmls() {
        this.attributesBuilder.clearJobXmls();
        return this;
    }

    public Hive2ActionBuilder withConfigProperty(final String key, final String value) {
        this.attributesBuilder.withConfigProperty(key, value);
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

    public Hive2ActionBuilder withScript(final String script) {
        this.script.set(script);
        return this;
    }

    public Hive2ActionBuilder withQuery(final String query) {
        this.script.set(query);
        return this;
    }

    public Hive2ActionBuilder withParam(final String param) {
        this.params.add(param);
        return this;
    }

    public Hive2ActionBuilder withoutParam(final String param) {
        this.params.remove(param);
        return this;
    }

    public Hive2ActionBuilder clearParams() {
        this.params.clear();
        return this;
    }

    public Hive2ActionBuilder withArg(final String arg) {
        this.attributesBuilder.withArg(arg);
        return this;
    }

    public Hive2ActionBuilder withoutArg(final String arg) {
        this.attributesBuilder.withoutArg(arg);
        return this;
    }

    public Hive2ActionBuilder clearArgs() {
        this.attributesBuilder.clearArgs();
        return this;
    }

    public Hive2ActionBuilder withFile(final String file) {
        this.attributesBuilder.withFile(file);
        return this;
    }

    public Hive2ActionBuilder withoutFile(final String file) {
        this.attributesBuilder.withoutFile(file);
        return this;
    }

    public Hive2ActionBuilder clearFiles() {
        this.attributesBuilder.clearFiles();
        return this;
    }

    public Hive2ActionBuilder withArchive(final String archive) {
        this.attributesBuilder.withArchive(archive);
        return this;
    }

    public Hive2ActionBuilder withoutArchive(final String archive) {
        this.attributesBuilder.withoutArchive(archive);
        return this;
    }

    public Hive2ActionBuilder clearArchive() {
        this.attributesBuilder.clearArchives();
        return this;
    }

    @Override
    public Hive2Action build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final Hive2Action instance = new Hive2Action(
                constructionData,
                attributesBuilder.build(),
                jdbcUrl.get(),
                password.get(),
                script.get(),
                query.get(),
                ImmutableList.copyOf(params));

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected Hive2ActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
