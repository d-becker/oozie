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

package org.apache.oozie.jobs.api;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.apache.oozie.jobs.api.action.MapReduceActionBuilder;
import org.apache.oozie.jobs.api.action.Node;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.oozie.dag.NodeBase;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class GraphVisualization {
    private static final String PARENT_FOLDER_NAME = "target/graphviz";
    private static final int PNG_WIDTH = 1024;

    public static String graphToDot(final Graph graph) {
        return nodeBasesToDot(graph.getNodes());
    }

    private static String nodeBasesToDot(final Collection<NodeBase> nodes) {
        final StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        for (final NodeBase node : nodes) {
            final List<NodeBase> children = node.getChildren();

            for (final NodeBase child : children) {
                final String s = String.format("\t%s -> %s%n", node.getName(), child.getName());
                builder.append(s);
            }
        }

        builder.append("}");

        return builder.toString();
    }

    private static String workflowToDot(final Workflow workflow) {
        return nodesToDot(workflow.getNodes());
    }

    private static String nodesToDot(final Collection<Node> nodes) {
        final StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        for (final Node node : nodes) {
            final List<Node> children = node.getAllChildren();

            for (final Node child : children) {
                builder.append(String.format("\t%s -> %s%n", node.getName(), child.getName()));
            }
        }

        builder.append("}");

        return builder.toString();
    }

    static void graphToPng(final Graph graph, final String fileName) throws IOException {
        final MutableGraph mg = Parser.read(graphToDot(graph));
        mg.setLabel(fileName);
        Graphviz.fromGraph(mg).width(PNG_WIDTH).render(Format.PNG).toFile(new File(PARENT_FOLDER_NAME, fileName));
    }

    static void workflowToPng(final Workflow workflow, final String fileName) throws IOException {
        final MutableGraph mg = Parser.read(workflowToDot(workflow));
        mg.setLabel(fileName);
        Graphviz.fromGraph(mg).width(PNG_WIDTH).render(Format.PNG).toFile(new File(PARENT_FOLDER_NAME, fileName));
    }

    public static void main(final String[] args) throws IOException {
        final Node a = new MapReduceActionBuilder().withName("A").build();

        new MapReduceActionBuilder().withName("B").withParent(a).build();
        new MapReduceActionBuilder().withName("C").withParent(a).build();

        final Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        final Graph graph = new Graph(w);

        final MutableGraph g = Parser.read(graphToDot(graph));
        Graphviz.fromGraph(g).width(PNG_WIDTH).render(Format.PNG).toFile(new File(PARENT_FOLDER_NAME, "EXAMPLE_GRAPH.png"));
    }
}
