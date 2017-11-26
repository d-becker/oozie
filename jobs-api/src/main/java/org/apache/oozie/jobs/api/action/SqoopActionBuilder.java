package org.apache.oozie.jobs.api.action;

import org.apache.oozie.jobs.api.ModifyOnce;

public class SqoopActionBuilder extends NodeBuilderBaseImpl<SqoopActionBuilder> implements Builder<SqoopAction> {
    private final ActionAttributesBuilder attributesBuilder;
    private final ModifyOnce<String> command;

    public static SqoopActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();
        final ModifyOnce<String> command = new ModifyOnce<>();

        return new SqoopActionBuilder(
                null,
                builder,
                command);
    }

    public static SqoopActionBuilder createFromExistingAction(final SqoopAction action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());
        final ModifyOnce<String> command = new ModifyOnce<>(action.getCommand());

        return new SqoopActionBuilder(action,
                builder,
                command);
    }

    private SqoopActionBuilder(final SqoopAction action,
                               final ActionAttributesBuilder attributesBuilder,
                               final ModifyOnce<String> command) {
        super(action);

        this.attributesBuilder = attributesBuilder;
        this.command = command;
    }

    public SqoopActionBuilder withJobTracker(final String jobTracker) {
        this.attributesBuilder.withJobTracker(jobTracker);
        return this;
    }

    public SqoopActionBuilder withResourceManager(final String resourceManager) {
        this.attributesBuilder.withResourceManager(resourceManager);
        return this;
    }

    public SqoopActionBuilder withNameNode(final String nameNode) {
        this.attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public SqoopActionBuilder withPrepare(final Prepare prepare) {
        this.attributesBuilder.withPrepare(prepare);
        return this;
    }

    public SqoopActionBuilder withLauncher(final Launcher launcher) {
        this.attributesBuilder.withLauncher(launcher);
        return this;
    }

    public SqoopActionBuilder withJobXml(final String jobXml) {
        this.attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public SqoopActionBuilder withoutJobXml(final String jobXml) {
        this.attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public SqoopActionBuilder clearJobXmls() {
        this.attributesBuilder.clearJobXmls();
        return this;
    }

    public SqoopActionBuilder withConfigProperty(final String key, final String value) {
        this.attributesBuilder.withConfigProperty(key, value);
        return this;
    }

    public SqoopActionBuilder withCommand(final String command) {
        this.command.set(command);
        return this;
    }

    public SqoopActionBuilder withArgument(final String argument) {
        this.attributesBuilder.withArg(argument);
        return this;
    }

    public SqoopActionBuilder withoutArgument(final String argument) {
        this.attributesBuilder.withoutArg(argument);
        return this;
    }

    public SqoopActionBuilder clearArguments() {
        this.attributesBuilder.clearArgs();
        return this;
    }

    public SqoopActionBuilder withFile(final String file) {
        this.attributesBuilder.withFile(file);
        return this;
    }

    public SqoopActionBuilder withoutFile(final String file) {
        this.attributesBuilder.withoutFile(file);
        return this;
    }

    public SqoopActionBuilder clearFiles() {
        this.attributesBuilder.clearFiles();
        return this;
    }

    public SqoopActionBuilder withArchive(final String archive) {
        this.attributesBuilder.withArchive(archive);
        return this;
    }

    public SqoopActionBuilder withoutArchive(final String archive) {
        this.attributesBuilder.withoutArchive(archive);
        return this;
    }

    public SqoopActionBuilder clearArchives() {
        this.attributesBuilder.clearArchives();
        return this;
    }

    @Override
    public SqoopAction build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final SqoopAction instance = new SqoopAction(
                constructionData,
                attributesBuilder.build(),
                command.get());

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected SqoopActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
