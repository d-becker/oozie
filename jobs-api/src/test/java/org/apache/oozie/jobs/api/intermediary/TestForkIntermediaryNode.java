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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestForkIntermediaryNode extends TestIntermediaryNode<ForkIntermediaryNode> {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Override
    protected ForkIntermediaryNode getInstance(final String name) {
        return new ForkIntermediaryNode(name);
    }

    @Test
    public void testAddParentWhenNoneAlreadyExists() {
        final StartIntermediaryNode parent = new StartIntermediaryNode("parent");
        final ForkIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent);
        assertEquals(parent, instance.getParent());
        assertEquals(instance, parent.getChild());
    }

    @Test
    public void testAddParentWhenItAlreadyExistsThrows() {
        final IntermediaryNode parent1 = getInstance("parent1");
        final IntermediaryNode parent2 = getInstance("parent2");

        final ForkIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent1);

        expectedException.expect(IllegalStateException.class);
        instance.addParent(parent2);
    }

    @Test
    public void testRemoveExistingParent() {
        final StartIntermediaryNode parent = new StartIntermediaryNode("parent");
        final ForkIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent);

        instance.removeParent(parent);
        assertEquals(null, instance.getParent());
        assertEquals(null, parent.getChild());
    }

    @Test
    public void testRemoveNonexistentParentThrows() {
        final StartIntermediaryNode parent = new StartIntermediaryNode("parent");
        final ForkIntermediaryNode instance = getInstance("instance");

        expectedException.expect(IllegalArgumentException.class);
        instance.removeParent(parent);
    }

    @Test
    public void testClearExistingParent() {
        final StartIntermediaryNode parent = new StartIntermediaryNode("parent");
        final ForkIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent);

        instance.clearParents();
        assertEquals(null, instance.getParent());
        assertEquals(null, parent.getChild());
    }

    @Test
    public void testClearNonExistentParent() {
        final StartIntermediaryNode parent = new StartIntermediaryNode("parent");
        final ForkIntermediaryNode instance = getInstance("instance");

        instance.clearParents();
        assertEquals(null, instance.getParent());
        assertEquals(null, parent.getChild());
    }

    @Test
    public void testForkAddedAsParentWhenItHasNoChild() {
        final ForkIntermediaryNode instance = getInstance("instance");
        final IntermediaryNode child = getInstance("child");

        child.addParent(instance);

        assertEquals(Arrays.asList(child), instance.getChildren());
    }

    @Test
    public void testForkAddedAsParentWhenItAlreadyHasAChild() {
        final ForkIntermediaryNode instance = getInstance("instance");
        final IntermediaryNode child1 = new NormalIntermediaryNode("child1", null);
        final IntermediaryNode child2 = new NormalIntermediaryNode("child2", null);

        child1.addParent(instance);
        child2.addParent(instance);

        assertEquals(Arrays.asList(child1, child2), instance.getChildren());
    }

    @Test
    public void testForkRemovedAsParent() {
        final ForkIntermediaryNode instance = getInstance("instance");
        final IntermediaryNode child1 = new NormalIntermediaryNode("child1", null);
        final IntermediaryNode child2 = new NormalIntermediaryNode("child2", null);
        final IntermediaryNode child3 = new NormalIntermediaryNode("child3", null);
        final IntermediaryNode child4 = new NormalIntermediaryNode("child4", null);
        final IntermediaryNode child5 = new NormalIntermediaryNode("child5", null);

        child1.addParent(instance);
        child2.addParent(instance);
        child3.addParent(instance);
        child4.addParent(instance);
        child5.addParent(instance);

        child5.removeParent(instance);

        assertEquals(Arrays.asList(child1, child2, child3, child4), instance.getChildren());
    }

    @Test
    public void testClose() {
        final ForkIntermediaryNode instance = getInstance("instance");

        final JoinIntermediaryNode join = new JoinIntermediaryNode("join", instance);

        assertEquals(join, instance.getClosingJoin());
        assertTrue(instance.isClosed());
    }
}
