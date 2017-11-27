package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SshAction extends Node {
    private final ActionAttributes attributes;
    private final String host;
    private final String command;


    SshAction(final ConstructionData constructionData,
              final ActionAttributes attributes,
              final String host,
              final String command) {
        super(constructionData);

        this.attributes = attributes;
        this.host = host;
        this.command = command;
    }

    public String getHost() {
        return host;
    }

    public String getCommand() {
        return command;
    }

    public ImmutableList<String> getArgs() {
        return attributes.getArgs();
    }

    public boolean isCaptureOutput() {
        return attributes.isCaptureOutput();
    }

    ActionAttributes getAttributes() {
        return attributes;
    }
}