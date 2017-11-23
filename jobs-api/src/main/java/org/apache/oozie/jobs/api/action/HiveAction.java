package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class HiveAction extends Node {
    private final ActionAttributes attributes;
    private final String script;
    private final String query;
    private final ImmutableList<String> params;

    public HiveAction(final ConstructionData constructionData,
                      final ActionAttributes attributes,
                      final String script,
                      final String query,
                      final ImmutableList<String> params) {
        super(constructionData);

        this.attributes = attributes;
        this.script = script;
        this.query = query;
        this.params = params;
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

    public String getScript() {
        return script;
    }

    public String getQuery() {
        return query;
    }

    public ImmutableList<String> getParams() {
        return params;
    }

    public ImmutableList<String> getArgs() {
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