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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ActionBuilderBaseImpl<B extends ActionBuilderBaseImpl<B>>
        extends NodeBuilderBaseImpl<B> {
    private final ConfigurationHandlerBuilder configurationHandlerBuilder;

    ActionBuilderBaseImpl() {
        super();

        configurationHandlerBuilder = new ConfigurationHandlerBuilder();
    }

    ActionBuilderBaseImpl(final Action action) {
        super(action);

        configurationHandlerBuilder = new ConfigurationHandlerBuilder(action.getConfiguration());
    }

    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public B withConfigProperty(final String key, final String value) {
        configurationHandlerBuilder.withConfigProperty(key, value);
        return ensureRuntimeSelfReference();
    }

    Action.ConstructionData getConstructionData() {
        final String nameStr = this.name.get();

        final ImmutableList<Node> parentsList = new ImmutableList.Builder<Node>().addAll(parents).build();
        final ImmutableList<Node.NodeWithCondition> parentsWithConditionsList
                = new ImmutableList.Builder<Node.NodeWithCondition>().addAll(parentsWithConditions).build();

        final ConfigurationHandler configurationHandler = configurationHandlerBuilder.build();

        return new Action.ConstructionData(
                nameStr,
                parentsList,
                parentsWithConditionsList,
                configurationHandler,
                errorHandler.get()
        );
    }
}