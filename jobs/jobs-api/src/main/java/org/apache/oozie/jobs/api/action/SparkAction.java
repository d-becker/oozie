package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SparkAction extends Node implements HasAttributes {
    private final ActionAttributes attributes;
    private final String master;
    private final String mode;
    private final String actionName;
    private final String actionClass;
    private final String jar;
    private final String sparkOpts;

    public SparkAction(final ConstructionData constructionData,
                       final ActionAttributes attributes,
                       final String master,
                       final String mode,
                       final String actionName,
                       final String actionClass,
                       final String jar,
                       final String sparkOpts) {
        super(constructionData);

        this.attributes = attributes;
        this.master = master;
        this.mode = mode;
        this.actionName = actionName;
        this.actionClass = actionClass;
        this.jar = jar;
        this.sparkOpts = sparkOpts;
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

    public String getMaster() {
        return master;
    }

    public String getMode() {
        return mode;
    }

    public String getActionName() {
        return actionName;
    }

    public String getActionClass() {
        return actionClass;
    }

    public String getJar() {
        return jar;
    }

    public String getSparkOpts() {
        return sparkOpts;
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

    public ActionAttributes getAttributes() {
        return attributes;
    }
}