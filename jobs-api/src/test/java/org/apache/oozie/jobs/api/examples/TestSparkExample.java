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

import junit.framework.TestCase;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.jobs.api.GraphVisualization;
import org.apache.oozie.jobs.api.action.Prepare;
import org.apache.oozie.jobs.api.action.PrepareBuilder;
import org.apache.oozie.jobs.api.action.SparkAction;
import org.apache.oozie.jobs.api.action.SparkActionBuilder;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.serialization.Serializer;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.apache.oozie.test.WorkflowTestCase;
import org.apache.oozie.util.XLog;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class TestSparkExample extends TestCase {
    private static final XLog log = XLog.getLog(TestSparkExample.class);

    public void testForkedSparkActions() throws IOException, JAXBException, OozieClientException {
        final Prepare prepare = new PrepareBuilder()
                .withDelete("${nameNode}/user/${wf:user()}/${examplesRoot}/output-data/spark")
                .build();

        final SparkAction parent = SparkActionBuilder.create()
                .withName("spark-file-copy")
                .withJobTracker("${jobTracker}")
                .withNameNode("${nameNode}")
                .withPrepare(prepare)
                .withConfigProperty("mapred.job.queue.name", "${queueName}")
                .withArg("${nameNode}/user/${wf:user()}/${examplesRoot}/input-data/text/data.txt")
                .withArg("${nameNode}/user/${wf:user()}/${examplesRoot}/output-data/spark")
                .withMaster("${master}")
                .withActionName("Spark File Copy Example")
                .withActionClass("org.apache.oozie.example.SparkFileCopy")
                .withJar("${nameNode}/user/${wf:user()}/${examplesRoot}/apps/spark/lib/oozie-examples.jar")
                .build();

        final Workflow workflow = new WorkflowBuilder()
                .withName("spark-file-copy")
                .withDagContainingNode(parent).build();

        final String xml = Serializer.serialize(workflow);

        GraphVisualization.workflowToPng(workflow, "spark-file-copy-workflow.png");

        final Graph intermediateGraph = new Graph(workflow);

        GraphVisualization.graphToPng(intermediateGraph, "spark-file-copy-graph.png");

        log.info("Workflow XML is:\n{0}", xml);
    }
}
