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

public class PrepareBuilder {
    private final ImmutableList.Builder<Delete> deletes;
    private final ImmutableList.Builder<Mkdir> mkdirs;

    public PrepareBuilder() {
        deletes = new ImmutableList.Builder<>();
        mkdirs = new ImmutableList.Builder<>();
    }

    public PrepareBuilder withDelete(String path) {
        return withDelete(path, null);
    }

    public PrepareBuilder withDelete(String path, Boolean skipTrash) {
        deletes.add(new Delete(path, skipTrash));
        return this;
    }

    public PrepareBuilder withMkdir(String path) {
        mkdirs.add(new Mkdir(path));
        return this;
    }

    public Prepare build() {
        return new Prepare(deletes.build(), mkdirs.build());
    }
}
