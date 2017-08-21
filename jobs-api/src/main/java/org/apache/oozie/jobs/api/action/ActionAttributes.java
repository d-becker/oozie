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

/**
 * An immutable class holding data that is used by several actions. It should be constructed by using an
 * {@link ActionAttributesBuilder}.
 */
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

    /**
     * Returns the job tracker name.
     * @return The job tracker name.
     */
    public String getJobTracker() {
        return jobTracker;
    }

    /**
     * Return the name of the name node.
     * @return The name of the name node.
     */
    public String getNameNode() {
        return nameNode;
    }

    /**
     * Return the {@link Prepare} object stored in this {@link ActionAttributes}.
     * @return The {@link Prepare} object stored in this {@link ActionAttributes}.
     */
    public Prepare getPrepare() {
        return prepare;
    }

    /**
     * Return the {@link Streaming} object stored in this {@link ActionAttributes}.
     * @return The {@link Streaming} object stored in this {@link ActionAttributes}.
     */
    public Streaming getStreaming() {
        return streaming;
    }

    /**
     * Return the {@link Pipes} object stored in this {@link ActionAttributes}.
     * @return The {@link Pipes} object stored in this {@link ActionAttributes}.
     */
    public Pipes getPipes() {
        return pipes;
    }

    /**
     * Return the list of job XMLs stored in this {@link ActionAttributes}.
     * @return The list of job XMLs stored in this {@link ActionAttributes}.
     */
    public ImmutableList<String> getJobXmls() {
        return jobXmls;
    }

    /**
     * Return an immutable map of the configuration key-value pairs stored in this {@link ActionAttributes}.
     * @return An immutable map of the configuration key-value pairs stored in this {@link ActionAttributes}.
     */
    public ImmutableMap<String, String> getConfiguration() {
        return configuration;
    }

    /**
     * Return the configuration class property of this {@link ActionAttributes}.
     * @return The configuration class property of this {@link ActionAttributes}.
     */
    public String getConfigClass() {
        return configClass;
    }

    /**
     * Return an immutable list of the names of the files associated with this {@link ActionAttributes}.
     * @return An immutable list of the names of the files associated with this {@link ActionAttributes}.
     */
    public ImmutableList<String> getFiles() {
        return files;
    }

    /**
     * Return an immutable list of the names of the archives associated with this {@link ActionAttributes}.
     * @return An immutable list of the names of the archives associated with this {@link ActionAttributes}.
     */
    public ImmutableList<String> getArchives() {
        return archives;
    }

    /**
     * Return an immutable list of the {@link Delete} objects stored in this {@link ActionAttributes}.
     * @return An immutable list of the {@link Delete} objects stored in this {@link ActionAttributes}.
     */
    public ImmutableList<Delete> getDeletes() {
        return deletes;
    }

    /**
     * Return an immutable list of the {@link Mkdir} objects stored in this {@link ActionAttributes}.
     * @return An immutable list of the {@link Mkdir} objects stored in this {@link ActionAttributes}.
     */
    public ImmutableList<Mkdir> getMkdirs() {
        return mkdirs;
    }

    /**
     * Return an immutable list of the {@link Move} objects stored in this {@link ActionAttributes}.
     * @return An immutable list of the {@link Move} objects stored in this {@link ActionAttributes}.
     */
    public ImmutableList<Move> getMoves() {
        return moves;
    }

    /**
     * Return an immutable list of the {@link Chmod} objects stored in this {@link ActionAttributes}.
     * @return An immutable list of the {@link Chmod} objects stored in this {@link ActionAttributes}.
     */
    public ImmutableList<Chmod> getChmods() {
        return chmods;
    }

    /**
     * Return an immutable list of the {@link Touchz} objects stored in this {@link ActionAttributes}.
     * @return An immutable list of the {@link Touchz} objects stored in this {@link ActionAttributes}.
     */
    public ImmutableList<Touchz> getTouchzs() {
        return touchzs;
    }

    /**
     * Return an immutable list of the {@link Chgrp} objects stored in this {@link ActionAttributes}.
     * @return An immutable list of the {@link Delete} objects stored in this {@link ActionAttributes}.
     */
    public ImmutableList<Chgrp> getChgrps() {
        return chgrps;
    }

    /**
     * Return an immutable list of the {@link Setrep} objects stored in this {@link ActionAttributes}.
     * @return An immutable list of the {@link Setrep} objects stored in this {@link ActionAttributes}.
     */
    public ImmutableList<Setrep> getSetreps() {
        return setreps;
    }
}
