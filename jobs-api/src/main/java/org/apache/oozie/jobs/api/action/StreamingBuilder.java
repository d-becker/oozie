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

public class StreamingBuilder {
    private final ModifyOnce<String> mapper;
    private final ModifyOnce<String> reducer;
    private final ModifyOnce<String> recordReader;
    private final ImmutableList.Builder<String> recordReaderMappings;
    private final ImmutableList.Builder<String> envs;

    public StreamingBuilder() {
        mapper = new ModifyOnce<>();
        reducer = new ModifyOnce<>();
        recordReader = new ModifyOnce<>();

        recordReaderMappings = new ImmutableList.Builder<>();
        envs = new ImmutableList.Builder<>();
    }

    public StreamingBuilder withMapper(final String mapper) {
        this.mapper.set(mapper);
        return this;
    }

    public StreamingBuilder withReducer(final String reducer) {
        this.reducer.set(reducer);
        return this;
    }

    public StreamingBuilder withRecordReader(final String recordReader) {
        this.recordReader.set(recordReader);
        return this;
    }

    public StreamingBuilder withRecordReaderMapping(final String recordReaderMapping) {
        this.recordReaderMappings.add(recordReaderMapping);
        return this;
    }

    public StreamingBuilder withEnv(final String env) {
        this.envs.add(env);
        return this;
    }

    public Streaming build() {
        return new Streaming(mapper.get(), reducer.get(), recordReader.get(), recordReaderMappings.build(), envs.build());
    }
}
