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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public abstract class TestIntermediaryNode<T extends IntermediaryNode> {
    public static final String NAME = "node name";

    protected abstract T getInstance(final String name);

    @Test
    public void testNameIsCorrect() {
        final T instance = getInstance(NAME);
        assertEquals(NAME, instance.getName());
    }

    @Test
    public void testAddParent() {
        final IntermediaryNode parent = new DummyIntermediaryNode("parent");
        final T child = getInstance("child");

        child.addParent(parent);

        assertEquals(Arrays.asList(parent), child.getParents());
        assertEquals(Arrays.asList(child), parent.getChildren());
    }

    @Test
    public void testAddChild() {
        final T parent = getInstance("parent");
        final IntermediaryNode child = new DummyIntermediaryNode("child");

        parent.addChild(child);

        assertEquals(Arrays.asList(parent), child.getParents());
        assertEquals(Arrays.asList(child), parent.getChildren());
    }

    @Test
    public void testRemoveParent() {
        final IntermediaryNode parent1 = new DummyIntermediaryNode("parent1");
        final IntermediaryNode parent2 = new DummyIntermediaryNode("parent2");
        final T child = getInstance("child");

        child.addParent(parent1);
        child.addParent(parent2);
        assertEquals(Arrays.asList(parent1, parent2), child.getParents());

        child.removeParent(parent1);
        assertEquals(Arrays.asList(parent2), child.getParents());
        assertFalse(parent1.getChildren().contains(child));
    }

    @Test
    public void testRemoveChild() {
        final T parent = getInstance("parent");
        final IntermediaryNode child1 = new DummyIntermediaryNode("child1");
        final IntermediaryNode child2 = new DummyIntermediaryNode("child2");

        parent.addChild(child1);
        parent.addChild(child2);

        assertEquals(Arrays.asList(child1, child2), parent.getChildren());

        parent.removeChild(child1);
        assertEquals(Arrays.asList(child2), parent.getChildren());
        assertFalse(child1.getParents().contains(parent));
    }
}
