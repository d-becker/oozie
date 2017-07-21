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

public class MapReduceAction {
    private final String name;
    private final ImmutableList<MapReduceAction> parents;
    private final List<MapReduceAction> children; // MUTABLE!

    private final String jobTracker;
    private final String nameNode;
    private final Prepare prepare;
    // private final STREAMING streaming;
    // private final PIPES pipes;
    private final ImmutableList<String> jobXmls;
    private final ImmutableMap<String, String> configuration;
    private final String configClass;
    private final ImmutableList<String> files;
    private final ImmutableList<String> archives;

    MapReduceAction(String name,
                    ImmutableList<MapReduceAction> parents,
                    String jobTracker,
                    String nameNode,
                    Prepare prepare,
                    ImmutableList<String> jobXmls,
                    ImmutableMap<String, String> configuration,
                    String configClass,
                    ImmutableList<String> files,
                    ImmutableList<String> archives)
    {
        this.name = name;
        this.parents = parents;
        this.children = new ArrayList<>();

        this.jobTracker = jobTracker;
        this.nameNode = nameNode;
        this.prepare = prepare;
        // this.streaming = streaming;
        // this.pipes = pipes;
        this.jobXmls = jobXmls;
        this.configuration = configuration;
        this.configClass = configClass;
        this.files = files;
        this.archives = archives;
    }

    public String getName() {
        return name;
    }

    public ImmutableList<MapReduceAction> getParents() {
        return parents;
    }

    public String getJobTracker() {
        return jobTracker;
    }

    public String getNameNode() {
        return nameNode;
    }

    public Prepare getPrepare() {
        return prepare;
    }

    public ImmutableList<String> getJobXmls() {
        return jobXmls;
    }

    public String getConfigProperty(String property) {
        return configuration.get(property);
    }

    public ImmutableMap<String, String> getConfiguration() {
        return configuration;
    }

    public String getConfigClass() {
        return configClass;
    }

    public ImmutableList<String> getFiles() {
        return files;
    }

    public ImmutableList<String> getArchives() {
        return archives;
    }

    void addChild(MapReduceAction child) {
        this.children.add(child);
    }

    /**
     * Returns an unmodifiable view of list of the children of this <code>MapReduceAction</code>.
     * @return An unmodifiable view of list of the children of this <code>MapReduceAction</code>.
     */
    List<MapReduceAction> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
