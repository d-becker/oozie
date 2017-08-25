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
import org.apache.oozie.jobs.api.action.EmailActionBuilder;
import org.apache.oozie.jobs.api.action.Node;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.serialization.Serializer;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class SimpleEmailExample {
    public static void main(String[] args) throws JAXBException, IOException {
        final Node a = EmailActionBuilder.create()
                .withName("A")
                .withRecipient("somebody@apache.org")
                .withSubject("Subject")
                .withBody("This is a wonderful e-mail.")
                .build();

        final Node b = EmailActionBuilder.create()
                .withName("B").withParent(a)
                .withRecipient("somebody.else@apache.org")
                .withSubject("Re: Subject")
                .withBody("This is an even more wonderful e-mail.")
                .build();
        final Node c = EmailActionBuilder.create()
                .withName("C").withParent(a)
                .withRecipient("somebody@apache.org")
                .withSubject("Re: Subject")
                .withBody("No, this is the most wonderful e-mail.")
                .build();

        final Workflow workflow = new WorkflowBuilder()
                .withName("simple-email-example")
                .withDagContainingNode(a).build();

        GraphVisualization.workflowToPng(workflow, "simple-email-example-workflow.png");

        final Graph intermediateGraph = new Graph(workflow);

        GraphVisualization.graphToPng(intermediateGraph, "simple-email-example-graph.png");

        final String xml = Serializer.serialize(workflow);

        System.out.println(xml);
    }
}
