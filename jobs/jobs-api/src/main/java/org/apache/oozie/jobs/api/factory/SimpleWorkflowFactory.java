package org.apache.oozie.jobs.api.factory;

import org.apache.oozie.jobs.api.action.ShellAction;
import org.apache.oozie.jobs.api.action.ShellActionBuilder;
import org.apache.oozie.jobs.api.factory.WorkflowFactory;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;

public class SimpleWorkflowFactory implements WorkflowFactory {

    @Override
    public Workflow create() {
        final ShellAction parent = ShellActionBuilder.create()
                .withName("parent")
                .withJobTracker("${jobTracker}")
                .withNameNode("${nameNode}")
                .withConfigProperty("mapred.job.queue.name", "${queueName}")
                .withArgument("my_output=Hello Oozie")
                .withExecutable("echo")
                .withCaptureOutput(true)
                .build();

        final ShellAction happyPath = ShellActionBuilder.createFromExistingAction(parent)
                .withName("happy-path")
                .withParentWithCondition(parent, "${wf:actionData('parent')['my_output'] eq 'Hello Oozie'}")
                .withoutArgument("my_output=Hello Oozie")
                .withArgument("Happy path")
                .withCaptureOutput(null)
                .build();

        final ShellAction sadPath = ShellActionBuilder.createFromExistingAction(parent)
                .withName("sad-path")
                .withParentDefaultConditional(parent)
                .withArgument("Sad path")
                .build();

        final Workflow workflow = new WorkflowBuilder()
                .withName("shell-example")
                .withDagContainingNode(parent).build();

        return workflow;
    }
}
