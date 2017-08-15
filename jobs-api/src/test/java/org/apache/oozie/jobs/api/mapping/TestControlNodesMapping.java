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

package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.generated.workflow.CASE;
import org.apache.oozie.jobs.api.generated.workflow.DECISION;
import org.apache.oozie.jobs.api.generated.workflow.DEFAULT;
import org.apache.oozie.jobs.api.generated.workflow.END;
import org.apache.oozie.jobs.api.generated.workflow.FORK;
import org.apache.oozie.jobs.api.generated.workflow.FORKTRANSITION;
import org.apache.oozie.jobs.api.generated.workflow.JOIN;
import org.apache.oozie.jobs.api.generated.workflow.START;
import org.apache.oozie.jobs.api.generated.workflow.SWITCH;
import org.apache.oozie.jobs.api.oozie.dag.Decision;
import org.apache.oozie.jobs.api.oozie.dag.End;
import org.apache.oozie.jobs.api.oozie.dag.ExplicitNode;
import org.apache.oozie.jobs.api.oozie.dag.Fork;
import org.apache.oozie.jobs.api.oozie.dag.Join;
import org.apache.oozie.jobs.api.oozie.dag.NodeBase;
import org.apache.oozie.jobs.api.oozie.dag.Start;
import org.dozer.DozerBeanMapper;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestControlNodesMapping {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final DozerBeanMapper mapper = new DozerBeanMapper();

    @BeforeClass
    public static void setUpMapper() {
        List<String> mappingFiles = new ArrayList<>();
        mappingFiles.add("dozer_config.xml");
        mappingFiles.add("mappingGraphToWORKFLOWAPP.xml");
        mappingFiles.add("action_mappings.xml");

        mapper.setMappingFiles(mappingFiles);
    }

    @Test
    public void testMappingStart() {
        final String childName = "child";
        final Start start = new Start("start");
        final NodeBase child = new ExplicitNode(childName, null);

        child.addParent(start);

        final START mappedStart = mapper.map(start, START.class);

        assertEquals(childName, mappedStart.getTo());
    }

    @Test
    public void testMappingEnd() {
        final String name = "end";
        final End end = new End(name);

        final END mappedEnd = mapper.map(end, END.class);

        assertEquals(name, mappedEnd.getName());
    }

    @Test
    public void testMappingFork() {
        final String name = "fork";
        final Fork fork = new Fork(name);

        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);

        child1.addParent(fork);
        child2.addParent(fork);

        final FORK mappedFork = mapper.map(fork, FORK.class);

        assertEquals(name, mappedFork.getName());

        List<FORKTRANSITION> transitions = mappedFork.getPath();
        assertEquals(child1.getName(), transitions.get(0).getStart());
        assertEquals(child2.getName(), transitions.get(1).getStart());
    }

    @Test
    public void testMappingJoin() {
        final String joinName = "join";
        final String childName = "child";
        final Join join = new Join(joinName, new Fork("fork"));

        final NodeBase child = new ExplicitNode(childName, null);

        child.addParent(join);

        final JOIN mappedJoin = mapper.map(join, JOIN.class);

        assertEquals(joinName, mappedJoin.getName());
        assertEquals(childName, mappedJoin.getTo());
    }

    @Test
    public void testMappingDecision() {
        final String name = "decision";
        final Decision decision = new Decision(name);

        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);
        final NodeBase defaultChild = new ExplicitNode("defaultChild", null);

        final String condition1 = "condition1";
        final String condition2 = "condition2";

        child1.addParentWithCondition(decision, condition1);
        child2.addParentWithCondition(decision, condition2);
        defaultChild.addParentDefaultConditional(decision);

        final DECISION mappedDecision = mapper.map(decision, DECISION.class);

        assertEquals(name, mappedDecision.getName());

        final SWITCH decisionSwitch = mappedDecision.getSwitch();
        final List<CASE> cases = decisionSwitch.getCase();

        assertEquals(2, cases.size());

        assertEquals(child1.getName(), cases.get(0).getTo());
        assertEquals(condition1, cases.get(0).getValue());

        assertEquals(child2.getName(), cases.get(1).getTo());
        assertEquals(condition2, cases.get(1).getValue());

        final DEFAULT decisionDefault = decisionSwitch.getDefault();
        assertEquals(defaultChild.getName(), decisionDefault.getTo());
    }

    @Test
    public void testMappingDecisionWithoutDefaultThrows() {
        final String name = "decision";
        final Decision decision = new Decision(name);

        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);

        final String condition1 = "condition1";
        final String condition2 = "condition2";

        child1.addParentWithCondition(decision, condition1);
        child2.addParentWithCondition(decision, condition2);

        expectedException.expect(IllegalStateException.class);
        final DECISION mappedDecision = mapper.map(decision, DECISION.class);
    }
}
