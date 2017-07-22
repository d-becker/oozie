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

public abstract class ActionBuilder<T extends ActionBuilder> {
    protected final ModifyOnce<String> name;
    protected final List<Action> parents;
    protected final Map<String, ModifyOnce<String>> configuration;

    protected ActionBuilder() {
        parents = new ArrayList<>();
        name = new ModifyOnce<>();
        configuration = new LinkedHashMap<>();
    }

    public ActionBuilder(final Action action) {
        parents = new ArrayList<>(action.getParents());
        name = new ModifyOnce<>(action.getName());
        configuration = immutableConfigurationMapToModifyOnce(action.getConfiguration());
    }

    public T withParent(Action action) {
        parents.add(action);
        return typeCastThis();
    }

    public T withoutParent(Action parent) {
        parents.remove(parent);
        return typeCastThis();
    }

    public T clearParents() {
        parents.clear();
        return typeCastThis();
    }

    public T withName(String name) {
        this.name.set(name);
        return typeCastThis();
    }

    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public T withConfigProperty(String key, String value) {
        ModifyOnce<String> mappedValue = this.configuration.get(key);

        if (mappedValue == null) {
            mappedValue = new ModifyOnce<>(value);
            this.configuration.put(key, mappedValue);
        }

        mappedValue.set(value);
        return typeCastThis();
    }

    protected Action.ConstructionData getConstructionData() {
        final String nameStr = this.name.get();
        final ImmutableList<Action> parentsList = new ImmutableList.Builder<Action>().addAll(parents).build();
        final ImmutableMap<String, String> configurationMap = modifyOnceConfigurationMapToImmutable(this.configuration);

        return new Action.ConstructionData(nameStr, parentsList, configurationMap);
    }

    private T typeCastThis() {
        T result;
        try {
            result = (T) this;
        } catch (ClassCastException e) {
            throw new ClassCastException("Can't convert " + this.getClass()
                    + " object to the requested concrete type."
                    + " Probably the requested concrete type T doesn't extend ActionBuilder<T>.");
        }

        return result;
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
