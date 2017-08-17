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
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigurationHandlerBuilder {
    private final Map<String, ModifyOnce<String>> configuration;

    public ConfigurationHandlerBuilder() {
        configuration = new LinkedHashMap<>();
    }

    public ConfigurationHandlerBuilder(final Map<String, String> configurationMap) {
        final Map<String, ModifyOnce<String>> modifyOnceEntries = new LinkedHashMap<>();
        for (final Map.Entry<String, String> keyAndValue : configurationMap.entrySet()) {
            modifyOnceEntries.put(keyAndValue.getKey(), new ModifyOnce<>(keyAndValue.getValue()));
        }

        configuration = modifyOnceEntries;
    }

    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public ConfigurationHandlerBuilder withConfigProperty(final String key, final String value) {
        ModifyOnce<String> mappedValue = this.configuration.get(key);

        if (mappedValue == null) {
            mappedValue = new ModifyOnce<>(value);
            this.configuration.put(key, mappedValue);
        }

        mappedValue.set(value);

        return this;
    }

    public ConfigurationHandler build() {
        final Map<String, String> mutableConfiguration = new LinkedHashMap<>();
        for (final Map.Entry<String, ModifyOnce<String>> modifyOnceEntry : this.configuration.entrySet()) {
            if (modifyOnceEntry.getValue().get() != null) {
                mutableConfiguration.put(modifyOnceEntry.getKey(), modifyOnceEntry.getValue().get());
            }
        }

        final ImmutableMap<String, String> configurationMap = ImmutableMap.copyOf(mutableConfiguration);

        return new ConfigurationHandler(configurationMap);
    }
}
