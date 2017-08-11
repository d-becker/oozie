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

import org.apache.oozie.jobs.api.ModifyOnce;

public class ChmodBuilder {
    private final ModifyOnce<Boolean >recursive;
    private final ModifyOnce<String> path;
    private final ModifyOnce<String> permissions;
    private final ModifyOnce<String> dirFiles;

    public ChmodBuilder() {
        recursive = new ModifyOnce<>(false);
        path = new ModifyOnce<>();
        permissions = new ModifyOnce<>();
        dirFiles = new ModifyOnce<>("true");
    }

    public ChmodBuilder setRecursive() {
        this.recursive.set(true);
        return this;
    }

    public ChmodBuilder setNonRecursive() {
        this.recursive.set(false);
        return this;
    }

    public ChmodBuilder withPath(final String path) {
        this.path.set(path);
        return this;
    }

    public ChmodBuilder withPermissions(final String permissions) {
        this.permissions.set(permissions);
        return this;
    }

    public ChmodBuilder setDirFiles(final boolean dirFiles) {
        this.dirFiles.set(Boolean.toString(dirFiles));
        return this;
    }

    public Chmod build() {
        return new Chmod(recursive.get(), path.get(), permissions.get(), dirFiles.get());
    }
}
