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

import static org.junit.Assert.assertEquals;

public class TestStartIntermediaryNode extends TestIntermediaryNode<StartIntermediaryNode> {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();


    @Override
    protected StartIntermediaryNode getInstance(final String name) {
        return new StartIntermediaryNode(name);
    }

    @Test
    public void testAddParent() {
        final NormalIntermediaryNode parent = new NormalIntermediaryNode("parent", null);
        final StartIntermediaryNode start = getInstance("start");

        expectedException.expect(IllegalStateException.class);
        start.addParent(parent);
    }

    @Test
    public void testRemoveParent() {
        final StartIntermediaryNode start = getInstance("start");

        expectedException.expect(IllegalStateException.class);
        start.removeParent(null);
    }

    @Test
    public void testStartAddedAsParentWhenItHasNoChild() {
        final StartIntermediaryNode start = getInstance("start");
        final IntermediaryNode child = new NormalIntermediaryNode("child", null);

        child.addParent(start);

        assertEquals(child, start.getChild());
    }

    @Test
    public void testStartAddedAsParentWhenItAlreadyHasAChildThrows() {
        final StartIntermediaryNode start = getInstance("start");
        final IntermediaryNode child1 = new NormalIntermediaryNode("child1", null);
        final IntermediaryNode child2 = new NormalIntermediaryNode("child2", null);

        child1.addParent(start);

        expectedException.expect(IllegalStateException.class);
        child2.addParent(start);
    }

    @Test
    public void testStartRemovedAsParent() {
        final StartIntermediaryNode instance = getInstance("instance");
        final IntermediaryNode child = new NormalIntermediaryNode("child", null);

        child.addParent(instance);
        child.removeParent(instance);

        assertEquals(null, instance.getChild());
    }
}
