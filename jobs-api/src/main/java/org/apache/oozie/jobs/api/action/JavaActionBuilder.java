package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public class JavaActionBuilder extends NodeBuilderBaseImpl<JavaActionBuilder> implements Builder<JavaAction> {
    private final ActionAttributesBuilder attributesBuilder;
    private final ModifyOnce<String> mainClass;
    private final ModifyOnce<String> javaOptsString;
    private final List<String> javaOpts;

    public static JavaActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();
        final ModifyOnce<String> mainClass = new ModifyOnce<>();
        final ModifyOnce<String> javaOptsString = new ModifyOnce<>();
        final List<String> javaOpts = new ArrayList<>();

        return new JavaActionBuilder(
                null,
                builder,
                mainClass,
                javaOptsString,
                javaOpts);
    }

    public static JavaActionBuilder createFromExistingAction(final JavaAction action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());
        final ModifyOnce<String> mainClass = new ModifyOnce<>(action.getMainClass());
        final ModifyOnce<String> javaOptsString = new ModifyOnce<>(action.getJavaOptsString());
        final List<String> javaOpts = new ArrayList<>(action.getJavaOpts());

        return new JavaActionBuilder(action,
                builder,
                mainClass,
                javaOptsString,
                javaOpts);
    }

    private JavaActionBuilder(final JavaAction action,
                              final ActionAttributesBuilder attributesBuilder,
                              final ModifyOnce<String> mainClass,
                              final ModifyOnce<String> javaOptsString,
                              final List<String> javaOpts) {
        super(action);

        this.attributesBuilder = attributesBuilder;
        this.mainClass = mainClass;
        this.javaOptsString = javaOptsString;
        this.javaOpts = javaOpts;
    }

    public JavaActionBuilder withJobTracker(final String jobTracker) {
        this.attributesBuilder.withJobTracker(jobTracker);
        return this;
    }

    public JavaActionBuilder withResourceManager(final String resourceManager) {
        this.attributesBuilder.withResourceManager(resourceManager);
        return this;
    }

    public JavaActionBuilder withNameNode(final String nameNode) {
        this.attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public JavaActionBuilder withPrepare(final Prepare prepare) {
        this.attributesBuilder.withPrepare(prepare);
        return this;
    }

    public JavaActionBuilder withLauncher(final Launcher launcher) {
        this.attributesBuilder.withLauncher(launcher);
        return this;
    }

    public JavaActionBuilder withJobXml(final String jobXml) {
        this.attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public JavaActionBuilder withoutJobXml(final String jobXml) {
        this.attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public JavaActionBuilder clearJobXmls() {
        this.attributesBuilder.clearJobXmls();
        return this;
    }

    public JavaActionBuilder withConfigProperty(final String key, final String value) {
        this.attributesBuilder.withConfigProperty(key, value);
        return this;
    }

    public JavaActionBuilder withMainClass(final String mainClass) {
        this.mainClass.set(mainClass);
        return this;
    }

    public JavaActionBuilder withJavaOptsString(final String javaOptsString) {
        this.javaOptsString.set(javaOptsString);
        return this;
    }

    public JavaActionBuilder withJavaOpt(final String javaOpt) {
        this.javaOpts.add(javaOpt);
        return this;
    }

    public JavaActionBuilder withoutJavaOpt(final String javaOpt) {
        this.javaOpts.remove(javaOpt);
        return this;
    }

    public JavaActionBuilder clearJavaOpts() {
        this.javaOpts.clear();
        return this;
    }

    public JavaActionBuilder withArg(final String arg) {
        this.attributesBuilder.withArg(arg);
        return this;
    }

    public JavaActionBuilder withoutArg(final String arg) {
        this.attributesBuilder.withoutArg(arg);
        return this;
    }

    public JavaActionBuilder clearArgs() {
        this.attributesBuilder.clearArgs();
        return this;
    }

    public JavaActionBuilder withFile(final String file) {
        this.attributesBuilder.withFile(file);
        return this;
    }

    public JavaActionBuilder withoutFile(final String file) {
        this.attributesBuilder.withoutFile(file);
        return this;
    }

    public JavaActionBuilder clearFiles() {
        this.attributesBuilder.clearFiles();
        return this;
    }

    public JavaActionBuilder withArchive(final String archive) {
        this.attributesBuilder.withArchive(archive);
        return this;
    }

    public JavaActionBuilder withoutArchive(final String archive) {
        this.attributesBuilder.withoutArchive(archive);
        return this;
    }

    public JavaActionBuilder clearArchives() {
        this.attributesBuilder.clearArchives();
        return this;
    }

    public JavaActionBuilder withCaptureOutput(final Boolean captureOutput) {
        this.attributesBuilder.withCaptureOutput(captureOutput);
        return this;
    }

    @Override
    public JavaAction build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final JavaAction instance = new JavaAction(
                constructionData,
                attributesBuilder.build(),
                mainClass.get(),
                javaOptsString.get(),
                ImmutableList.copyOf(javaOpts));

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected JavaActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
