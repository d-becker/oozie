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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class TestNodeBase<T extends NodeBase> {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    static final String NAME = "node-name";

    protected abstract T getInstance(final String name);

    @Test
    public void testNameIsCorrect() {
        final T instance = getInstance(NAME);
        assertEquals(NAME, instance.getName());
    }

    @Test
    public void testAddParentWithoutCondition() {
        final NodeBase parent = new ExplicitNode("parent", null);
        final T child = getInstance("child");

        child.addParent(parent);

        List<NodeBase> expectedParents = Arrays.asList(parent);
        assertEquals(expectedParents, child.getParentsWithoutCondition());
        assertEquals(expectedParents, child.getAllParents());
    }

    @Test
    public void testAddParentWithCondition() {
        final Decision parent = new Decision("parent");
        final T child = getInstance("child");

        final String condition = "condition";

        child.addParentWithCondition(parent, condition);

        List<NodeBase.DagNodeWithCondition> parentsWithCondition = child.getParentsWithCondition();
        assertEquals(1, parentsWithCondition.size());
        assertEquals(parent, parentsWithCondition.get(0).getNode());
        assertEquals(condition, parentsWithCondition.get(0).getCondition());

        List<NodeBase> expectedParents = Arrays.asList((NodeBase) parent);
        assertEquals(expectedParents, child.getAllParents());
    }

    @Test
    public void testRemoveParentWithoutCondition() {
        final ExplicitNode parent = new ExplicitNode("parent", null);
        final T child = getInstance("child");

        child.addParent(parent);
        child.removeParent(parent);

        assertTrue(child.getParentsWithoutCondition().isEmpty());
        assertTrue(child.getAllParents().isEmpty());
    }

    @Test
    public void testRemoveParentWithCondition() {
        final Decision parent = new Decision("parent");
        final T child = getInstance("child");

        final String condition = "condition";

        child.addParentWithCondition(parent, condition);
        child.removeParent(parent);

        assertTrue(child.getParentsWithoutCondition().isEmpty());
        assertTrue(child.getAllParents().isEmpty());
    }

    @Test
    public void testRemoveNonExistentParentThrows() {
        final NodeBase parent = new ExplicitNode("parent", null);
        final T child = getInstance("child");

        expectedException.expect(IllegalArgumentException.class);
        child.removeParent(parent);
    }

    @Test
    public void testClearExistingParents() {
        final NodeBase parent1 = new ExplicitNode("parent1", null);
        final Decision parent2 = new Decision("parent2");
        final T instance = getInstance("instance");

        instance.addParent(parent1);
        instance.addParentWithCondition(parent2, "any_condition");

        instance.clearParents();

        assertTrue(instance.getParentsWithoutCondition().isEmpty());
        assertTrue(instance.getParentsWithCondition().isEmpty());
        assertTrue(instance.getAllParents().isEmpty());
    }

    @Test
    public void testClearNonExistentParent() {
        final Start parent = new Start("parent");
        final T instance = getInstance("instance");

        instance.clearParents();

        assertTrue(instance.getParentsWithoutCondition().isEmpty());
        assertTrue(instance.getParentsWithCondition().isEmpty());
        assertTrue(instance.getAllParents().isEmpty());
    }
}
