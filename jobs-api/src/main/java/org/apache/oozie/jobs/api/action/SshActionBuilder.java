package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public class SshActionBuilder extends NodeBuilderBaseImpl<SshActionBuilder> implements Builder<SshAction> {
    private final ActionAttributesBuilder attributesBuilder;
    private final ModifyOnce<String> host;
    private final ModifyOnce<String> command;

    public static SshActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();
        final ModifyOnce<String> host = new ModifyOnce<>();
        final ModifyOnce<String> command = new ModifyOnce<>();

        return new SshActionBuilder(
                null,
                builder,
                host,
                command);
    }

    public static SshActionBuilder createFromExistingAction(final SshAction action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());
        final ModifyOnce<String> host = new ModifyOnce<>(action.getHost());
        final ModifyOnce<String> command = new ModifyOnce<>(action.getCommand());

        return new SshActionBuilder(action,
                builder,
                host,
                command);
    }

    public static SshActionBuilder createFromExistingAction(final Node action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromAction(action);
        final ModifyOnce<String> host = new ModifyOnce<>();
        final ModifyOnce<String> command = new ModifyOnce<>();

        return new SshActionBuilder(action,
                builder,
                host,
                command);
    }

    private SshActionBuilder(final Node action,
                             final ActionAttributesBuilder attributesBuilder,
                             final ModifyOnce<String> host,
                             final ModifyOnce<String> command) {
        super(action);

        this.attributesBuilder = attributesBuilder;
        this.host = host;
        this.command = command;
    }

    public SshActionBuilder withHost(final String host) {
        this.host.set(host);
        return this;
    }

    public SshActionBuilder withCommand(final String command) {
        this.command.set(command);
        return this;
    }

    public SshActionBuilder withArg(final String arg) {
        this.attributesBuilder.withArg(arg);
        return this;
    }

    public SshActionBuilder withoutArg(final String arg) {
        this.attributesBuilder.withoutArg(arg);
        return this;
    }

    public SshActionBuilder clearArgs() {
        this.attributesBuilder.clearArgs();
        return this;
    }

    public SshActionBuilder withCaptureOutput(final Boolean captureOutput) {
        this.attributesBuilder.withCaptureOutput(captureOutput);
        return this;
    }

    @Override
    public SshAction build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final SshAction instance = new SshAction(
                constructionData,
                attributesBuilder.build(),
                host.get(),
                command.get());

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected SshActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
