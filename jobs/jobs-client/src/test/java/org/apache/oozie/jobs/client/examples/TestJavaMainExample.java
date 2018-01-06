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

package org.apache.oozie.jobs.client.examples;

import junit.framework.TestCase;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.jobs.api.GraphVisualization;
import org.apache.oozie.jobs.api.action.JavaAction;
import org.apache.oozie.jobs.api.action.JavaActionBuilder;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.serialization.WorkflowMarshaller;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.apache.oozie.util.XLog;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class TestJavaMainExample extends TestCase {
    private static final XLog log = XLog.getLog(TestJavaMainExample.class);

    public void testJavaMain() throws IOException, JAXBException, OozieClientException {
        final JavaAction parent = JavaActionBuilder.create()
                .withName("java-main")
                .withJobTracker("${jobTracker}")
                .withNameNode("${nameNode}")
                .withConfigProperty("mapred.job.queue.name", "${queueName}")
                .withMainClass("org.apache.oozie.example.DemoJavaMain")
                .withArg("Hello")
                .withArg("Oozie!")
                .build();

        final Workflow workflow = new WorkflowBuilder()
                .withName("java-main-example")
                .withDagContainingNode(parent).build();

        final String xml = WorkflowMarshaller.unmarshal(workflow);

        GraphVisualization.workflowToPng(workflow, "java-main-workflow.png");

        final Graph intermediateGraph = new Graph(workflow);

        GraphVisualization.graphToPng(intermediateGraph, "java-main-graph.png");

        log.info("Workflow XML is:\n{0}", xml);
    }
}
