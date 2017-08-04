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

package org.apache.oozie.jobs.api.oozie.dag;

import org.apache.oozie.jobs.api.action.MapReduceActionBuilder;
import org.apache.oozie.jobs.api.action.Node;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.apache.oozie.jobs.api.Visualization.intermediaryGraphToDot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestGraph {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testNameIsCorrect() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        final String name = "workflow-name";
        Workflow workflow = new WorkflowBuilder().withName(name).withDagContainingNode(a).build();

        Graph graph = new Graph(workflow);
        assertEquals(name, graph.getName());
    }

    @Test
    public void testDuplicateNamesThrow() {
        Node a = new MapReduceActionBuilder().withName("A").build();
        Node b = new MapReduceActionBuilder().withName("A").withParent(a).build();

        // The exception will be thrown by the Workflow object,
        // but if it breaks there, we want to catch duplicates here, too.
        expectedException.expect(IllegalArgumentException.class);
        Workflow workflow = new WorkflowBuilder().withDagContainingNode(a).build();

        Graph graph = new Graph(workflow);
    }

    @Test
    public void testWorkflowWithoutJoin() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(w);

        checkDependencies(w.getNodes(), graph);
    }

    @Test
    public void testWorkflowWithTrivialJoin() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();
        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(w);

        System.out.println(intermediaryGraphToDot(graph));

        checkDependencies(w.getNodes(), graph);
    }

    @Test
    public void testWorkflowNewDependenciesNeeded() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();
        Node e = new MapReduceActionBuilder().withName("E").withParent(c).build();

        Node f = new MapReduceActionBuilder().withName("F").withParent(d).withParent(e).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(w);

        checkDependencies(w.getNodes(), graph);

        NodeBase A = new ExplicitNode("A", null);
        NodeBase B = new ExplicitNode("B", null);
        NodeBase C = new ExplicitNode("C", null);
        NodeBase D = new ExplicitNode("D", null);
        NodeBase E = new ExplicitNode("E", null);
        NodeBase F = new ExplicitNode("F", null);

        Start start = new Start("start");
        End end = new End("end");
        Fork fork1 = new Fork("fork1");
        Fork fork2 = new Fork("fork2");
        Join join1 = new Join("join1", fork1);
        Join join2 = new Join("join2", fork2);

        end.addParent(F);
        F.addParent(join2);
        join2.addParent(D);
        join2.addParent(E);
        D.addParent(fork2);
        E.addParent(fork2);
        fork2.addParent(join1);
        join1.addParent(B);
        join1.addParent(C);
        B.addParent(fork1);
        C.addParent(fork1);
        fork1.addParent(A);
        A.addParent(start);

        List<NodeBase> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D, E, F);

        checkEqualStructureByNames(nodes, graph);

        System.out.println(intermediaryGraphToDot(graph));
    }

    @Test
    public void testCrossingDependencyLines() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).withParent(b).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(a).withParent(b).build();

        Workflow workflow = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(workflow);

        checkDependencies(workflow.getNodes(), graph);

        NodeBase A = new ExplicitNode("A", null);
        NodeBase B = new ExplicitNode("B", null);
        NodeBase C = new ExplicitNode("C", null);
        NodeBase D = new ExplicitNode("D", null);

        Start start = new Start("start");
        End end = new End("end");
        Fork fork1 = new Fork("fork1");
        Fork fork2 = new Fork("fork2");
        Join join1 = new Join("join1", fork1);
        Join join2 = new Join("join2", fork2);

        end.addParent(join2);
        join2.addParent(C);
        join2.addParent(D);
        C.addParent(fork2);
        D.addParent(fork2);
        fork2.addParent(join1);
        join1.addParent(A);
        join1.addParent(B);
        A.addParent(fork1);
        B.addParent(fork1);
        fork1.addParent(start);

        List<NodeBase> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D);
        checkEqualStructureByNames(nodes, graph);

        System.out.println(intermediaryGraphToDot(graph));
    }

    @Test
    public void testSplittingJoins() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(b).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(b).build();
        Node e = new MapReduceActionBuilder().withName("E").withParent(a).build();

        Node f = new MapReduceActionBuilder().withName("F").withParent(c).withParent(d).withParent(e).build();

        Workflow workflow = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(workflow);

        checkDependencies(workflow.getNodes(), graph);

        NodeBase A = new ExplicitNode("A", null);
        NodeBase B = new ExplicitNode("B", null);
        NodeBase C = new ExplicitNode("C", null);
        NodeBase D = new ExplicitNode("D", null);
        NodeBase E = new ExplicitNode("E", null);
        NodeBase F = new ExplicitNode("F", null);

        Start start = new Start("start");
        End end = new End("end");
        Fork fork1 = new Fork("fork1");
        Fork fork2 = new Fork("fork2");
        Join join1 = new Join("join1", fork1);
        Join join2 = new Join("join2", fork2);

        end.addParent(F);
        F.addParent(join1);
        join1.addParent(join2);
        join1.addParent(E);
        join2.addParent(C);
        join2.addParent(D);
        C.addParent(fork2);
        D.addParent(fork2);
        fork2.addParent(B);
        B.addParent(fork1);
        E.addParent(fork1);
        fork1.addParent(A);
        A.addParent(start);

        List<NodeBase> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D, E, F);

        System.out.println(intermediaryGraphToDot(graph));

        checkEqualStructureByNames(nodes, graph);
    }

    @Test
    public void testSplittingForks() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(a).build();
        Node e = new MapReduceActionBuilder().withName("E").withParent(b).withParent(c).build();

        Node f = new MapReduceActionBuilder().withName("F").withParent(e).withParent(d).build();

        Workflow workflow = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(workflow);

        checkDependencies(workflow.getNodes(), graph);

        NodeBase A = new ExplicitNode("A", null);
        NodeBase B = new ExplicitNode("B", null);
        NodeBase C = new ExplicitNode("C", null);
        NodeBase D = new ExplicitNode("D", null);
        NodeBase E = new ExplicitNode("E", null);
        NodeBase F = new ExplicitNode("F", null);

        Start start = new Start("start");
        End end = new End("end");
        Fork fork1 = new Fork("fork1");
        Fork fork2 = new Fork("fork2");
        Join join1 = new Join("join1", fork1);
        Join join2 = new Join("join2", fork2);

        end.addParent(F);
        F.addParent(join1);
        join1.addParent(E);
        join1.addParent(D);
        E.addParent(join2);
        join2.addParent(B);
        join2.addParent(C);
        B.addParent(fork2);
        C.addParent(fork2);
        fork2.addParent(fork1);
        D.addParent(fork1);
        fork1.addParent(A);
        A.addParent(start);

        List<NodeBase> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D, E, F);

        System.out.println(intermediaryGraphToDot(graph));

        checkEqualStructureByNames(nodes, graph);
    }

    @Test
    public void testBranchingUncles() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(b).build();
        Node e = new MapReduceActionBuilder().withName("E").withParent(c).build();

        Node f = new MapReduceActionBuilder().withName("F").withParent(d).withParent(e).build();
        Node g = new MapReduceActionBuilder().withName("G").withParent(c).build();
        Node h = new MapReduceActionBuilder().withName("H").withParent(f).withParent(g).build();

        Workflow workflow = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(workflow);

        checkDependencies(workflow.getNodes(), graph);

        System.out.println(intermediaryGraphToDot(graph));

        NodeBase A = new ExplicitNode("A", null);
        NodeBase B = new ExplicitNode("B", null);
        NodeBase C = new ExplicitNode("C", null);
        NodeBase D = new ExplicitNode("D", null);
        NodeBase E = new ExplicitNode("E", null);
        NodeBase F = new ExplicitNode("F", null);
        NodeBase G = new ExplicitNode("G", null);
        NodeBase H = new ExplicitNode("H", null);

        Start start = new Start("start");
        End end = new End("end");
        Fork fork1 = new Fork("fork1");
        Fork fork2 = new Fork("fork3");
        Join join1 = new Join("join1", fork1);
        Join join2 = new Join("join3", fork2);


        end.addParent(H);
        H.addParent(join2);
        join2.addParent(F);
        join2.addParent(G);
        F.addParent(fork2);
        G.addParent(fork2);
        fork2.addParent(join1);
        join1.addParent(D);
        join1.addParent(E);
        D.addParent(B);
        E.addParent(C);
        B.addParent(fork1);
        C.addParent(fork1);
        fork1.addParent(A);
        A.addParent(start);


        List<NodeBase> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D, E, F, G, H);

        checkEqualStructureByNames(nodes, graph);
    }

    @Test
    public void testTrivialRedundantEdge() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).withParent(b).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(w);

        checkDependencies(w.getNodes(), graph);
    }

    @Test
    public void testRedundantEdge() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();
        Node e = new MapReduceActionBuilder().withName("E").withParent(c).build();

        Node f = new MapReduceActionBuilder().withName("F").withParent(d).withParent(e).withParent(a).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(w);

        checkDependencies(w.getNodes(), graph);
    }

    @Test
    public void testLateUncle() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(b).build();
        Node e = new MapReduceActionBuilder().withName("E").withParent(b).build();

        Node f = new MapReduceActionBuilder().withName("F").withParent(c).build();

        Node g = new MapReduceActionBuilder().withName("G").withParent(e).build();
        Node h = new MapReduceActionBuilder().withName("H").withParent(f).build();
        Node i = new MapReduceActionBuilder().withName("I").withParent(d).withParent(g).build();
        Node j = new MapReduceActionBuilder().withName("J").withParent(e).withParent(h).build();
        Node k = new MapReduceActionBuilder().withName("K").withParent(i).withParent(j).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        Graph graph = new Graph(w);

        System.out.println(intermediaryGraphToDot(graph));

        checkDependencies(w.getNodes(), graph);
    }


//
//    @Test
//    public void testWorkflowToIntermediaryGraphWithMultipleRoots() {
//        Node a = new MapReduceActionBuilder().withName("A").build();
//        Node g = new MapReduceActionBuilder().withName("G").build();
//
//        Node b = new MapReduceActionBuilder().withName("B").withParent(a).withParent(g).build();
//        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();
//
//        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();
//        Node e = new MapReduceActionBuilder().withName("E").withParent(c).build();
//
//        Node f = new MapReduceActionBuilder().withName("F").withParent(d).withParent(e).build();
//
//        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
//        Graph graph = new Graph(w);
//
//        NodeBase start = graph.getStart();
//
//        assertEquals(start.getChildren().size(), 2);
//
//        // The order of the roots is not deterministic, they are stored in a set.
//        NodeBase IA = start.getChildren().get(0).getName().equals("A") ?
//                start.getChildren().get(0) : start.getChildren().get(1);
//        NodeBase IG = start.getChildren().get(0).getName().equals("G") ?
//                start.getChildren().get(0) : start.getChildren().get(1);;
//
//        checkNode(IA, "A", Arrays.asList(start), 2);
//
//        checkNode(IG, "G", Arrays.asList(start), 1);
//
//        NodeBase IB = IA.getChildren().get(0);
//        assertEquals("B", IB.getName());
//        assertEquals(new HashSet<>(Arrays.asList(IA, IG)), new HashSet<>(IB.getParents())); // The order is not deterministic.
//        assertEquals(1, IB.getChildren().size());
//
//        NodeBase IC = IA.getChildren().get(1);
//        checkNode(IC, "C", Arrays.asList(IA), 2);
//
//        NodeBase ID = IB.getChildren().get(0);
//        checkNode(ID, "D", Arrays.asList(IB, IC), 1);
//
//        NodeBase IE = IC.getChildren().get(1);
//        checkNode(IE, "E", Arrays.asList(IC), 1);
//
//        NodeBase IF = ID.getChildren().get(0);
//        checkNode(IF, "F", Arrays.asList(ID, IE), 1);
//
//        NodeBase end = graph.getEnd();
//        checkNode(end, "end", Arrays.asList(IF), 0);
//    }
//
//    @Test
//    public void testConvertToForkAndJoinFriendlyWhenItIsAlreadyThat() {
//        Node a = new MapReduceActionBuilder().withName("A").build();
//
//        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
//        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();
//
//        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();
//
//        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
//        Graph graph = new Graph(w);
//
//        graph.convertToForkJoinFriendly();
//
//        NodeBase start = graph.getStart();
//
//        assertEquals(1, start.getChildren().size());
//
//        NodeBase IA = start.getChildren().get(0);
//        checkNode(IA, "A", Arrays.asList(start), 2);
//
//        NodeBase IB = IA.getChildren().get(0);
//        checkNode(IB, "B", Arrays.asList(IA), 1);
//
//        NodeBase IC = IA.getChildren().get(1);
//        checkNode(IC, "C", Arrays.asList(IA), 1);
//
//        NodeBase ID = IB.getChildren().get(0);
//        checkNode(ID, "D", Arrays.asList(IB, IC), 1);
//        assertEquals(Arrays.asList(graph.getEnd()), ID.getChildren());
//    }
//
//    @Test
//    public void testConvertToForkAndJoinFriendlyWhenItIsNotAlreadyThat() {
//        Node a = new MapReduceActionBuilder().withName("A").build();
//
//        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
//        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();
//
//        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();
//        Node e = new MapReduceActionBuilder().withName("E").withParent(c).build();
//
//        Node f = new MapReduceActionBuilder().withName("F").withParent(d).withParent(e).build();
//
//        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
//        Graph graph = new Graph(w);
//
//        NodeBase start = graph.getStart();
//
//        assertEquals(1, start.getChildren().size());
//
//        NodeBase IA = start.getChildren().get(0);
//        checkNode(IA, "A", Arrays.asList(start), 2);
//
//        NodeBase IB = IA.getChildren().get(0);
//        checkNode(IB, "B", Arrays.asList(IA), 1);
//
//        NodeBase IC = IA.getChildren().get(1);
//        checkNode(IC, "C", Arrays.asList(IA), 1);
//
//        NodeBase dummyNode = IB.getChildren().get(0);
//        checkNode(dummyNode, null, Arrays.asList(IB, IC), 2);
//
//        NodeBase ID = dummyNode.getChildren().get(0);
//        checkNode(ID, "D", Arrays.asList(dummyNode), 1);
//
//        NodeBase IE = dummyNode.getChildren().get(1);
//        checkNode(IE, "E", Arrays.asList(dummyNode), 1);
//
//        NodeBase IF = ID.getChildren().get(0);
//        checkNode(IF, "F", Arrays.asList(ID, IE), 1);
//
//        NodeBase end = graph.getEnd();
//        assertEquals("end", end.getName());
//
//        fail();
//    }
//
//    private void checkNode(final NodeBase node, final String name,
//                           final List<NodeBase> parents, final int numberOfChildren) {
//        if (name != null) {
//            assertEquals(name, node.getName());
//        }
//        if (parents != null) {
//            assertEquals(parents, node.getParents());
//        }
//
//        if (numberOfChildren >= 0) {
//            assertEquals(numberOfChildren, node.getChildren().size());
//        }
//    }

    private void checkEqualStructureByNames(final Collection<NodeBase> expectedNodes, final Graph graph2) {
        if (expectedNodes.size() != graph2.getNodes().size()) {
            fail();
        }

        for (NodeBase expectedNode : expectedNodes) {
            NodeBase nodeInOtherGraph = graph2.getNodeByName(expectedNode.getName());

            if (nodeInOtherGraph == null) {
                fail();
            }

            List<NodeBase> expectedChildren = expectedNode.getChildren();
            List<NodeBase> actualChildren = nodeInOtherGraph.getChildren();

            List<String> expectedChildrenNames = new ArrayList<>();
            for (NodeBase child : expectedChildren) {
                expectedChildrenNames.add(child.getName());
            }

            List<String> actualChildrenNames = new ArrayList<>();
            for (NodeBase child : actualChildren) {
                actualChildrenNames.add(child.getName());
            }

            if (expectedNode instanceof Fork) {
                // The order of the children of fork nodes is not important.
                Collections.sort(expectedChildrenNames);
                Collections.sort(actualChildrenNames);
            }

            assertEquals(expectedChildrenNames.size(), actualChildrenNames.size());

            for (int i = 0; i < expectedChildren.size(); ++i) {
                String expectedName = expectedChildrenNames.get(i);
                String actualName = actualChildrenNames.get(i);

                if (graph2.getNodeByName(actualName) instanceof ExplicitNode) {
                    assertEquals(expectedName, actualName);
                }
            }
        }
    }

    private void checkDependencies(final Set<Node> originalNodes, final Graph graph) {
        for (Node originalNode : originalNodes) {
            for (Node originalParent : originalNode.getParents()) {
                NodeBase node = graph.getNodeByName(originalNode.getName());
                NodeBase parent = graph.getNodeByName(originalParent.getName());

                assertTrue(verifyDependency(parent, node));
            }
        }
    }

    private boolean verifyDependency(final NodeBase dependency, final NodeBase dependent) {
        List<NodeBase> children = dependency.getChildren();

        for (NodeBase child : children) {
            if (child == dependent || verifyDependency(child, dependent)) {
                return true;
            }
        }

        return false;
    }
}
