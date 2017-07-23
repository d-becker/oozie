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

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public abstract class TestActionBuilderBase<ACTION_T extends Action, BUILDER_T extends ActionBuilderBase<ACTION_T, BUILDER_T>> {
    public static final String NAME = "map-reduce-name";
    public static final String QNAME = "mapred.job.queue.name";
    public static final String DEFAULT = "default";

    private static final ImmutableMap<String, String> CONFIG_EXAMPLE = getConfigExample();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static ImmutableMap<String, String> getConfigExample() {
        ImmutableMap.Builder<String, String> configExampleBuilder = new ImmutableMap.Builder<>();

        final String[] keys = {"mapred.map.tasks", "mapred.input.dir", "mapred.output.dir"};
        final String[] values = {"1", "${inputDir}", "${outputDir}"};

        for (int i = 0; i < keys.length; ++i) {
            configExampleBuilder.put(keys[i], values[i]);
        }

        return configExampleBuilder.build();
    }

    protected abstract BUILDER_T getBuilderInstance();
    protected abstract BUILDER_T getBuilderInstance(ACTION_T action);

    @Test
    public final void testIncorrectSubclassingThrows() {
        class WrongBuilder extends ActionBuilderBase<MapReduceAction, MapReduceActionBuilder> {

            @Override
            public MapReduceAction build() {
                return null;
            }
        }

        WrongBuilder builder = new WrongBuilder();
        expectedException.expect(ClassCastException.class);
        builder.withName("Wrong builder");
    }

    @Test
    public void testAddParents() {
        ACTION_T parent1 = Mockito.spy(getBuilderInstance().build());
        ACTION_T parent2 = Mockito.spy(getBuilderInstance().build());

        BUILDER_T builder = getBuilderInstance();
        builder.withParent(parent1)
                .withParent(parent2);

        ACTION_T child = builder.build();

        assertEquals(Arrays.asList(parent1, parent2), child.getParents());

        Mockito.verify(parent1).addChild(child);
        Mockito.verify(parent2).addChild(child);

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testWithoutParent() {
        ACTION_T parent1 = Mockito.spy(getBuilderInstance().build());
        ACTION_T parent2 = Mockito.spy(getBuilderInstance().build());

        BUILDER_T builder = getBuilderInstance();
        builder.withParent(parent1)
                .withParent(parent2);

        builder.withoutParent(parent2);

        ACTION_T child = builder.build();

        assertEquals(Arrays.asList(parent1), child.getParents());

        Mockito.verify(parent1).addChild(child);

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testClearParents() {
        ACTION_T parent1 = Mockito.spy(getBuilderInstance().build());
        ACTION_T parent2 = Mockito.spy(getBuilderInstance().build());

        BUILDER_T builder = getBuilderInstance();
        builder.withParent(parent1)
                .withParent(parent2);

        builder.clearParents();

        ACTION_T child = builder.build();

        assertEquals(0, child.getParents().size());

        Mockito.verifyNoMoreInteractions(parent1);
        Mockito.verifyNoMoreInteractions(parent2);
    }

    @Test
    public void testNameAdded() {
        BUILDER_T builder = getBuilderInstance();
        builder.withName(NAME);

        ACTION_T action = builder.build();
        assertEquals(NAME, action.getName());
    }

    @Test
    public void testNameAddedTwiceThrows() {
        BUILDER_T builder = getBuilderInstance();
        builder.withName(NAME);

        expectedException.expect(IllegalStateException.class);
        builder.withName("any_name");
    }

    @Test
    public void testConfigPropertyAdded() {
        BUILDER_T builder = getBuilderInstance();
        builder.withConfigProperty(QNAME, DEFAULT);

        ACTION_T action = builder.build();
        assertEquals(DEFAULT, action.getConfigProperty(QNAME));
    }

    @Test
    public void testSeveralConfigPropertiesAdded() {
        BUILDER_T builder = getBuilderInstance();

        for (Map.Entry<String, String> entry : CONFIG_EXAMPLE.entrySet()) {
            builder.withConfigProperty(entry.getKey(), entry.getValue());
        }

        ACTION_T action = builder.build();

        for (Map.Entry<String, String> entry : CONFIG_EXAMPLE.entrySet()) {
            assertEquals(entry.getValue(), action.getConfigProperty(entry.getKey()));
        }

        assertEquals(CONFIG_EXAMPLE, action.getConfiguration());
    }

    @Test
    public void testSameConfigPropertyAddedTwiceThrows() {
        BUILDER_T builder = getBuilderInstance();
        builder.withConfigProperty(QNAME, DEFAULT);

        expectedException.expect(IllegalStateException.class);
        builder.withConfigProperty(QNAME, DEFAULT);
    }

    @Test
    public void testFromExistingAction() {
        BUILDER_T builder = getBuilderInstance();

        builder.withName(NAME);

        for (Map.Entry<String, String> entry : CONFIG_EXAMPLE.entrySet()) {
            builder.withConfigProperty(entry.getKey(), entry.getValue());
        }

        ACTION_T action = builder.build();

        List<String> keys = new ArrayList<>(CONFIG_EXAMPLE.keySet());
        Map<String, String> expectedModifiedConfiguration = new LinkedHashMap<>(CONFIG_EXAMPLE);

        String keyToModify = keys.get(0);
        String modifiedValue = "modified-property-value";
        expectedModifiedConfiguration.put(keyToModify, modifiedValue);

        String keyToRemove = keys.get(1);
        expectedModifiedConfiguration.remove(keyToRemove);


        String newKey = "new-property-name";
        String newValue = "new-property-value";
        expectedModifiedConfiguration.put(newKey, newValue);

        BUILDER_T fromExistingBuilder = getBuilderInstance(action);

        final String newName = "fromExisting_" + NAME;
        fromExistingBuilder.withName(newName)
                .withConfigProperty(keyToModify, modifiedValue)
                .withConfigProperty(keyToRemove, null)
                .withConfigProperty(newKey, newValue);

        ACTION_T modifiedMrAction = fromExistingBuilder.build();

        assertEquals(newName, modifiedMrAction.getName());
        assertEquals(expectedModifiedConfiguration, modifiedMrAction.getConfiguration());
    }
}
