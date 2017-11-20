package org.apache.oozie.jobs.api.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class DistcpAction extends Node {
    private final ActionAttributes attributes;

    DistcpAction(final ConstructionData constructionData,
                 final ActionAttributes attributes) {
        super(constructionData);

        this.attributes = attributes;
    }

    public String getJobTracker() {
        return attributes.getJobTracker();
    }

    public String getNameNode() {
        return attributes.getNameNode();
    }

    public Prepare getPrepare() {
        return attributes.getPrepare();
    }

    public String getConfigProperty(final String property) {
        return attributes.getConfiguration().get(property);
    }

    public ImmutableMap<String, String> getConfiguration() {
        return attributes.getConfiguration();
    }

    public String getJavaOpts() {
        return attributes.getJavaOpts();
    }

    public ImmutableList<String> getArgs() {
        return attributes.getArgs();
    }

    ActionAttributes getAttributes() {
        return attributes;
    }
}