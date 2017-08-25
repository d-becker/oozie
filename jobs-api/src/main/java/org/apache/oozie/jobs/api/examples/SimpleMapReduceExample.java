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

import org.apache.oozie.jobs.api.GraphVisualization;
import org.apache.oozie.jobs.api.action.MapReduceAction;
import org.apache.oozie.jobs.api.action.MapReduceActionBuilder;
import org.apache.oozie.jobs.api.action.Prepare;
import org.apache.oozie.jobs.api.action.PrepareBuilder;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.serialization.Serializer;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class SimpleMapReduceExample {
    public static void main(String[] args) throws IOException, JAXBException {
        final Prepare prepare = new PrepareBuilder()
                .withDelete("${nameNode}/user/${wf:user()}/${examplesRoot}/output")
                .build();

        final MapReduceAction mrAction1 = MapReduceActionBuilder.create()
                .withName("mr-action-1")
                .withJobTracker("${jobTracker}")
                .withNameNode("${nameNode}")
                .withPrepare(prepare)
                .withConfigProperty("mapred.job.queue.name", "${queueName}")
                .withConfigProperty("mapred.mapper.class", "org.apache.hadoop.mapred.lib.IdentityMapper")
                .withConfigProperty("mapred.input.dir", "/user/${wf:user()}/${examplesRoot}/input")
                .withConfigProperty("mapred.output.dir", "/user/${wf:user()}/${examplesRoot}/output")
                .build();

        //  We are reusing the definition of mrAction1 and only modifying and adding what is different.
        final MapReduceAction mrAction2 = MapReduceActionBuilder.createFromExistingAction(mrAction1)
                .withName("mr-action-2")
                .withParent(mrAction1)
                .build();

        final MapReduceAction mrAction3 = MapReduceActionBuilder.createFromExistingAction(mrAction2)
                .withName("mr-action-3")
                .build();

        final Workflow workflow = new WorkflowBuilder()
                .withName("simple-map-reduce-example")
                .withDagContainingNode(mrAction1).build();

        final String xml = Serializer.serialize(workflow);

        System.out.println(xml);

        GraphVisualization.workflowToPng(workflow, "simple-map-reduce-example-workflow.png");

        final Graph intermediateGraph = new Graph(workflow);

        GraphVisualization.graphToPng(intermediateGraph, "simple-map-reduce-example-graph.png");
    }
}
