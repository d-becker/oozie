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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public abstract class TestNodeBuilderBaseImpl <N extends Node,
        B extends NodeBuilderBaseImpl<B> & Builder<N>> {
    static final String NAME = "map-reduce-name";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    protected abstract B getBuilderInstance();
    protected abstract B getBuilderInstance(N action);

    @Test
    public final void testIncorrectSubclassingThrows() {
        class WrongBuilder extends NodeBuilderBaseImpl<MapReduceActionBuilder> implements Builder<MapReduceAction> {

            private WrongBuilder() {
                super();
            }

            public MapReduceActionBuilder getRuntimeSelfReference() {
                return new MapReduceActionBuilder();
            }

            @Override
            public MapReduceAction build() {
                return null;
            }
        }

        expectedException.expect(IllegalStateException.class);

        new WrongBuilder().withName("obsolete");
    }

    @Test
    public void testAddParents() {
        final N parent1 = Mockito.spy(getBuilderInstance().build());
        final N parent2 = Mockito.spy(getBuilderInstance().build());

        final B builder = getBuilderInstance();
        builder.withParent(parent1)
                .withParent(parent2);

        final N child = builder.build();

        assertEquals(Arrays.asList(parent1, parent2), child.getParents());

        Mockito.verify(parent1).addChild(child);
        Mockito.verify(parent2).addChild(child);

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testWithoutParent() {
        final N parent1 = Mockito.spy(getBuilderInstance().build());
        final N parent2 = Mockito.spy(getBuilderInstance().build());

        final B builder = getBuilderInstance();
        builder.withParent(parent1)
                .withParent(parent2);

        builder.withoutParent(parent2);

        final N child = builder.build();

        assertEquals(Arrays.asList(parent1), child.getParents());

        Mockito.verify(parent1).addChild(child);

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testClearParents() {
        final N parent1 = Mockito.spy(getBuilderInstance().build());
        final N parent2 = Mockito.spy(getBuilderInstance().build());

        final B builder = getBuilderInstance();
        builder.withParent(parent1)
                .withParent(parent2);

        builder.clearParents();

        final N child = builder.build();

        assertEquals(0, child.getParents().size());

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testNameAdded() {
        final B builder = getBuilderInstance();
        builder.withName(NAME);

        final N action = builder.build();
        assertEquals(NAME, action.getName());
    }

    @Test
    public void testNameAddedTwiceThrows() {
        final B builder = getBuilderInstance();
        builder.withName(NAME);

        expectedException.expect(IllegalStateException.class);
        builder.withName("any_name");
    }

    @Test
    public void testFromExistingNode() {
        final Node parent1 = new MapReduceActionBuilder().withName("parent1").build();
        final Node parent2 = new MapReduceActionBuilder().withName("parent2").build();
        final Node parent3 = new MapReduceActionBuilder().withName("parent3").build();

        final B builder = getBuilderInstance();

        builder.withName(NAME)
                .withParent(parent1)
                .withParent(parent2);

        final N node = builder.build();

        final B fromExistingBuilder = getBuilderInstance(node);

        final String newName = "fromExisting_" + NAME;
        fromExistingBuilder.withName(newName)
                .withoutParent(parent2)
                .withParent(parent3);

        final Node modifiedNode = fromExistingBuilder.build();

        assertEquals(newName, modifiedNode.getName());
        assertEquals(Arrays.asList(parent1, parent3), modifiedNode.getParents());
    }
}
