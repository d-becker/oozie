package org.apache.oozie.jobs.api.workflow;

import org.apache.oozie.jobs.api.action.ActionAttributes;
import org.apache.oozie.jobs.api.action.HasAttributes;
import org.apache.oozie.jobs.api.action.Launcher;

import java.util.List;
import java.util.Map;

public class Global implements HasAttributes {
    private final ActionAttributes attributes;

    Global(final ActionAttributes attributes) {
        this.attributes = attributes;
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

    public Launcher getLauncher() {
        return attributes.getLauncher();
    }

    public List<String> getJobXmls() {
        return attributes.getJobXmls();
    }

    public String getConfigProperty(final String property) {
        return attributes.getConfiguration().get(property);
    }

    public Map<String, String> getConfiguration() {
        return attributes.getConfiguration();
    }

    @Override
    public ActionAttributes getAttributes() {
        return attributes;
    }
}
