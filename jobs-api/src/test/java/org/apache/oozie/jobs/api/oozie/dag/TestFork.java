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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestFork extends TestNodeBase<Fork> {
    @Override
    protected Fork getInstance(final String name) {
        return new Fork(name);
    }

    @Test
    public void testForkAddedAsParentWhenItHasNoChild() {
        final Fork instance = getInstance("instance");
        final NodeBase child = getInstance("child");

        child.addParent(instance);

        assertEquals(Arrays.asList(child), instance.getChildren());
    }

    @Test
    public void testForkAddedAsParentWhenItAlreadyHasAChild() {
        final Fork instance = getInstance("instance");
        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);

        child1.addParent(instance);
        child2.addParent(instance);

        assertEquals(Arrays.asList(child1, child2), instance.getChildren());
    }

    @Test
    public void testForkRemovedAsParent() {
        final Fork instance = getInstance("instance");
        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);
        final NodeBase child3 = new ExplicitNode("child3", null);
        final NodeBase child4 = new ExplicitNode("child4", null);
        final NodeBase child5 = new ExplicitNode("child5", null);

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
        final Fork instance = getInstance("instance");

        final Join join = new Join("join", instance);

        assertEquals(join, instance.getClosingJoin());
        assertTrue(instance.isClosed());
    }
}
