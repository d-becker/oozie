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

package org.apache.oozie.jobs.api.intermediary;

import org.apache.oozie.jobs.api.MapReduceActionBuilder;
import org.apache.oozie.jobs.api.Node;
import org.apache.oozie.jobs.api.Workflow;
import org.apache.oozie.jobs.api.WorkflowBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestIntermediaryGraph {
    @Test
    public void testWorkflowWithoutJoin() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        IntermediaryGraph graph = new IntermediaryGraph(w);

        checkDependencies(w.getNodes(), graph);
    }

    @Test
    public void testWorkflowWithTrivialJoin() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();
        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        IntermediaryGraph graph = new IntermediaryGraph(w);

        System.out.println(graph.toDot());

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
        IntermediaryGraph graph = new IntermediaryGraph(w);

        checkDependencies(w.getNodes(), graph);

        IntermediaryNode A = new NormalIntermediaryNode("A", null);
        IntermediaryNode B = new NormalIntermediaryNode("B", null);
        IntermediaryNode C = new NormalIntermediaryNode("C", null);
        IntermediaryNode D = new NormalIntermediaryNode("D", null);
        IntermediaryNode E = new NormalIntermediaryNode("E", null);
        IntermediaryNode F = new NormalIntermediaryNode("F", null);

        StartIntermediaryNode start = new StartIntermediaryNode("start");
        EndIntermediaryNode end = new EndIntermediaryNode("end");
        ForkIntermediaryNode fork1 = new ForkIntermediaryNode("fork1");
        ForkIntermediaryNode fork2 = new ForkIntermediaryNode("fork2");
        JoinIntermediaryNode join1 = new JoinIntermediaryNode("join1", fork1);
        JoinIntermediaryNode join2 = new JoinIntermediaryNode("join2", fork2);

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

        List<IntermediaryNode> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D, E, F);

        checkEqualStructureByNames(nodes, graph);

        System.out.println(graph.toDot());
    }

    @Test
    public void testCrossingDependencyLines() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).withParent(b).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(a).withParent(b).build();

        Workflow workflow = new WorkflowBuilder().withDagContainingNode(a).build();
        IntermediaryGraph graph = new IntermediaryGraph(workflow);

        checkDependencies(workflow.getNodes(), graph);

        IntermediaryNode A = new NormalIntermediaryNode("A", null);
        IntermediaryNode B = new NormalIntermediaryNode("B", null);
        IntermediaryNode C = new NormalIntermediaryNode("C", null);
        IntermediaryNode D = new NormalIntermediaryNode("D", null);

        StartIntermediaryNode start = new StartIntermediaryNode("start");
        EndIntermediaryNode end = new EndIntermediaryNode("end");
        ForkIntermediaryNode fork1 = new ForkIntermediaryNode("fork1");
        ForkIntermediaryNode fork2 = new ForkIntermediaryNode("fork2");
        JoinIntermediaryNode join1 = new JoinIntermediaryNode("join1", fork1);
        JoinIntermediaryNode join2 = new JoinIntermediaryNode("join2", fork2);

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

        List<IntermediaryNode> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D);
        checkEqualStructureByNames(nodes, graph);

        System.out.println(graph.toDot());
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
        IntermediaryGraph graph = new IntermediaryGraph(workflow);

        checkDependencies(workflow.getNodes(), graph);

        IntermediaryNode A = new NormalIntermediaryNode("A", null);
        IntermediaryNode B = new NormalIntermediaryNode("B", null);
        IntermediaryNode C = new NormalIntermediaryNode("C", null);
        IntermediaryNode D = new NormalIntermediaryNode("D", null);
        IntermediaryNode E = new NormalIntermediaryNode("E", null);
        IntermediaryNode F = new NormalIntermediaryNode("F", null);

        StartIntermediaryNode start = new StartIntermediaryNode("start");
        EndIntermediaryNode end = new EndIntermediaryNode("end");
        ForkIntermediaryNode fork1 = new ForkIntermediaryNode("fork1");
        ForkIntermediaryNode fork2 = new ForkIntermediaryNode("fork2");
        JoinIntermediaryNode join1 = new JoinIntermediaryNode("join1", fork1);
        JoinIntermediaryNode join2 = new JoinIntermediaryNode("join2", fork2);

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

        List<IntermediaryNode> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D, E, F);

        System.out.println(graph.toDot());

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
        IntermediaryGraph graph = new IntermediaryGraph(workflow);

        checkDependencies(workflow.getNodes(), graph);

        IntermediaryNode A = new NormalIntermediaryNode("A", null);
        IntermediaryNode B = new NormalIntermediaryNode("B", null);
        IntermediaryNode C = new NormalIntermediaryNode("C", null);
        IntermediaryNode D = new NormalIntermediaryNode("D", null);
        IntermediaryNode E = new NormalIntermediaryNode("E", null);
        IntermediaryNode F = new NormalIntermediaryNode("F", null);

        StartIntermediaryNode start = new StartIntermediaryNode("start");
        EndIntermediaryNode end = new EndIntermediaryNode("end");
        ForkIntermediaryNode fork1 = new ForkIntermediaryNode("fork1");
        ForkIntermediaryNode fork2 = new ForkIntermediaryNode("fork2");
        JoinIntermediaryNode join1 = new JoinIntermediaryNode("join1", fork1);
        JoinIntermediaryNode join2 = new JoinIntermediaryNode("join2", fork2);

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

        List<IntermediaryNode> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D, E, F);

        System.out.println(graph.toDot());

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
        IntermediaryGraph graph = new IntermediaryGraph(workflow);

        checkDependencies(workflow.getNodes(), graph);
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
        IntermediaryGraph graph = new IntermediaryGraph(w);

        checkDependencies(w.getNodes(), graph);

        IntermediaryNode A = new NormalIntermediaryNode("A", null);
        IntermediaryNode B = new NormalIntermediaryNode("B", null);
        IntermediaryNode C = new NormalIntermediaryNode("C", null);
        IntermediaryNode D = new NormalIntermediaryNode("D", null);
        IntermediaryNode E = new NormalIntermediaryNode("E", null);
        IntermediaryNode F = new NormalIntermediaryNode("F", null);

        StartIntermediaryNode start = new StartIntermediaryNode("start");
        EndIntermediaryNode end = new EndIntermediaryNode("end");
        ForkIntermediaryNode fork1 = new ForkIntermediaryNode("fork1");
        ForkIntermediaryNode fork2 = new ForkIntermediaryNode("fork2");
        JoinIntermediaryNode join1 = new JoinIntermediaryNode("join1", fork1);
        JoinIntermediaryNode join2 = new JoinIntermediaryNode("join2", fork2);


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

        List<IntermediaryNode> nodes = Arrays.asList(start, end, fork1, fork2, join1, join2, A, B, C, D, E, F);

        checkEqualStructureByNames(nodes, graph);
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
//        IntermediaryGraph graph = new IntermediaryGraph(w);
//
//        IntermediaryNode start = graph.getStart();
//
//        assertEquals(start.getChildren().size(), 2);
//
//        // The order of the roots is not deterministic, they are stored in a set.
//        IntermediaryNode IA = start.getChildren().get(0).getName().equals("A") ?
//                start.getChildren().get(0) : start.getChildren().get(1);
//        IntermediaryNode IG = start.getChildren().get(0).getName().equals("G") ?
//                start.getChildren().get(0) : start.getChildren().get(1);;
//
//        checkNode(IA, "A", Arrays.asList(start), 2);
//
//        checkNode(IG, "G", Arrays.asList(start), 1);
//
//        IntermediaryNode IB = IA.getChildren().get(0);
//        assertEquals("B", IB.getName());
//        assertEquals(new HashSet<>(Arrays.asList(IA, IG)), new HashSet<>(IB.getParents())); // The order is not deterministic.
//        assertEquals(1, IB.getChildren().size());
//
//        IntermediaryNode IC = IA.getChildren().get(1);
//        checkNode(IC, "C", Arrays.asList(IA), 2);
//
//        IntermediaryNode ID = IB.getChildren().get(0);
//        checkNode(ID, "D", Arrays.asList(IB, IC), 1);
//
//        IntermediaryNode IE = IC.getChildren().get(1);
//        checkNode(IE, "E", Arrays.asList(IC), 1);
//
//        IntermediaryNode IF = ID.getChildren().get(0);
//        checkNode(IF, "F", Arrays.asList(ID, IE), 1);
//
//        IntermediaryNode end = graph.getEnd();
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
//        IntermediaryGraph graph = new IntermediaryGraph(w);
//
//        graph.convertToForkJoinFriendly();
//
//        IntermediaryNode start = graph.getStart();
//
//        assertEquals(1, start.getChildren().size());
//
//        IntermediaryNode IA = start.getChildren().get(0);
//        checkNode(IA, "A", Arrays.asList(start), 2);
//
//        IntermediaryNode IB = IA.getChildren().get(0);
//        checkNode(IB, "B", Arrays.asList(IA), 1);
//
//        IntermediaryNode IC = IA.getChildren().get(1);
//        checkNode(IC, "C", Arrays.asList(IA), 1);
//
//        IntermediaryNode ID = IB.getChildren().get(0);
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
//        IntermediaryGraph graph = new IntermediaryGraph(w);
//
//        IntermediaryNode start = graph.getStart();
//
//        assertEquals(1, start.getChildren().size());
//
//        IntermediaryNode IA = start.getChildren().get(0);
//        checkNode(IA, "A", Arrays.asList(start), 2);
//
//        IntermediaryNode IB = IA.getChildren().get(0);
//        checkNode(IB, "B", Arrays.asList(IA), 1);
//
//        IntermediaryNode IC = IA.getChildren().get(1);
//        checkNode(IC, "C", Arrays.asList(IA), 1);
//
//        IntermediaryNode dummyNode = IB.getChildren().get(0);
//        checkNode(dummyNode, null, Arrays.asList(IB, IC), 2);
//
//        IntermediaryNode ID = dummyNode.getChildren().get(0);
//        checkNode(ID, "D", Arrays.asList(dummyNode), 1);
//
//        IntermediaryNode IE = dummyNode.getChildren().get(1);
//        checkNode(IE, "E", Arrays.asList(dummyNode), 1);
//
//        IntermediaryNode IF = ID.getChildren().get(0);
//        checkNode(IF, "F", Arrays.asList(ID, IE), 1);
//
//        IntermediaryNode end = graph.getEnd();
//        assertEquals("end", end.getName());
//
//        fail();
//    }
//
//    private void checkNode(final IntermediaryNode node, final String name,
//                           final List<IntermediaryNode> parents, final int numberOfChildren) {
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

    private void checkEqualStructureByNames(final Collection<IntermediaryNode> expectedNodes, final IntermediaryGraph graph2) {
        if (expectedNodes.size() != graph2.getNodes().size()) {
            fail();
        }

        for (IntermediaryNode expectedNode : expectedNodes) {
            IntermediaryNode nodeInOtherGraph = graph2.getNodeByName(expectedNode.getName());

            if (nodeInOtherGraph == null) {
                fail();
            }

            List<IntermediaryNode> expectedChildren = expectedNode.getChildren();
            List<IntermediaryNode> actualChildren = nodeInOtherGraph.getChildren();

            List<String> expectedChildrenNames = new ArrayList<>();
            for (IntermediaryNode child : expectedChildren) {
                expectedChildrenNames.add(child.getName());
            }

            List<String> actualChildrenNames = new ArrayList<>();
            for (IntermediaryNode child : actualChildren) {
                actualChildrenNames.add(child.getName());
            }

            if (expectedNode instanceof ForkIntermediaryNode) {
                // The order of the children of fork nodes is not important.
                Collections.sort(expectedChildrenNames);
                Collections.sort(actualChildrenNames);
            }

            assertEquals(expectedChildrenNames.size(), actualChildrenNames.size());

            for (int i = 0; i < expectedChildren.size(); ++i) {
                String expectedName = expectedChildrenNames.get(i);
                String actualName = actualChildrenNames.get(i);

                if (graph2.getNodeByName(actualName) instanceof NormalIntermediaryNode) {
                    assertEquals(expectedName, actualName);
                }
            }
        }
    }

    private void checkDependencies(final Set<Node> originalNodes, final IntermediaryGraph graph) {
        for (Node originalNode : originalNodes) {
            for (Node originalParent : originalNode.getParents()) {
                IntermediaryNode node = graph.getNodeByName(originalNode.getName());
                IntermediaryNode parent = graph.getNodeByName(originalParent.getName());

                assertTrue(verifyDependency(parent, node));
            }
        }
    }

    private boolean verifyDependency(final IntermediaryNode dependency, final IntermediaryNode dependent) {
        List<IntermediaryNode> children = dependency.getChildren();

        for (IntermediaryNode child : children) {
            if (child == dependent || verifyDependency(child, dependent)) {
                return true;
            }
        }

        return false;
    }
}
