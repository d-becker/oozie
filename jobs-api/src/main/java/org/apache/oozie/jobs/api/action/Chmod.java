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


public class Chmod {
    private final boolean recursive;
    private final String path;
    private final String permissions;
    private final String dirFiles;

    Chmod(final boolean recursive,
          final String path,
          final String permissions,
          final String dirFiles) {
        this.recursive = recursive;
        this.path = path;
        this.permissions = permissions;
        this.dirFiles = dirFiles;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public String getPath() {
        return path;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getDirFiles() {
        return dirFiles;
    }
}
