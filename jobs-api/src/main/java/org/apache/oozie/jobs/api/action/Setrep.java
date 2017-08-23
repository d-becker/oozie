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

/**
 * A class representing the setrep command of {@link FSAction}.
 */
public class Setrep {
    private final String path;
    private final short replicationFactor;

    public Setrep(final String path,
                  final short replicationFactor) {
        this.path = path;
        this.replicationFactor = replicationFactor;
    }

    /**
     * Returns the path of the file for which the replication factor will be set when running this command.
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the replication factor that will be set when running this command.
     * @return
     */
    public short getReplicationFactor() {
        return replicationFactor;
    }
}
