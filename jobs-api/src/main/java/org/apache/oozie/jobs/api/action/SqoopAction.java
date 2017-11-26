package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SqoopAction extends Node {
    private final ActionAttributes attributes;
    private final String command;

    SqoopAction(final ConstructionData constructionData,
                final ActionAttributes attributes,
                final String command) {
        super(constructionData);

        this.attributes = attributes;
        this.command = command;
    }

    public String getJobTracker() {
        return attributes.getJobTracker();
    }

    public String getResourceManager() {
        return attributes.getResourceManager();
    }

    public String getNameNode() {
        return attributes.getNameNode();
    }

    public Prepare getPrepare() {
        return attributes.getPrepare();
    }

    public Launcher getLauncher() {
        return attributes.getLauncher();
    }

    public ImmutableList<String> getJobXmls() {
        return attributes.getJobXmls();
    }

    public String getConfigProperty(final String property) {
        return attributes.getConfiguration().get(property);
    }

    public ImmutableMap<String, String> getConfiguration() {
        return attributes.getConfiguration();
    }

    public String getCommand() {
        return command;
    }

    public ImmutableList<String> getArguments() {
        return attributes.getArgs();
    }

    public ImmutableList<String> getFiles() {
        return attributes.getFiles();
    }

    public ImmutableList<String> getArchives() {
        return attributes.getArchives();
    }

    ActionAttributes getAttributes() {
        return attributes;
    }
}