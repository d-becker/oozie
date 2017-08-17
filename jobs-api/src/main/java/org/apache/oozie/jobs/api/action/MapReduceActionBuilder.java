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
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public class MapReduceActionBuilder extends ActionBuilderBaseImpl<MapReduceActionBuilder> implements Builder<MapReduceAction> {
    private final ModifyOnce<String> jobTracker;
    private final ModifyOnce<String> nameNode;
    private final ModifyOnce<Prepare> prepare;
    private final ModifyOnce<Streaming> streaming;
    private final ModifyOnce<Pipes> pipes;
    private final List<String> jobXmls;
    private final ConfigurationHandlerBuilder configurationHandlerBuilder;
    private final ModifyOnce<String> configClass;
    private final List<String> files;
    private final List<String> archives;

    public static MapReduceActionBuilder create() {
        final ModifyOnce<String> jobTracker = new ModifyOnce<>();
        final ModifyOnce<String> nameNode = new ModifyOnce<>();
        final ModifyOnce<Prepare> prepare = new ModifyOnce<>();
        final ModifyOnce<Streaming> streaming = new ModifyOnce<>();
        final ModifyOnce<Pipes> pipes = new ModifyOnce<>();
        final List<String> jobXmls = new ArrayList<>();
        final ConfigurationHandlerBuilder configurationHandlerBuilder = new ConfigurationHandlerBuilder();
        final ModifyOnce<String> configClass = new ModifyOnce<>();
        final List<String> files = new ArrayList<>();
        final List<String> archives = new ArrayList<>();

        return new MapReduceActionBuilder(
                null,
                jobTracker,
                nameNode,
                prepare,
                streaming,
                pipes,
                jobXmls,
                configurationHandlerBuilder,
                configClass,
                files,
                archives);
    }

    public static MapReduceActionBuilder createFromExistingAction(final MapReduceAction action) {
        final ModifyOnce<String> jobTracker = new ModifyOnce<>(action.getJobTracker());
        final ModifyOnce<String> nameNode = new ModifyOnce<>(action.getNameNode());
        final ModifyOnce<Prepare> prepare = new ModifyOnce<>(action.getPrepare());
        final ModifyOnce<Streaming> streaming = new ModifyOnce<>(action.getStreaming());
        final ModifyOnce<Pipes> pipes = new ModifyOnce<>(action.getPipes());
        final List<String> jobXmls = new ArrayList<>(action.getJobXmls());
        final ConfigurationHandlerBuilder configurationHandlerBuilder = new ConfigurationHandlerBuilder(action.getConfiguration());
        final ModifyOnce<String> configClass = new ModifyOnce<>(action.getConfigClass());
        final List<String> files = new ArrayList<>(action.getFiles());
        final List<String> archives = new ArrayList<>(action.getArchives());

        return new MapReduceActionBuilder(
                action,
                jobTracker,
                nameNode,
                prepare,
                streaming,
                pipes,
                jobXmls,
                configurationHandlerBuilder,
                configClass,
                files,
                archives);
    }

    public MapReduceActionBuilder(final MapReduceAction action,
                                  final ModifyOnce<String> jobTracker,
                                  final ModifyOnce<String> nameNode,
                                  final ModifyOnce<Prepare> prepare,
                                  final ModifyOnce<Streaming> streaming,
                                  final ModifyOnce<Pipes> pipes,
                                  final List<String> jobXmls,
                                  final ConfigurationHandlerBuilder configurationHandlerBuilder,
                                  final ModifyOnce<String> configClass,
                                  final List<String> files,
                                  final List<String> archives) {
        super(action);

        this.jobTracker = jobTracker;
        this.nameNode = nameNode;
        this.prepare = prepare;
        this.streaming = streaming;
        this.pipes = pipes;
        this.jobXmls = jobXmls;
        this.configurationHandlerBuilder = configurationHandlerBuilder;
        this.configClass = configClass;
        this.files = files;
        this.archives = archives;
    }

    public MapReduceActionBuilder withJobTracker(final String jobTracker) {
        this.jobTracker.set(jobTracker);
        return this;
    }

    public MapReduceActionBuilder withNameNode(final String nameNode) {
        this.nameNode.set(nameNode);
        return this;
    }

    public MapReduceActionBuilder withPrepare(final Prepare prepare) {
        this.prepare.set(prepare);
        return this;
    }

    public MapReduceActionBuilder withStreaming(final Streaming streaming) {
        this.streaming.set(streaming);
        return this;
    }

    public MapReduceActionBuilder withPipes(final Pipes pipes) {
        this.pipes.set(pipes);
        return this;
    }

    public MapReduceActionBuilder withJobXml(final String jobXml) {
        this.jobXmls.add(jobXml);
        return this;
    }

    public MapReduceActionBuilder withoutJobXml(final String jobXml) {
        jobXmls.remove(jobXml);
        return this;
    }

    public MapReduceActionBuilder clearJobXmls() {
        jobXmls.clear();
        return this;
    }

    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public MapReduceActionBuilder withConfigProperty(final String key, final String value) {
        configurationHandlerBuilder.withConfigProperty(key, value);
        return this;
    }

    public MapReduceActionBuilder withConfigClass(final String configClass) {
        this.configClass.set(configClass);
        return this;
    }

    public MapReduceActionBuilder withFile(final String file) {
        this.files.add(file);
        return this;
    }

    public MapReduceActionBuilder withoutFile(final String file) {
        files.remove(file);
        return this;
    }

    public MapReduceActionBuilder clearFiles() {
        files.clear();
        return this;
    }

    public MapReduceActionBuilder withArchive(final String archive) {
        this.archives.add(archive);
        return this;
    }

    public MapReduceActionBuilder withoutArchive(final String archive) {
        archives.remove(archive);
        return this;
    }

    public MapReduceActionBuilder clearArchives() {
        archives.clear();
        return this;
    }

    @Override
    public MapReduceAction build() {
        final Action.ConstructionData constructionData = getConstructionData();
        final String jobTrackerStr = this.jobTracker.get();
        final String nameNodeStr = this.nameNode.get();
        final Prepare prepareActual = this.prepare.get();
        final Streaming streamingActual = this.streaming.get();
        final Pipes pipesActual = this.pipes.get();
        final ImmutableList<String> jobXmlsList = new ImmutableList.Builder<String>().addAll(this.jobXmls).build();
        final ConfigurationHandler configurationHandler = configurationHandlerBuilder.build();
        final String configClassStr = this.configClass.get();
        final ImmutableList<String> filesList = new ImmutableList.Builder<String>().addAll(this.files).build();
        final ImmutableList<String> archivesList = new ImmutableList.Builder<String>().addAll(this.archives).build();

        final MapReduceAction instance = new MapReduceAction(
                constructionData,
                jobTrackerStr,
                nameNodeStr,
                prepareActual,
                streamingActual,
                pipesActual,
                jobXmlsList,
                configurationHandler,
                configClassStr,
                filesList,
                archivesList);

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected MapReduceActionBuilder getRuntimeSelfReference() {
        return this;
    }
}