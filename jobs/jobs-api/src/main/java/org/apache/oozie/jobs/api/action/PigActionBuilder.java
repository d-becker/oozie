package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public class PigActionBuilder extends NodeBuilderBaseImpl<PigActionBuilder> implements Builder<PigAction> {
    protected final ActionAttributesBuilder attributesBuilder;
    protected final ModifyOnce<String> script;
    protected final List<String> params;

    public static PigActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();
        final ModifyOnce<String> script = new ModifyOnce<>();
        final List<String> params = new ArrayList<>();

        return new PigActionBuilder(
                null,
                builder,
                script,
                params);
    }

    public static PigActionBuilder createFromExistingAction(final PigAction action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());
        final ModifyOnce<String> script = new ModifyOnce<>(action.getScript());
        final List<String> params = new ArrayList<>(action.getParams());

        return new PigActionBuilder(action,
                builder,
                script,
                params);
    }

    public static PigActionBuilder createFromExistingAction(final Node action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromAction(action);
        final ModifyOnce<String> script = new ModifyOnce<>();
        final List<String> params = new ArrayList<>();

        return new PigActionBuilder(action,
                builder,
                script,
                params);
    }

    PigActionBuilder(final Node action,
                     final ActionAttributesBuilder attributesBuilder,
                     final ModifyOnce<String> script,
                     final List<String> params) {
        super(action);

        this.attributesBuilder = attributesBuilder;
        this.script = script;
        this.params = params;
    }

    public PigActionBuilder withJobTracker(final String jobTracker) {
        this.attributesBuilder.withJobTracker(jobTracker);
        return this;
    }

    public PigActionBuilder withResourceManager(final String resourceManager) {
        this.attributesBuilder.withResourceManager(resourceManager);
        return this;
    }

    public PigActionBuilder withNameNode(final String nameNode) {
        this.attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public PigActionBuilder withPrepare(final Prepare prepare) {
        this.attributesBuilder.withPrepare(prepare);
        return this;
    }

    public PigActionBuilder withLauncher(final Launcher launcher) {
        this.attributesBuilder.withLauncher(launcher);
        return this;
    }

    public PigActionBuilder withJobXml(final String jobXml) {
        this.attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public PigActionBuilder withoutJobXml(final String jobXml) {
        this.attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public PigActionBuilder clearJobXmls() {
        this.attributesBuilder.clearJobXmls();
        return this;
    }

    public PigActionBuilder withConfigProperty(final String key, final String value) {
        this.attributesBuilder.withConfigProperty(key, value);
        return this;
    }

    public PigActionBuilder withScript(final String script) {
        this.script.set(script);
        return this;
    }

    public PigActionBuilder withParam(final String param) {
        this.params.add(param);
        return this;
    }

    public PigActionBuilder withoutParam(final String param) {
        this.params.remove(param);
        return this;
    }

    public PigActionBuilder clearParams() {
        this.params.clear();
        return this;
    }

    public PigActionBuilder withArg(final String arg) {
        this.attributesBuilder.withArg(arg);
        return this;
    }

    public PigActionBuilder withoutArg(final String arg) {
        this.attributesBuilder.withoutArg(arg);
        return this;
    }

    public PigActionBuilder clearArgs() {
        this.attributesBuilder.clearArgs();
        return this;
    }

    public PigActionBuilder withFile(final String file) {
        this.attributesBuilder.withFile(file);
        return this;
    }

    public PigActionBuilder withoutFile(final String file) {
        this.attributesBuilder.withoutFile(file);
        return this;
    }

    public PigActionBuilder clearFiles() {
        this.attributesBuilder.clearFiles();
        return this;
    }

    public PigActionBuilder withArchive(final String archive) {
        this.attributesBuilder.withArchive(archive);
        return this;
    }

    public PigActionBuilder withoutArchive(final String archive) {
        this.attributesBuilder.withoutArchive(archive);
        return this;
    }

    public PigActionBuilder clearArchives() {
        this.attributesBuilder.clearArchives();
        return this;
    }

    @Override
    public PigAction build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final PigAction instance = new PigAction(
                constructionData,
                attributesBuilder.build(),
                script.get(),
                ImmutableList.copyOf(params));

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected PigActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
