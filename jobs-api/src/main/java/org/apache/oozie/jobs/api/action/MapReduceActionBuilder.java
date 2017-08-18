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

public class MapReduceActionBuilder extends ActionBuilderBaseImpl<MapReduceActionBuilder> implements Builder<MapReduceAction> {
    private final ActionAttributesBuilder attributesBuilder;

    public static MapReduceActionBuilder create() {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.create();

        return new MapReduceActionBuilder(
                null,
                builder);
    }

    public static MapReduceActionBuilder createFromExistingAction(final MapReduceAction action) {
        final ActionAttributesBuilder builder = ActionAttributesBuilder.createFromExisting(action.getAttributes());

        return new MapReduceActionBuilder(
                action,
                builder);
    }

    public MapReduceActionBuilder(final MapReduceAction action,
                                  final ActionAttributesBuilder attributesBuilder) {
        super(action);

        this.attributesBuilder = attributesBuilder;
    }

    public MapReduceActionBuilder withJobTracker(final String jobTracker) {
        attributesBuilder.withJobTracker(jobTracker);
        return this;
    }

    public MapReduceActionBuilder withNameNode(final String nameNode) {
        attributesBuilder.withNameNode(nameNode);
        return this;
    }

    public MapReduceActionBuilder withPrepare(final Prepare prepare) {
        attributesBuilder.withPrepare(prepare);
        return this;
    }

    public MapReduceActionBuilder withStreaming(final Streaming streaming) {
        attributesBuilder.withStreaming(streaming);
        return this;
    }

    public MapReduceActionBuilder withPipes(final Pipes pipes) {
        attributesBuilder.withPipes(pipes);
        return this;
    }

    public MapReduceActionBuilder withJobXml(final String jobXml) {
        attributesBuilder.withJobXml(jobXml);
        return this;
    }

    public MapReduceActionBuilder withoutJobXml(final String jobXml) {
        attributesBuilder.withoutJobXml(jobXml);
        return this;
    }

    public MapReduceActionBuilder clearJobXmls() {
        attributesBuilder.clearJobXmls();
        return this;
    }

    /**
     * Setting a key to null means deleting it.
     * @param key
     * @param value
     * @return
     */
    public MapReduceActionBuilder withConfigProperty(final String key, final String value) {
        attributesBuilder.withConfigProperty(key, value);
        return this;
    }

    public MapReduceActionBuilder withConfigClass(final String configClass) {
        attributesBuilder.withConfigClass(configClass);
        return this;
    }

    public MapReduceActionBuilder withFile(final String file) {
        attributesBuilder.withFile(file);
        return this;
    }

    public MapReduceActionBuilder withoutFile(final String file) {
        attributesBuilder.withoutFile(file);
        return this;
    }

    public MapReduceActionBuilder clearFiles() {
        attributesBuilder.clearFiles();
        return this;
    }

    public MapReduceActionBuilder withArchive(final String archive) {
        attributesBuilder.withArchive(archive);
        return this;
    }

    public MapReduceActionBuilder withoutArchive(final String archive) {
        attributesBuilder.withoutArchive(archive);
        return this;
    }

    public MapReduceActionBuilder clearArchives() {
        attributesBuilder.clearArchives();
        return this;
    }

    @Override
    public MapReduceAction build() {
        final Action.ConstructionData constructionData = getConstructionData();

        final MapReduceAction instance = new MapReduceAction(
                constructionData,
                attributesBuilder.build());

        addAsChildToAllParents(instance);

        return instance;
    }

    @Override
    protected MapReduceActionBuilder getRuntimeSelfReference() {
        return this;
    }
}