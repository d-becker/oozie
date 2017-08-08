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

public class TestJoin extends TestNodeBase<Join> {
    @Override
    protected Join getInstance(final String name) {
        return new Join(name, new Fork("fork"));
    }

    @Test
    public void testCorrespondingForkIsCorrect() {
        Fork fork = new Fork("fork");
        Join join = new Join("join", fork);

        assertEquals(fork, join.getForkPair());

        assertEquals(join, fork.getClosingJoin());
        assertTrue(fork.isClosed());
    }

    @Test
    public void testJoinAddedAsParentWhenItHasNoChild() {
        final Join instance = getInstance("instance");
        final NodeBase child = new ExplicitNode("child", null);

        child.addParent(instance);

        assertEquals(child, instance.getChild());
    }

    @Test
    public void testJoinAddedAsParentWhenItAlreadyHasAChildThrows() {
        final Join instance = getInstance("instance");
        final NodeBase child1 = new ExplicitNode("child1", null);
        final NodeBase child2 = new ExplicitNode("child2", null);

        child1.addParent(instance);

        expectedException.expect(IllegalStateException.class);
        child2.addParent(instance);
    }

    @Test
    public void testJoinRemovedAsParent() {
        final Join instance = getInstance("instance");
        final NodeBase child = new ExplicitNode("child", null);

        child.addParent(instance);

        child.removeParent(instance);

        assertEquals(null, instance.getChild());
    }

    @Test
    public void testGetChildren() {
        final Join instance = getInstance("instance");
        final NodeBase child = new ExplicitNode("child", null);

        child.addParent(instance);

        assertEquals(Arrays.asList(child), instance.getChildren());
    }
}
