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

import static org.junit.Assert.assertEquals;

public class TestIntermediaryGraph {
    @Test
    public void testWorkflowToIntermediaryGraphWithSingleRoot() {
        Node a = new MapReduceActionBuilder().withName("A").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();
        Node e = new MapReduceActionBuilder().withName("E").withParent(c).build();

        Node f = new MapReduceActionBuilder().withName("F").withParent(d).withParent(e).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        IntermediaryGraph graph = new IntermediaryGraph(w);

        IntermediaryNode start = graph.getStart();

        assertEquals(1, start.getChildren().size());

        IntermediaryNode IA = start.getChildren().get(0);
        assertEquals("A", IA.getName());
        assertEquals(Arrays.asList(start), IA.getParents());
        assertEquals(2, IA.getChildren().size());

        IntermediaryNode IB = IA.getChildren().get(0);
        assertEquals("B", IB.getName());
        assertEquals(Arrays.asList(IA), IB.getParents());
        assertEquals(1, IB.getChildren().size());

        IntermediaryNode IC = IA.getChildren().get(1);
        assertEquals("C", IC.getName());
        assertEquals(Arrays.asList(IA), IC.getParents());
        assertEquals(2, IC.getChildren().size());

        IntermediaryNode ID = IB.getChildren().get(0);
        assertEquals("D", ID.getName());
        assertEquals(Arrays.asList(IB, IC), ID.getParents());
        assertEquals(1, ID.getChildren().size());

        IntermediaryNode IE = IC.getChildren().get(1);
        assertEquals("E", IE.getName());
        assertEquals(Arrays.asList(IC), IE.getParents());
        assertEquals(1, IE.getChildren().size());

        IntermediaryNode IF = ID.getChildren().get(0);
        assertEquals("F", IF.getName());
        assertEquals(Arrays.asList(ID, IE), IF.getParents());
        assertEquals(1, IF.getChildren().size());

        IntermediaryNode end = graph.getEnd();
        assertEquals("end", end.getName());
        assertEquals(Arrays.asList(IF), end.getParents());
    }

    @Test
    public void testWorkflowToIntermediaryGraphWithMultipleRoots() {
        Node a = new MapReduceActionBuilder().withName("A").build();
        Node g = new MapReduceActionBuilder().withName("G").build();

        Node b = new MapReduceActionBuilder().withName("B").withParent(a).withParent(g).build();
        Node c = new MapReduceActionBuilder().withName("C").withParent(a).build();

        Node d = new MapReduceActionBuilder().withName("D").withParent(b).withParent(c).build();
        Node e = new MapReduceActionBuilder().withName("E").withParent(c).build();

        Node f = new MapReduceActionBuilder().withName("F").withParent(d).withParent(e).build();

        Workflow w = new WorkflowBuilder().withDagContainingNode(a).build();
        IntermediaryGraph graph = new IntermediaryGraph(w);

        IntermediaryNode start = graph.getStart();

        assertEquals(start.getChildren().size(), 2);

        // The order of the roots is not deterministic, they are stored in a set.
        IntermediaryNode IA = start.getChildren().get(0).getName().equals("A") ?
                start.getChildren().get(0) : start.getChildren().get(1);
        IntermediaryNode IG = start.getChildren().get(0).getName().equals("G") ?
                start.getChildren().get(0) : start.getChildren().get(1);;

        assertEquals("A", IA.getName());
        assertEquals(Arrays.asList(start), IA.getParents());
        assertEquals(2, IA.getChildren().size());

        assertEquals("G", IG.getName());
        assertEquals(Arrays.asList(start), IG.getParents());
        assertEquals(1, IG.getChildren().size());

        IntermediaryNode IB = IA.getChildren().get(0);
        assertEquals("B", IB.getName());
        assertEquals(new HashSet<>(Arrays.asList(IA, IG)), new HashSet<>(IB.getParents())); // The order is not deterministic.
        assertEquals(1, IB.getChildren().size());

        IntermediaryNode IC = IA.getChildren().get(1);
        assertEquals("C", IC.getName());
        assertEquals(Arrays.asList(IA), IC.getParents());
        assertEquals(2, IC.getChildren().size());

        IntermediaryNode ID = IB.getChildren().get(0);
        assertEquals("D", ID.getName());
        assertEquals(Arrays.asList(IB, IC), ID.getParents());
        assertEquals(1, ID.getChildren().size());

        IntermediaryNode IE = IC.getChildren().get(1);
        assertEquals("E", IE.getName());
        assertEquals(Arrays.asList(IC), IE.getParents());
        assertEquals(1, IE.getChildren().size());

        IntermediaryNode IF = ID.getChildren().get(0);
        assertEquals("F", IF.getName());
        assertEquals(Arrays.asList(ID, IE), IF.getParents());
        assertEquals(1, IF.getChildren().size());

        IntermediaryNode end = graph.getEnd();
        assertEquals("end", end.getName());
        assertEquals(Arrays.asList(IF), end.getParents());
    }
}
