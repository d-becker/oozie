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

import org.apache.oozie.jobs.api.MapReduceActionBuilder;
import org.apache.oozie.jobs.api.Node;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestRealIntermediaryNode extends TestIntermediaryNode<RealIntermediaryNode> {
    @Override
    protected RealIntermediaryNode getInstance(final String name) {
        return new RealIntermediaryNode(name, null);
    }

    @Test
    public void testRealNode() {
        final Node node = new MapReduceActionBuilder().build();
        final RealIntermediaryNode instance = new RealIntermediaryNode(NAME, node);

        assertEquals(node, instance.getRealNode());
    }
}
