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

import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.LinkedHashMap;
import java.util.Map;

public class SubWorkflowActionBuilder
        extends NodeBuilderBaseImpl<SubWorkflowActionBuilder> implements Builder<SubWorkflowAction> {
    private final ModifyOnce<String> appPath;
    private final ModifyOnce<Boolean> propagateConfiguration;
    private final Map<String, ModifyOnce<String>> configuration;

    public static SubWorkflowActionBuilder create() {
        final ModifyOnce<String> appPath = new ModifyOnce<>();
        final ModifyOnce<Boolean> propagateConfiguration = new ModifyOnce<>(false);
        final Map<String, ModifyOnce<String>> configuration = new LinkedHashMap<>();

        return new SubWorkflowActionBuilder(null, appPath, propagateConfiguration, configuration);
    }

    public static SubWorkflowActionBuilder createFromExistingAction(final SubWorkflowAction action) {
        final ModifyOnce<String> appPath = new ModifyOnce<>(action.getAppPath());
        final ModifyOnce<Boolean> propagateConfiguration = new ModifyOnce<>(action.isPropagatingConfiguration());
        final Map<String, ModifyOnce<String>> configuration = ActionAttributesBuilder.convertToModifyOnceMap(action.getConfiguration());

        return new SubWorkflowActionBuilder(action, appPath, propagateConfiguration, configuration);
    }

    SubWorkflowActionBuilder(final SubWorkflowAction action,
                             final ModifyOnce<String> appPath,
                             final ModifyOnce<Boolean> propagateConfiguration,
                             final Map<String, ModifyOnce<String>> configuration) {
        super(action);

        this.appPath = appPath;
        this.propagateConfiguration = propagateConfiguration;
        this.configuration = configuration;
    }

    public SubWorkflowActionBuilder withAppPath(final String appPath) {
        this.appPath.set(appPath);
        return this;
    }

    public SubWorkflowActionBuilder withPropagatingConfiguration() {
        this.propagateConfiguration.set(true);
        return this;
    }

    public SubWorkflowActionBuilder withoutPropagatingConfiguration() {
        this.propagateConfiguration.set(false);
        return this;
    }

    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public SubWorkflowActionBuilder withConfigProperty(final String key, final String value) {
        ModifyOnce<String> mappedValue = this.configuration.get(key);

        if (mappedValue == null) {
            mappedValue = new ModifyOnce<>(value);
            this.configuration.put(key, mappedValue);
        }

        mappedValue.set(value);

        return this;
    }

    @Override
    public SubWorkflowAction build() {
        final Node.ConstructionData constructionData = getConstructionData();

        final SubWorkflowAction instance = new SubWorkflowAction(
                constructionData,
                appPath.get(),
                propagateConfiguration.get(),
                ActionAttributesBuilder.convertToConfigurationMap(configuration));

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected SubWorkflowActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
