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

package org.apache.oozie.jobs.api.workflow;

import org.apache.oozie.jobs.api.action.MapReduceAction;
import org.apache.oozie.jobs.api.action.MapReduceActionBuilder;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class TestWorkflowBuilder {
    public static final String NAME = "workflow-name";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testAddName() {
        WorkflowBuilder builder = new WorkflowBuilder();
        builder.withName(NAME);

        Workflow workflow = builder.build();

        assertEquals(NAME, workflow.getName());
    }

    @Test
    public void testAddDagTrivial() {
        MapReduceAction mrAction1 = new MapReduceActionBuilder()
                .withName("mr1")
                .withNameNode("${nameNode}")
                .withJobTracker("${jobTracker}")
                .withConfigProperty("mapred.output.dir", "${outputDir}")
                .build();

        MapReduceAction mrAction2 = new MapReduceActionBuilder()
                .withName("mr2")
                .withNameNode("${nameNode}")
                .withJobTracker("${jobTracker}")
                .withConfigProperty("mapred.output.dir", "${outputDir}")
                .build();

        WorkflowBuilder builder = new WorkflowBuilder();

        builder.withDagContainingNode(mrAction1)
                .withDagContainingNode(mrAction2);

        Workflow workflow = builder.build();

        assertEquals(new HashSet<>(Arrays.asList(mrAction1, mrAction2)), workflow.getRoots());
    }

    @Test
    public void testAddDagFindRoots() {
        MapReduceAction mrAction1 = new MapReduceActionBuilder()
                .withName("mr1")
                .build();

        MapReduceAction mrAction2 = new MapReduceActionBuilder()
                .withName("mr2")
                .build();

        MapReduceAction mrAction3 = new MapReduceActionBuilder()
                .withName("mr3")
                .withParent(mrAction1)
                .withParent(mrAction2)
                .build();

        WorkflowBuilder builder = new WorkflowBuilder();

        builder.withDagContainingNode(mrAction3);

        Workflow workflow = builder.build();

        assertEquals(new HashSet<>(Arrays.asList(mrAction1, mrAction2)), workflow.getRoots());
        assertEquals(new HashSet<>(Arrays.asList(mrAction1, mrAction2, mrAction3)), workflow.getNodes());
    }

    @Test
    public void testAddDagThrowOnDuplicateNodeNames() {
        MapReduceAction mrAction = new MapReduceActionBuilder()
                .withName("mr-action")
                .build();

        MapReduceAction mrActionWithTheSameName = new MapReduceActionBuilder()
                .withName("mr-action")
                .build();

        WorkflowBuilder builder = new WorkflowBuilder();
        builder.withName(NAME)
                .withDagContainingNode(mrAction)
                .withDagContainingNode(mrActionWithTheSameName);

        expectedException.expect(IllegalArgumentException.class);
        builder.build();
    }
}
