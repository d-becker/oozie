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

public class TestDecision extends TestNodeBase<Decision> {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Override
    protected Decision getInstance(String name) {
        return new Decision(name);
    }

    @Test
    public void testAddParentWhenNoneAlreadyExists() {
        final Start parent = new Start("parent");
        final Decision instance = getInstance("instance");

        instance.addParent(parent);
        assertEquals(parent, instance.getParent());
        assertEquals(instance, parent.getChild());
    }

    @Test
    public void testAddParentWhenItAlreadyExistsThrows() {
        final NodeBase parent1 = new ExplicitNode("parent1", null);
        final NodeBase parent2 = new ExplicitNode("parent2", null);

        final Decision instance = getInstance("instance");

        instance.addParent(parent1);

        expectedException.expect(IllegalStateException.class);
        instance.addParent(parent2);
    }

    @Test
    public void testRemoveExistingParent() {
        final Start parent = new Start("parent");
        final Decision instance = getInstance("instance");

        instance.addParent(parent);

        instance.removeParent(parent);
        assertEquals(null, instance.getParent());
        assertEquals(null, parent.getChild());
    }

    @Test
    public void testRemoveNonexistentParentThrows() {
        final Start parent = new Start("parent");
        final Decision instance = getInstance("instance");

        expectedException.expect(IllegalArgumentException.class);
        instance.removeParent(parent);
    }

    @Test
    public void testClearExistingParent() {
        final Start parent = new Start("parent");
        final Decision instance = getInstance("instance");

        instance.addParent(parent);

        instance.clearParents();
        assertEquals(null, instance.getParent());
        assertEquals(null, parent.getChild());
    }

    @Test
    public void testClearNonExistentParent() {
        final Start parent = new Start("parent");
        final Decision instance = getInstance("instance");

        instance.clearParents();
        assertEquals(null, instance.getParent());
        assertEquals(null, parent.getChild());
    }

    @Test
    public void testChildrenWithConditionsAreCorrect() {
        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);

        final Decision decision = getInstance("decision");

        final String condition1 = "condition1";
        final String condition2 = "condition2";

        child1.addParentWithCondition(decision, condition1);
        child2.addParentWithCondition(decision, condition2);

        List<Decision.DagNodeWithCondition> childrenWithConditions = decision.getChildrenWithConditions();

        assertEquals(2, childrenWithConditions.size());

        assertEquals(child1, childrenWithConditions.get(0).getNode());
        assertEquals(condition1, childrenWithConditions.get(0).getCondition());

        assertEquals(child2, childrenWithConditions.get(1).getNode());
        assertEquals(condition2, childrenWithConditions.get(1).getCondition());
    }

    @Test
    public void testDecisionRemovedAsParent() {
        final Decision instance = getInstance("instance");
        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);
        final NodeBase child3 = new ExplicitNode("child3", null);
        final NodeBase child4 = new ExplicitNode("child4", null);
        final NodeBase child5 = new ExplicitNode("child5", null);

        child1.addParentWithCondition(instance, "condition");
        child2.addParentWithCondition(instance, "condition");
        child3.addParentWithCondition(instance, "condition");
        child4.addParentWithCondition(instance, "condition");
        child5.addParentWithCondition(instance, "condition");

        child5.removeParent(instance);

        assertEquals(Arrays.asList(child1, child2, child3, child4), instance.getChildren());
    }
}
