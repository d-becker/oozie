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

public class Visualization {
    public static String intermediaryGraphToDot(final Graph graph) {
        return intermediaryNodesToDot(graph.getNodes());
    }

    public static String intermediaryNodesToDot(Collection<NodeBase> nodes) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        for (NodeBase node : nodes) {
            List<NodeBase> children = node.getChildren();

            for (NodeBase child : children) {
                String s = String.format("\t%s -> %s\n", node.getName(), child.getName());
                builder.append(s);
            }
        }

        builder.append("}");

        return builder.toString();
    }

    public static String workflowToDot(final Workflow workflow) {
        return nodesToDot(workflow.getNodes());
    }

    public static String nodesToDot(final Collection<Node> nodes) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        for (Node node : nodes) {
            List<Node> children = node.getChildren();

            for (Node child : children) {
                String s = String.format("\t%s -> %s\n", node.getName(), child.getName());
                builder.append(s);
            }
        }

        builder.append("}");

        return builder.toString();
    }

    public static void main(String[] args) throws IOException {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(w);

        MutableGraph g = Parser.read(intermediaryGraphToDot(graph));
        Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("EXAMPLE_GRAPH.png"));
    }

}
