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

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public abstract class TestActionBuilderBaseImpl<A extends Action,
            B extends ActionBuilderBaseImpl<B> & Builder<A>>
        extends TestNodeBuilderBaseImpl<A, B>{
    private static final String MAPRED_JOB_QUEUE_NAME = "mapred.job.queue.name";
    private static final String DEFAULT = "default";

    private static final ImmutableMap<String, String> CONFIG_EXAMPLE = getConfigExample();

    private static ImmutableMap<String, String> getConfigExample() {
        final ImmutableMap.Builder<String, String> configExampleBuilder = new ImmutableMap.Builder<>();

        final String[] keys = {"mapred.map.tasks", "mapred.input.dir", "mapred.output.dir"};
        final String[] values = {"1", "${inputDir}", "${outputDir}"};

        for (int i = 0; i < keys.length; ++i) {
            configExampleBuilder.put(keys[i], values[i]);
        }

        return configExampleBuilder.build();
    }

    @Test
    public void testConfigPropertyAdded() {
        final B builder = getBuilderInstance();
        builder.withConfigProperty(MAPRED_JOB_QUEUE_NAME, DEFAULT);

        final A action = builder.build();
        assertEquals(DEFAULT, action.getConfigProperty(MAPRED_JOB_QUEUE_NAME));
    }

    @Test
    public void testSeveralConfigPropertiesAdded() {
        final B builder = getBuilderInstance();

        for (final Map.Entry<String, String> entry : CONFIG_EXAMPLE.entrySet()) {
            builder.withConfigProperty(entry.getKey(), entry.getValue());
        }

        final A action = builder.build();

        for (final Map.Entry<String, String> entry : CONFIG_EXAMPLE.entrySet()) {
            assertEquals(entry.getValue(), action.getConfigProperty(entry.getKey()));
        }

        assertEquals(CONFIG_EXAMPLE, action.getConfiguration());
    }

    @Test
    public void testSameConfigPropertyAddedTwiceThrows() {
        final B builder = getBuilderInstance();
        builder.withConfigProperty(MAPRED_JOB_QUEUE_NAME, DEFAULT);

        expectedException.expect(IllegalStateException.class);
        builder.withConfigProperty(MAPRED_JOB_QUEUE_NAME, DEFAULT);
    }

    @Test
    public void testFromExistingAction() {
        final B builder = getBuilderInstance();

        builder.withName(NAME);

        for (final Map.Entry<String, String> entry : CONFIG_EXAMPLE.entrySet()) {
            builder.withConfigProperty(entry.getKey(), entry.getValue());
        }

        final A action = builder.build();

        final List<String> keys = new ArrayList<>(CONFIG_EXAMPLE.keySet());
        final Map<String, String> expectedModifiedConfiguration = new LinkedHashMap<>(CONFIG_EXAMPLE);

        final String keyToModify = keys.get(0);
        final String modifiedValue = "modified-property-value";
        expectedModifiedConfiguration.put(keyToModify, modifiedValue);

        final String keyToRemove = keys.get(1);
        expectedModifiedConfiguration.remove(keyToRemove);

        final String newKey = "new-property-name";
        final String newValue = "new-property-value";
        expectedModifiedConfiguration.put(newKey, newValue);

        final B fromExistingBuilder = getBuilderInstance(action);

        final String newName = "fromExisting_" + NAME;
        fromExistingBuilder.withName(newName)
                .withConfigProperty(keyToModify, modifiedValue)
                .withConfigProperty(keyToRemove, null)
                .withConfigProperty(newKey, newValue);

        final A modifiedMrAction = fromExistingBuilder.build();

        assertEquals(newName, modifiedMrAction.getName());
        assertEquals(expectedModifiedConfiguration, modifiedMrAction.getConfiguration());
    }
}
