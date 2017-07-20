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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapReduceActionBuilder {
    private final ModifyOnce<String> name;
    private final List<MapReduceAction> parents;

    private final ModifyOnce<String> jobTracker;
    private final ModifyOnce<String> nameNode;
    private final ModifyOnce<Prepare> prepare;
    // private STREAMING streaming;
    // private PIPES pipes;
    private final List<String> jobXmls;
    private final Map<String, ModifyOnce<String>> configuration;
    private final ModifyOnce<String> configClass;
    private final List<String> files;
    private final List<String> archives;

    public MapReduceActionBuilder() {
        parents = new ArrayList<>();
        name = new ModifyOnce<>();
        jobTracker = new ModifyOnce<>();
        nameNode = new ModifyOnce<>();
        prepare = new ModifyOnce<>();
        jobXmls = new ArrayList<>();
        configuration = new LinkedHashMap<>();
        configClass = new ModifyOnce<>();
        files = new ArrayList<>();
        archives = new ArrayList<>();
    }

    public MapReduceActionBuilder(MapReduceAction action) {
        parents = new ArrayList<>(action.getParents());
        name = new ModifyOnce<>(action.getName());
        jobTracker = new ModifyOnce<>(action.getJobTracker());
        nameNode = new ModifyOnce<>(action.getNameNode());
        prepare = new ModifyOnce<>(action.getPrepare());
        jobXmls = new ArrayList<>(action.getJobXmls());
        configuration = immutableConfigurationMapToModifyOnce(action.getConfiguration());
        configClass = new ModifyOnce<>(action.getConfigClass());
        files = new ArrayList<>(action.getFiles());
        archives = new ArrayList<>(action.getArchives());
    }

    public MapReduceActionBuilder withParent(MapReduceAction action) {
        parents.add(action);
        return this;
    }

    public MapReduceActionBuilder removeParent(MapReduceAction parent) {
        parents.remove(parent);
        return this;
    }

    public MapReduceActionBuilder clearParents() {
        parents.clear();
        return this;
    }

    public MapReduceActionBuilder withName(String name) {
        this.name.set(name);
        return this;
    }

    public MapReduceActionBuilder withJobTracker(String jobTracker) {
        this.jobTracker.set(jobTracker);
        return this;
    }

    public MapReduceActionBuilder withNameNode(String nameNode) {
        this.nameNode.set(nameNode);
        return this;
    }

    public MapReduceActionBuilder withPrepare(Prepare prepare) {
        this.prepare.set(prepare);
        return this;
    }

    public MapReduceActionBuilder withJobXml(String jobXml) {
        this.jobXmls.add(jobXml);
        return this;
    }

    public MapReduceActionBuilder removeJobXml(String jobXml) {
        jobXmls.remove(jobXml);
        return this;
    }

    public MapReduceActionBuilder clearJobXmls() {
        jobXmls.clear();
        return this;
    }

    public MapReduceActionBuilder withConfigProperty(String key, String value) {
        ModifyOnce<String> mappedValue = this.configuration.get(key);

        if (mappedValue == null) {
            mappedValue = new ModifyOnce<>(value);
            this.configuration.put(key, mappedValue);
        }

        mappedValue.set(value);
        return this;
    }

    public MapReduceActionBuilder withConfigClass(String configClass) {
        this.configClass.set(configClass);
        return this;
    }

    public MapReduceActionBuilder withFile(String file) {
        this.files.add(file);
        return this;
    }

    public MapReduceActionBuilder removeFile(String file) {
        files.remove(file);
        return this;
    }

    public MapReduceActionBuilder clearFiles() {
        files.clear();
        return this;
    }

    public MapReduceActionBuilder withArchive(String archive) {
        this.archives.add(archive);
        return this;
    }

    public MapReduceActionBuilder removeArchive(String archive) {
        archives.remove(archive);
        return this;
    }

    public MapReduceActionBuilder clearArchives() {
        archives.clear();
        return this;
    }

    public MapReduceAction build() {
        final String nameStr = this.name.get();
        final ImmutableList<MapReduceAction> parentsList = new ImmutableList.Builder<MapReduceAction>().addAll(parents).build();
        final String jobTrackerStr = this.jobTracker.get();
        final String nameNodeStr = this.nameNode.get();
        final Prepare prepareStr = this.prepare.get();
        final ImmutableList<String> jobXmlsList = new ImmutableList.Builder<String>().addAll(this.jobXmls).build();
        final ImmutableMap<String, String> configurationMap = modifyOnceConfigurationMapToImmutable(this.configuration);
        final String configClassStr = this.configClass.get();
        final ImmutableList<String> filesList = new ImmutableList.Builder<String>().addAll(this.files).build();
        final ImmutableList<String> archivesList = new ImmutableList.Builder<String>().addAll(this.archives).build();


        MapReduceAction instance = new MapReduceAction(
                nameStr,
                parentsList,
                jobTrackerStr,
                nameNodeStr,
                prepareStr,
                jobXmlsList,
                configurationMap,
                configClassStr,
                filesList,
                archivesList);

        if (parentsList != null) {
            for (MapReduceAction parent : parentsList) {
                parent.addChild(instance);
            }
        }

        return instance;
    }

    private static ImmutableMap<String, String> modifyOnceConfigurationMapToImmutable(Map<String, ModifyOnce<String>> map) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();

        for (Map.Entry<String, ModifyOnce<String>> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().get());
        }

        return builder.build();
    }

    private static Map<String, ModifyOnce<String>> immutableConfigurationMapToModifyOnce(ImmutableMap<String, String> map) {
        Map<String, ModifyOnce<String>> result = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.put(entry.getKey(), new ModifyOnce<>(entry.getValue()));
        }

        return result;
    }

}