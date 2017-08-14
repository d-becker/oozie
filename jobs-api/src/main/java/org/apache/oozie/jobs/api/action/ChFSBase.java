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

public class ChFSBase {
    private final boolean recursive;
    private final String path;
    private final String dirFiles;

    ChFSBase(final ConstructionData constructionData) {
        this.recursive = constructionData.recursive;
        this.path = constructionData.path;
        this.dirFiles = constructionData.dirFiles;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public String getPath() {
        return path;
    }

    public String getDirFiles() {
        return dirFiles;
    }

    public static class ConstructionData {
        private final boolean recursive;
        private final String path;
        private final String dirFiles;

        public ConstructionData(final boolean recursive,
                                final String path,
                                final String dirFiles) {
            this.recursive = recursive;
            this.path = path;
            this.dirFiles = dirFiles;
        }
    }
}
