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
import org.apache.oozie.client.WorkflowJob;
import org.apache.oozie.jobs.api.GraphVisualization;
import org.apache.oozie.jobs.api.action.Delete;
import org.apache.oozie.jobs.api.action.FSAction;
import org.apache.oozie.jobs.api.action.FSActionBuilder;
import org.apache.oozie.jobs.api.action.Mkdir;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.serialization.Serializer;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.apache.oozie.test.TestWorkflow;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Date;

public class TestFSAction extends TestWorkflow {

    public void testTwoFSActions() throws JAXBException, IOException, OozieClientException {
        final String hdfsPath = getFsTestCaseDir() + "/user/${wf:user()}/examples/output_" + new Date().getTime();

        final Delete delete = new Delete(hdfsPath, true);

        final Mkdir mkdir = new Mkdir(hdfsPath);

        final FSAction parent = FSActionBuilder.create()
                .withName("parent")
                .withNameNode(getNameNodeUri())
                .withDelete(delete)
                .withMkdir(mkdir)
                .build();

        final FSAction child = FSActionBuilder.createFromExistingAction(parent)
                .withName("child")
                .withParent(parent)
                .build();

        final Workflow workflow = new WorkflowBuilder()
                .withName("simple-fs-example")
                .withDagContainingNode(parent).build();

        final String xml = Serializer.serialize(workflow);

        log.debug("Workflow XML is:\n{0}", xml);

        GraphVisualization.workflowToPng(workflow, "simple-fs-example-workflow.png");

        final Graph intermediateGraph = new Graph(workflow);

        GraphVisualization.graphToPng(intermediateGraph, "simple-fs-example-graph.png");

        submitAndAssert(xml, WorkflowJob.Status.SUCCEEDED);
    }
}