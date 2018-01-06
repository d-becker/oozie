package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JavaAction extends Node {
    private final ActionAttributes attributes;
    private final String mainClass;
    private final String javaOptsString;
    private final ImmutableList<String> javaOpts;


    JavaAction(final ConstructionData constructionData,
               final ActionAttributes attributes,
               final String mainClass,
               final String javaOptsString,
               final ImmutableList<String> javaOpts) {
        super(constructionData);

        this.attributes = attributes;
        this.mainClass = mainClass;
        this.javaOptsString = javaOptsString;
        this.javaOpts = javaOpts;
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

    public String getMainClass() {
        return mainClass;
    }

    public String getJavaOptsString() {
        return javaOptsString;
    }

    public ImmutableList<String> getJavaOpts() {
        return javaOpts;
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

    public boolean isCaptureOutput() {
        return attributes.isCaptureOutput();
    }

    ActionAttributes getAttributes() {
        return attributes;
    }
}