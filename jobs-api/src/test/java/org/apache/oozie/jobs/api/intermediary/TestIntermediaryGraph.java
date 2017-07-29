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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestIntermediaryGraph {
//    @Test
//    public void testWorkflowToIntermediaryGraphWithSingleRoot() {
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
//        assertEquals("end", end.getName());
//    }
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
}
