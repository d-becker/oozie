package org.apache.oozie.jobs.api.factory;

import org.apache.oozie.jobs.api.workflow.Workflow;

public interface WorkflowFactory {
    Workflow create();
}
