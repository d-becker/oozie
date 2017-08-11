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

public class Streaming {
    private final String mapper;
    private final String reducer;
    private final String recordReader;
    private final ImmutableList<String> recordReaderMappings;
    private final ImmutableList<String> envs;

    Streaming(final String mapper,
              final String reducer,
              final String recordReader,
              final ImmutableList<String> recordReaderMappings,
              final ImmutableList<String> envs) {
        this.mapper = mapper;
        this.reducer = reducer;
        this.recordReader = recordReader;
        this.recordReaderMappings = recordReaderMappings;
        this.envs = envs;
    }

    public String getMapper() {
        return mapper;
    }

    public String getReducer() {
        return reducer;
    }

    public String getRecordReader() {
        return recordReader;
    }

    public ImmutableList<String> getRecordReaderMappings() {
        return recordReaderMappings;
    }

    public ImmutableList<String> getEnvs() {
        return envs;
    }
}
