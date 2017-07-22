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

import java.util.List;

public class MapReduceAction extends Action {
    private final String jobTracker;
    private final String nameNode;
    private final Prepare prepare;
    // private final STREAMING streaming;
    // private final PIPES pipes;
    private final ImmutableList<String> jobXmls;
    private final String configClass;
    private final ImmutableList<String> files;
    private final ImmutableList<String> archives;

    MapReduceAction(final Action.ConstructionData constructionData,
                    final String jobTracker,
                    final String nameNode,
                    final Prepare prepare,
                    final ImmutableList<String> jobXmls,
                    final String configClass,
                    final ImmutableList<String> files,
                    final ImmutableList<String> archives)
    {
        super(constructionData);

        this.jobTracker = jobTracker;
        this.nameNode = nameNode;
        this.prepare = prepare;
        // this.streaming = streaming;
        // this.pipes = pipes;
        this.jobXmls = jobXmls;
        this.configClass = configClass;
        this.files = files;
        this.archives = archives;
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

    public List<String> getJobXmls() {
        return jobXmls;
    }

    public String getConfigClass() {
        return configClass;
    }

    public List<String> getFiles() {
        return files;
    }

    public List<String> getArchives() {
        return archives;
    }
}
