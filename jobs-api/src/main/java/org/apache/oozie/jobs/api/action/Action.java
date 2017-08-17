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

import java.util.Map;

public abstract class Action extends Node {

    // TODO: Remove this field as not all actions should have it.
    private final ConfigurationHandler configurationHandler;

    Action(final ConstructionData data) {
        super(data.name, data.parents, data.parentsWithConditions, data.errorHandler);
        this.configurationHandler = data.configurationHandler;
    }


    String getConfigProperty(final String property) {
        return configurationHandler.getConfigProperty(property);
    }

    public Map<String, String> getConfiguration() {
        return configurationHandler.getConfiguration();
    }

    static class ConstructionData {

        ConstructionData(final String name,
                         final ImmutableList<Node> parents,
                         final ImmutableList<Node.NodeWithCondition> parentsWithConditions,
                         final ConfigurationHandler configurationHandler,
                         final ErrorHandler errorHandler) {
            this.name = name;
            this.parents = parents;
            this.parentsWithConditions = parentsWithConditions;
            this.configurationHandler = configurationHandler;
            this.errorHandler = errorHandler;
        }

        private final String name;
        private final ImmutableList<Node> parents;
        private final ImmutableList<Node.NodeWithCondition> parentsWithConditions;
        private final ConfigurationHandler configurationHandler;
        private final ErrorHandler errorHandler;
    }
}
