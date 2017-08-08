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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestEnd extends TestNodeBase<End> {
    @Override
    protected End getInstance(final String name) {
        return new End(name);
    }

    @Test
    public void testRemoveNonexistentParentThrows() {
        final Start parent = new Start("parent");
        final End instance = getInstance("instance");

        expectedException.expect(IllegalArgumentException.class);
        instance.removeParent(parent);
    }

    @Test
    public void testAddedAsParentThrows () {
        final End instance = getInstance("instance");
        final ExplicitNode child = new ExplicitNode("child", null);

        expectedException.expect(IllegalStateException.class);
        child.addParent(instance);
    }

    @Test
    public void testGetChildren() {
        final End instance = getInstance("end");

        assertTrue(instance.getChildren().isEmpty());
    }
}
