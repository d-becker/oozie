package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public class ShellActionBuilder extends NodeBuilderBaseImpl<ShellActionBuilder> implements Builder<ShellAction> {
    private final ActionAttributesBuilder attributesBuilder;
    private final ModifyOnce<String> executable;
    private final List<String> environmentVariables;

    public static ShellActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();
        final ModifyOnce<String> executable = new ModifyOnce<>();
        final List<String> environmentVariables = new ArrayList<>();

        return new ShellActionBuilder(
                null,
                builder,
                executable,
                environmentVariables);
    }

    public static ShellActionBuilder createFromExistingAction(final ShellAction action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());
        final ModifyOnce<String> executable = new ModifyOnce<>(action.getExecutable());
        final List<String> environmentVariables = new ArrayList<>(action.getEnvironmentVariables());

        return new ShellActionBuilder(action,
                builder,
                executable,
                environmentVariables);
    }

    public static ShellActionBuilder createFromExistingAction(final Node action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromAction(action);
        final ModifyOnce<String> executable = new ModifyOnce<>();
        final List<String> environmentVariables = new ArrayList<>();

        return new ShellActionBuilder(action,
                builder,
                executable,
                environmentVariables);
    }

    private ShellActionBuilder(final Node action,
                               final ActionAttributesBuilder attributesBuilder,
                               final ModifyOnce<String> executable,
                               final List<String> environmentVariables) {
        super(action);

        this.attributesBuilder = attributesBuilder;
        this.executable = executable;
        this.environmentVariables = environmentVariables;
    }

    public ShellActionBuilder withJobTracker(final String jobTracker) {
        this.attributesBuilder.withJobTracker(jobTracker);
        return this;
    }

    public ShellActionBuilder withResourceManager(final String resourceManager) {
        this.attributesBuilder.withResourceManager(resourceManager);
        return this;
    }

    public ShellActionBuilder withNameNode(final String nameNode) {
        this.attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public ShellActionBuilder withPrepare(final Prepare prepare) {
        this.attributesBuilder.withPrepare(prepare);
        return this;
    }

    public ShellActionBuilder withLauncher(final Launcher launcher) {
        this.attributesBuilder.withLauncher(launcher);
        return this;
    }

    public ShellActionBuilder withJobXml(final String jobXml) {
        this.attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public ShellActionBuilder withoutJobXml(final String jobXml) {
        this.attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public ShellActionBuilder clearJobXmls() {
        this.attributesBuilder.clearJobXmls();
        return this;
    }

    public ShellActionBuilder withConfigProperty(final String key, final String value) {
        this.attributesBuilder.withConfigProperty(key, value);
        return this;
    }

    public ShellActionBuilder withExecutable(final String executable) {
        this.executable.set(executable);
        return this;
    }

    public ShellActionBuilder withArgument(final String argument) {
        this.attributesBuilder.withArg(argument);
        return this;
    }

    public ShellActionBuilder withoutArgument(final String argument) {
        this.attributesBuilder.withoutArg(argument);
        return this;
    }

    public ShellActionBuilder clearArguments() {
        this.attributesBuilder.clearArgs();
        return this;
    }

    public ShellActionBuilder withEnvironmentVariable(final String environmentVariable) {
        this.environmentVariables.add(environmentVariable);
        return this;
    }

    public ShellActionBuilder withoutEnvironmentVariable(final String environmentVariable) {
        this.environmentVariables.remove(environmentVariable);
        return this;
    }

    public ShellActionBuilder clearEnvironmentVariables() {
        this.environmentVariables.clear();
        return this;
    }

    public ShellActionBuilder withFile(final String file) {
        this.attributesBuilder.withFile(file);
        return this;
    }

    public ShellActionBuilder withoutFile(final String file) {
        this.attributesBuilder.withoutFile(file);
        return this;
    }

    public ShellActionBuilder clearFiles() {
        this.attributesBuilder.clearFiles();
        return this;
    }

    public ShellActionBuilder withArchive(final String archive) {
        this.attributesBuilder.withArchive(archive);
        return this;
    }

    public ShellActionBuilder withoutArchive(final String archive) {
        this.attributesBuilder.withoutArchive(archive);
        return this;
    }

    public ShellActionBuilder clearArchives() {
        this.attributesBuilder.clearArchives();
        return this;
    }

    public ShellActionBuilder withCaptureOutput(final Boolean captureOutput) {
        this.attributesBuilder.withCaptureOutput(captureOutput);
        return this;
    }

    @Override
    public ShellAction build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final ShellAction instance = new ShellAction(
                constructionData,
                attributesBuilder.build(),
                executable.get(),
                ImmutableList.copyOf(environmentVariables));

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected ShellActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
