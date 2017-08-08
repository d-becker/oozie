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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestStart extends TestNodeBase<Start> {
    @Override
    protected Start getInstance(final String name) {
        return new Start(name);
    }

    @Test
    @Override
    public void testAddParentWithoutCondition() {
        final ExplicitNode parent = new ExplicitNode("parent", null);
        final Start start = getInstance("start");

        expectedException.expect(IllegalStateException.class);
        start.addParent(parent);
    }

    @Test
    @Override
    public void testAddParentWithCondition() {
        final Decision parent = new Decision("parent");
        final Start start = getInstance("start");

        expectedException.expect(IllegalStateException.class);
        start.addParentWithCondition(parent, "any_condition");
    }

    @Test
    @Override
    public void testRemoveParentWithoutCondition() {
        final Start start = getInstance("start");

        expectedException.expect(IllegalStateException.class);
        start.removeParent(null);
    }

    @Test
    @Override
    public void testRemoveParentWithCondition() {
        // Start nodes cannot have parents, so we cannot test removing it.
    }

    @Test
    @Override
    public void testRemoveNonExistentParentThrows() {
        final NodeBase parent = new ExplicitNode("parent", null);
        final Start child = getInstance("child");

        expectedException.expect(IllegalStateException.class);
        child.removeParent(parent);
    }

    @Test
    @Override
    public void testClearExistingParents() {
        // Start nodes cannot have parents, so we cannot test removing it.
    }

    @Test
    public void testStartAddedAsParentWhenItHasNoChild() {
        final Start start = getInstance("start");
        final NodeBase child = new ExplicitNode("child", null);

        child.addParent(start);

        assertEquals(child, start.getChild());
    }

    @Test
    public void testStartAddedAsParentWhenItAlreadyHasAChildThrows() {
        final Start start = getInstance("start");
        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);

        child1.addParent(start);

        expectedException.expect(IllegalStateException.class);
        child2.addParent(start);
    }

    @Test
    public void testStartRemovedAsParent() {
        final Start instance = getInstance("instance");
        final NodeBase child = new ExplicitNode("child", null);

        child.addParent(instance);
        child.removeParent(instance);

        assertEquals(null, instance.getChild());
        assertTrue(instance.getChildren().isEmpty());
    }

    @Test
    public void testGetChildren() {
        final Start start = getInstance("start");
        final NodeBase child = new ExplicitNode("child", null);

        child.addParent(start);

        assertEquals(Arrays.asList(child), start.getChildren());
    }
}
