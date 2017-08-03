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

package org.apache.oozie.jobs.api.action;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestKillBuilder extends TestNodeBuilderBaseImpl<Kill, KillBuilder> {
    public static final String MESSAGE = "Error message.";

    @Override
    protected KillBuilder getBuilderInstance() {
        return new KillBuilder();
    }

    @Override
    protected KillBuilder getBuilderInstance(Kill kill) {
        return new KillBuilder(kill);
    }

    @Test
    public void testMessageAdded() {
        KillBuilder builder = new KillBuilder();
        builder.withMessage(MESSAGE);
    }

    @Test
    public void testMessageAddedTwiceThrows() {
        KillBuilder builder = new KillBuilder();
        builder.withMessage(MESSAGE);

        expectedException.expect(IllegalStateException.class);
        builder.withMessage("Any message.");
    }

    @Test
    public void testFromExistingKill() {
        Node parent1 = new MapReduceActionBuilder().withName("parent1").build();
        Node parent2 = new MapReduceActionBuilder().withName("parent2").build();
        Node parent3 = new MapReduceActionBuilder().withName("parent3").build();

        KillBuilder builder = getBuilderInstance();

        builder.withName(NAME)
                .withMessage(MESSAGE)
                .withParent(parent1)
                .withParent(parent2);

        Kill kill = builder.build();

        KillBuilder fromExistingBuilder = getBuilderInstance(kill);

        final String newMessage = "From existing: " + MESSAGE;
        fromExistingBuilder.withMessage(newMessage)
                .withoutParent(parent2)
                .withParent(parent3);

        Kill modifiedNode = fromExistingBuilder.build();

        assertEquals(NAME, modifiedNode.getName());
        assertEquals(newMessage, modifiedNode.getMessage());
        assertEquals(Arrays.asList(parent1, parent3), modifiedNode.getParents());
    }
}
