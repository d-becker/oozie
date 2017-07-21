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

package org.apache.oozie.jobs.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

// TODO: Replace MapReduceAction with something more general.
public class TestActionBuilder {
    public static final String NAME = "map-reduce-name";
    public static final String QNAME = "mapred.job.queue.name";
    public static final String DEFAULT = "default";

    private Map<String, String> configExample;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        configExample = new HashMap<>();

        final String[] keys = {"mapred.map.tasks", "mapred.input.dir", "mapred.output.dir"};
        final String[] values = {"1", "${inputDir}", "${outputDir}"};

        for (int i = 0; i < keys.length; ++i) {
            configExample.put(keys[i], values[i]);
        }
    }

    @Test
    public void testAddParents() {
        MapReduceAction parent1 = Mockito.spy(new MapReduceActionBuilder().build());
        MapReduceAction parent2 = Mockito.spy(new MapReduceActionBuilder().build());

        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withParent(parent1)
                .withParent(parent2);

        MapReduceAction child = builder.build();

        assertEquals(Arrays.asList(parent1, parent2), child.getParents());

        Mockito.verify(parent1).addChild(child);
        Mockito.verify(parent2).addChild(child);

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testRemoveParent() {
        MapReduceAction parent1 = Mockito.spy(new MapReduceActionBuilder().build());
        MapReduceAction parent2 = Mockito.spy(new MapReduceActionBuilder().build());

        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withParent(parent1)
                .withParent(parent2);

        builder.withoutParent(parent2);

        MapReduceAction child = builder.build();

        assertEquals(Arrays.asList(parent1), child.getParents());

        Mockito.verify(parent1).addChild(child);

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testClearParents() {
        MapReduceAction parent1 = Mockito.spy(new MapReduceActionBuilder().build());
        MapReduceAction parent2 = Mockito.spy(new MapReduceActionBuilder().build());

        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withParent(parent1)
                .withParent(parent2);

        builder.clearParents();

        MapReduceAction child = builder.build();

        assertEquals(0, child.getParents().size());

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testNameAddedMocked() {
        MapReduceActionBuilder builder = getSpyBuilder();
        builder.withName(NAME);

        MapReduceAction mrAction = builder.build();

        assertEquals(NAME, mrAction.getName());

        Mockito.verify(builder).withName(NAME);
        Mockito.verify(builder).build();
        Mockito.verifyNoMoreInteractions(builder);
    }

    @Test
    public void testNameAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withName(NAME);

        MapReduceAction mrAction = builder.build();
        assertEquals(NAME, mrAction.getName());
    }

    @Test
    public void testNameAddedTwiceThrows() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withName(NAME);

        expectedException.expect(IllegalStateException.class);
        builder.withName("any_name");
    }

    @Test
    public void testConfigPropertyAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withConfigProperty(QNAME, DEFAULT);

        MapReduceAction mrAction = builder.build();
        assertEquals(DEFAULT, mrAction.getConfigProperty(QNAME));
    }

    @Test
    public void testSeveralConfigPropertiesAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (Map.Entry<String, String> entry : configExample.entrySet()) {
            builder.withConfigProperty(entry.getKey(), entry.getValue());
        }

        MapReduceAction mrAction = builder.build();

        for (Map.Entry<String, String> entry : configExample.entrySet()) {
            assertEquals(entry.getValue(), mrAction.getConfigProperty(entry.getKey()));
        }

        assertEquals(configExample, mrAction.getConfiguration());
    }

    @Test
    public void testSameConfigPropertyAddedTwiceThrows() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withConfigProperty(QNAME, DEFAULT);

        expectedException.expect(IllegalStateException.class);
        builder.withConfigProperty(QNAME, DEFAULT);
    }

    @Test
    public void testFromExistingAction() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        builder.withName(NAME);

        for (Map.Entry<String, String> entry : configExample.entrySet()) {
            builder.withConfigProperty(entry.getKey(), entry.getValue());
        }

        MapReduceAction mrAction = builder.build();

        List<String> keys = new ArrayList<>(configExample.keySet());
        Map<String, String> expectedModifiedConfiguration = new LinkedHashMap<>(configExample);

        String keyToModify = keys.get(0);
        String modifiedValue = "modified-property-value";
        expectedModifiedConfiguration.put(keyToModify, modifiedValue);

        String keyToRemove = keys.get(1);
        expectedModifiedConfiguration.remove(keyToRemove);


        String newKey = "new-property-name";
        String newValue = "new-property-value";
        expectedModifiedConfiguration.put(newKey, newValue);

        MapReduceActionBuilder fromExistingBuilder = new MapReduceActionBuilder(mrAction);

        final String newName = "fromExisting_" + NAME;
        fromExistingBuilder.withName(newName)
                .withConfigProperty(keyToModify, modifiedValue)
                .withConfigProperty(keyToRemove, null)
                .withConfigProperty(newKey, newValue);

        MapReduceAction modifiedMrAction = fromExistingBuilder.build();

        assertEquals(newName, modifiedMrAction.getName());
        assertEquals(expectedModifiedConfiguration, modifiedMrAction.getConfiguration());
    }

    private MapReduceActionBuilder getSpyBuilder() {
        return Mockito.spy(new MapReduceActionBuilder());
    }
}
