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

public class SubWorkflowActionBuilder
        extends ActionBuilderBaseImpl<SubWorkflowActionBuilder> implements Builder<SubWorkflowAction> {
    private final ModifyOnce<String> appPath;
    private final ModifyOnce<Boolean> propagateConfiguration;

    public SubWorkflowActionBuilder() {
        appPath = new ModifyOnce<>();
        propagateConfiguration = new ModifyOnce<>(false);
    }

    public SubWorkflowActionBuilder(final SubWorkflowAction action) {
        super(action);
        appPath = new ModifyOnce<>(action.getAppPath());
        propagateConfiguration = new ModifyOnce<>(action.isPropagatingConfiguration());
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

    @Override
    public SubWorkflowAction build() {
        final Action.ConstructionData constructionData = getConstructionData();

        final SubWorkflowAction instance = new SubWorkflowAction(constructionData, appPath.get(), propagateConfiguration.get());

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected SubWorkflowActionBuilder getRuntimeSelfReference() {
        return this;
    }
}
