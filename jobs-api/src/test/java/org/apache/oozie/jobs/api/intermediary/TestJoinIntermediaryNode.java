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

public class TestJoinIntermediaryNode extends TestIntermediaryNode<JoinIntermediaryNode> {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Override
    protected JoinIntermediaryNode getInstance(final String name) {
        return new JoinIntermediaryNode(name, null);
    }

    @Test
    public void testCorrespondingForkIsCorrect() {
        ForkIntermediaryNode fork = new ForkIntermediaryNode("fork");
        JoinIntermediaryNode join = new JoinIntermediaryNode("join", fork);

        assertEquals(fork, join.getCorrespondingFork());
    }

    @Test
    public void testAddParentWhenNoneAlreadyExists() {
        final NormalIntermediaryNode parent = new NormalIntermediaryNode("parent", null);
        final JoinIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent);
        assertEquals(Arrays.asList(parent), instance.getParents());
        assertEquals(instance, parent.getChild());
    }

    @Test
    public void testAddParentWhenSomeAlreadyExist() {
        final IntermediaryNode parent1 = new NormalIntermediaryNode("parent1", null);
        final IntermediaryNode parent2 = new NormalIntermediaryNode("parent2", null);

        final JoinIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent1);
        instance.addParent(parent2);

        assertEquals(Arrays.asList(parent1, parent2), instance.getParents());
    }

    @Test
    public void testRemoveExistingParent() {
        final NormalIntermediaryNode parent1 = new NormalIntermediaryNode("parent1", null);
        final NormalIntermediaryNode parent2 = new NormalIntermediaryNode("parent2", null);

        final JoinIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent1);
        instance.addParent(parent2);

        instance.removeParent(parent2);
        assertEquals(Arrays.asList(parent1), instance.getParents());
        assertEquals(null, parent2.getChild());
    }

    @Test
    public void testRemoveNonexistentParentThrows() {
        final NormalIntermediaryNode parent = new NormalIntermediaryNode("parent", null);
        final JoinIntermediaryNode instance = getInstance("instance");

        expectedException.expect(IllegalArgumentException.class);
        instance.removeParent(parent);
    }

    @Test
    public void testClearExistingParent() {
        final NormalIntermediaryNode parent1 = new NormalIntermediaryNode("parent1", null);
        final NormalIntermediaryNode parent2 = new NormalIntermediaryNode("parent2", null);

        final JoinIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent1);
        instance.addParent(parent2);

        instance.clearParents();
        assertEquals(0, instance.getParents().size());
        assertEquals(null, parent1.getChild());
        assertEquals(null, parent2.getChild());
    }

    @Test
    public void testClearNonExistentParent() {
        final JoinIntermediaryNode instance = getInstance("instance");

        instance.clearParents();
        assertEquals(0, instance.getParents().size());
    }

    @Test
    public void testJoinAddedAsParentWhenItHasNoChild() {
        final JoinIntermediaryNode instance = getInstance("instance");
        final IntermediaryNode child = new NormalIntermediaryNode("child", null);

        child.addParent(instance);

        assertEquals(child, instance.getChild());
    }

    @Test
    public void testJoinAddedAsParentWhenItAlreadyHasAChildThrows() {
        final JoinIntermediaryNode instance = getInstance("instance");
        final IntermediaryNode child1 = new NormalIntermediaryNode("child1", null);
        final IntermediaryNode child2 = new NormalIntermediaryNode("child2", null);

        child1.addParent(instance);

        expectedException.expect(IllegalStateException.class);
        child2.addParent(instance);
    }

    @Test
    public void testJoinRemovedAsParent() {
        final JoinIntermediaryNode instance = getInstance("instance");
        final IntermediaryNode child = new NormalIntermediaryNode("child", null);

        child.addParent(instance);

        child.removeParent(instance);

        assertEquals(null, instance.getChild());
    }

    @Test
    public void testGetChildren() {
        final JoinIntermediaryNode instance = getInstance("instance");
        final IntermediaryNode child = new NormalIntermediaryNode("child", null);

        child.addParent(instance);

        assertEquals(Arrays.asList(child), instance.getChildren());
    }
}
