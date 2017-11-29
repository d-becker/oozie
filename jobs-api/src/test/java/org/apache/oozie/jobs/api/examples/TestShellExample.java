/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.jobs.api.examples;

import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.jobs.api.GraphVisualization;
import org.apache.oozie.jobs.api.action.Prepare;
import org.apache.oozie.jobs.api.action.PrepareBuilder;
import org.apache.oozie.jobs.api.action.ShellAction;
import org.apache.oozie.jobs.api.action.ShellActionBuilder;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.serialization.Serializer;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.apache.oozie.test.WorkflowTestCase;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class TestShellExample extends WorkflowTestCase {
    public void testShellExample() throws IOException, JAXBException, OozieClientException {
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
                .build();

        final ShellAction sadPath = ShellActionBuilder.createFromExistingAction(parent)
                .withName("sad-path")
                .withParentDefaultConditional(parent)
                .withArgument("Sad path")
                .build();

        final Workflow workflow = new WorkflowBuilder()
                .withName("shell-example")
                .withDagContainingNode(parent).build();

        final String xml = Serializer.serialize(workflow);

        System.out.println(xml);

        GraphVisualization.workflowToPng(workflow, "shell-workflow.png");

        final Graph intermediateGraph = new Graph(workflow);

        GraphVisualization.graphToPng(intermediateGraph, "shell-graph.png");

        log.debug("Workflow XML is:\n{0}", xml);

        validate(xml);
    }
}
