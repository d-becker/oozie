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
import static org.junit.Assert.assertTrue;

public class TestEndIntermediaryNode extends TestIntermediaryNode<EndIntermediaryNode> {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Override
    protected EndIntermediaryNode getInstance(final String name) {
        return new EndIntermediaryNode(name);
    }

    @Test
    public void testAddParentWhenNoneAlreadyExists() {
        final StartIntermediaryNode parent = new StartIntermediaryNode("parent");
        final EndIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent);
        assertEquals(parent, instance.getParent());
        assertEquals(instance, parent.getChild());
    }

    @Test
    public void testAddParentWhenItAlreadyExists() {
        final StartIntermediaryNode parent1 = new StartIntermediaryNode("parent1");
        final StartIntermediaryNode parent2 = new StartIntermediaryNode("parent2");
        final EndIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent1);

        expectedException.expect(IllegalStateException.class);
        instance.addParent(parent2);
    }

    @Test
    public void testRemoveExistingParent() {
        final StartIntermediaryNode parent = new StartIntermediaryNode("parent");
        final EndIntermediaryNode instance = getInstance("instance");

        instance.addParent(parent);

        instance.removeParent(parent);
        assertEquals(null, instance.getParent());
        assertEquals(null, parent.getChild());
    }

    @Test
    public void testRemoveNonexistentParentThrows() {
        final StartIntermediaryNode parent = new StartIntermediaryNode("parent");
        final EndIntermediaryNode instance = getInstance("instance");

        expectedException.expect(IllegalArgumentException.class);
        instance.removeParent(parent);
    }

    @Test
    public void testAddedAsParentThrows () {
        final EndIntermediaryNode instance = getInstance("instance");
        final NormalIntermediaryNode child = new NormalIntermediaryNode("child", null);

        expectedException.expect(IllegalStateException.class);
        child.addParent(instance);
    }

    @Test
    public void testGetChildren() {
        EndIntermediaryNode instance = getInstance("end");

        assertTrue(instance.getChildren().isEmpty());
    }
}
