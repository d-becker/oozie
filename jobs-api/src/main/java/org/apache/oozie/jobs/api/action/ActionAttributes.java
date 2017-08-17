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

public class ActionAttributes {
    private final String jobTracker;
    private final String nameNode;
    private final Prepare prepare;
    private final Streaming streaming;
    private final Pipes pipes;
    private final ImmutableList<String> jobXmls;
    private final ImmutableMap<String, String> configuration;
    private final String configClass;
    private final ImmutableList<String> files;
    private final ImmutableList<String> archives;

    private final ImmutableList<Delete> deletes;
    private final ImmutableList<Mkdir> mkdirs;
    private final ImmutableList<Move> moves;
    private final ImmutableList<Chmod> chmods;
    private final ImmutableList<Touchz> touchzs;
    private final ImmutableList<Chgrp> chgrps;
    private final ImmutableList<Setrep> setreps;

    ActionAttributes(final String jobTracker,
                     final String nameNode,
                     final Prepare prepare,
                     final Streaming streaming,
                     final Pipes pipes,
                     final ImmutableList<String> jobXmls,
                     final ImmutableMap<String, String> configuration,
                     final String configClass,
                     final ImmutableList<String> files,
                     final ImmutableList<String> archives,

                     final ImmutableList<Delete> deletes,
                     final ImmutableList<Mkdir> mkdirs,
                     final ImmutableList<Move> moves,
                     final ImmutableList<Chmod> chmods,
                     final ImmutableList<Touchz> touchzs,
                     final ImmutableList<Chgrp> chgrps,
                     final ImmutableList<Setrep> setreps) {
        this.jobTracker = jobTracker;
        this.nameNode = nameNode;
        this.prepare = prepare;
        this.streaming = streaming;
        this.pipes = pipes;
        this.jobXmls = jobXmls;
        this.configuration = configuration;
        this.configClass = configClass;
        this.files = files;
        this.archives = archives;

        this.deletes = deletes;
        this.mkdirs = mkdirs;
        this.moves = moves;
        this.chmods = chmods;
        this.touchzs = touchzs;
        this.chgrps = chgrps;
        this.setreps = setreps;
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

    public Streaming getStreaming() {
        return streaming;
    }

    public Pipes getPipes() {
        return pipes;
    }

    public ImmutableList<String> getJobXmls() {
        return jobXmls;
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

    public ImmutableList<Delete> getDeletes() {
        return deletes;
    }

    public ImmutableList<Mkdir> getMkdirs() {
        return mkdirs;
    }

    public ImmutableList<Move> getMoves() {
        return moves;
    }

    public ImmutableList<Chmod> getChmods() {
        return chmods;
    }

    public ImmutableList<Touchz> getTouchzs() {
        return touchzs;
    }

    public ImmutableList<Chgrp> getChgrps() {
        return chgrps;
    }

    public ImmutableList<Setrep> getSetreps() {
        return setreps;
    }
}
