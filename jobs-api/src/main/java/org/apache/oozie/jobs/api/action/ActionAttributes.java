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
    private final String javaOpts;
    private final ImmutableList<String> args;
    private final String resourceManager;
    private final Launcher launcher;
    private final Boolean captureOutput;

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
                     final String javaOpts,
                     final ImmutableList<String> args,
                     final String resourceManager,
                     final Launcher launcher,
                     final Boolean captureOutput) {
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
        this.javaOpts = javaOpts;
        this.args = args;
        this.resourceManager = resourceManager;
        this.launcher = launcher;
        this.captureOutput = captureOutput;
    }

    /**
     * Returns the job tracker name.
     * @return The job tracker name.
     */
    public String getJobTracker() {
        return jobTracker;
    }

    /**
     * Returns the name node stored in this {@link ActionAttributes} object.
     * @return The name node stored in this {@link ActionAttributes} object.
     */
    public String getNameNode() {
        return nameNode;
    }

    /**
     * Returns the {@link Prepare} object stored in this {@link ActionAttributes} object.
     * @return The {@link Prepare} object stored in this {@link ActionAttributes} object.
     */
    public Prepare getPrepare() {
        return prepare;
    }

    /**
     * Returns the {@link Streaming} object stored in this {@link ActionAttributes} object.
     * @return The {@link Streaming} object stored in this {@link ActionAttributes} object.
     */
    public Streaming getStreaming() {
        return streaming;
    }

    /**
     * Returns the {@link Pipes} object stored in this {@link ActionAttributes} object.
     * @return The {@link Pipes} object stored in this {@link ActionAttributes} object.
     */
    public Pipes getPipes() {
        return pipes;
    }

    /**
     * Returns the list of job XMLs stored in this {@link ActionAttributes} object.
     * @return The list of job XMLs stored in this {@link ActionAttributes} object.
     */
    public ImmutableList<String> getJobXmls() {
        return jobXmls;
    }

    /**
     * Returns an immutable map of the configuration key-value pairs stored in this {@link ActionAttributes} object.
     * @return An immutable map of the configuration key-value pairs stored in this {@link ActionAttributes} object.
     */
    public ImmutableMap<String, String> getConfiguration() {
        return configuration;
    }

    /**
     * Returns the configuration class property of this {@link ActionAttributes} object.
     * @return The configuration class property of this {@link ActionAttributes} object.
     */
    public String getConfigClass() {
        return configClass;
    }

    /**
     * Returns an immutable list of the names of the files associated with this {@link ActionAttributes} object.
     * @return An immutable list of the names of the files associated with this {@link ActionAttributes} object.
     */
    public ImmutableList<String> getFiles() {
        return files;
    }

    /**
     * Returns an immutable list of the names of the archives associated with this {@link ActionAttributes} object.
     * @return An immutable list of the names of the archives associated with this {@link ActionAttributes} object.
     */
    public ImmutableList<String> getArchives() {
        return archives;
    }

    /**
     * Returns an immutable list of the {@link Delete} objects stored in this {@link ActionAttributes} object.
     * @return An immutable list of the {@link Delete} objects stored in this {@link ActionAttributes} object.
     */
    public ImmutableList<Delete> getDeletes() {
        return deletes;
    }

    /**
     * Returns an immutable list of the {@link Mkdir} objects stored in this {@link ActionAttributes} object.
     * @return An immutable list of the {@link Mkdir} objects stored in this {@link ActionAttributes} object.
     */
    public ImmutableList<Mkdir> getMkdirs() {
        return mkdirs;
    }

    /**
     * Returns an immutable list of the {@link Move} objects stored in this {@link ActionAttributes} object.
     * @return An immutable list of the {@link Move} objects stored in this {@link ActionAttributes} object.
     */
    public ImmutableList<Move> getMoves() {
        return moves;
    }

    /**
     * Returns an immutable list of the {@link Chmod} objects stored in this {@link ActionAttributes} object.
     * @return An immutable list of the {@link Chmod} objects stored in this {@link ActionAttributes} object.
     */
    public ImmutableList<Chmod> getChmods() {
        return chmods;
    }

    /**
     * Returns an immutable list of the {@link Touchz} objects stored in this {@link ActionAttributes} object.
     * @return An immutable list of the {@link Touchz} objects stored in this {@link ActionAttributes} object.
     */
    public ImmutableList<Touchz> getTouchzs() {
        return touchzs;
    }

    /**
     * Returns an immutable list of the {@link Chgrp} objects stored in this {@link ActionAttributes} object.
     * @return An immutable list of the {@link Delete} objects stored in this {@link ActionAttributes} object.
     */
    public ImmutableList<Chgrp> getChgrps() {
        return chgrps;
    }

    public String getJavaOpts() {
        return javaOpts;
    }

    public ImmutableList<String> getArgs() {
        return args;
    }

    public String getResourceManager() {
        return resourceManager;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public Boolean isCaptureOutput() {
        return captureOutput;
    }
}
