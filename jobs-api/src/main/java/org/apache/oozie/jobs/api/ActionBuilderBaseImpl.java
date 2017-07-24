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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ActionBuilderBaseImpl<BUILDER_T extends ActionBuilderBaseImpl<BUILDER_T>>
        extends NodeBuilderBaseImpl<BUILDER_T> {
    private final Map<String, ModifyOnce<String>> configuration;

    protected ActionBuilderBaseImpl() {
        super();

        configuration = new LinkedHashMap<>();
    }

    public ActionBuilderBaseImpl(final Action action) {
        super(action);

        configuration = immutableConfigurationMapToModifyOnce(action.getConfiguration());
    }



    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public BUILDER_T withConfigProperty(String key, String value) {
        ModifyOnce<String> mappedValue = this.configuration.get(key);

        if (mappedValue == null) {
            mappedValue = new ModifyOnce<>(value);
            this.configuration.put(key, mappedValue);
        }

        mappedValue.set(value);
        return concreteThis;
    }



    protected Action.ConstructionData getConstructionData() {
        final String nameStr = this.name.get();
        final ImmutableList<Node> parentsList = new ImmutableList.Builder<Node>().addAll(parents).build();
        final ImmutableMap<String, String> configurationMap = modifyOnceConfigurationMapToImmutable(this.configuration);

        return new Action.ConstructionData(nameStr, parentsList, configurationMap);
    }

    private static ImmutableMap<String, String> modifyOnceConfigurationMapToImmutable(Map<String, ModifyOnce<String>> map) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();

        for (Map.Entry<String, ModifyOnce<String>> entry : map.entrySet()) {
            String value = entry.getValue().get();
            if (value != null) {
                builder.put(entry.getKey(), value);
            }
        }

        return builder.build();
    }

    private static Map<String, ModifyOnce<String>> immutableConfigurationMapToModifyOnce(Map<String, String> map) {
        Map<String, ModifyOnce<String>> result = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.put(entry.getKey(), new ModifyOnce<>(entry.getValue()));
        }

        return result;
    }
}
