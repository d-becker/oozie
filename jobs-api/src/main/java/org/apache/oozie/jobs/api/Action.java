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
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Action {
    private final String name;
    private final ImmutableList<Action> parents;
    private final List<Action> children; // MUTABLE!
    private final ImmutableMap<String, String> configuration;

    Action(final String name,
           final ImmutableList<Action> parents,
           final ImmutableMap<String, String> configuration)
    {
        this.name = name;
        this.parents = parents;
        this.children = new ArrayList<>();
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public List<Action> getParents() {
        return parents;
    }

    public String getConfigProperty(String property) {
        return configuration.get(property);
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    void addChild(Action child) {
        this.children.add(child);
    }

    /**
     * Returns an unmodifiable view of list of the children of this <code>Action</code>.
     * @return An unmodifiable view of list of the children of this <code>Action</code>.
     */
    List<Action> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
