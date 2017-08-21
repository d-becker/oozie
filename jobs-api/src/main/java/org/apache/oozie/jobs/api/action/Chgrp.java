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
 * A class representing the chgrp operation of {@link FSAction}.
 */
public class Chgrp extends ChFSBase {
    private String group;

    Chgrp(final ChFSBase.ConstructionData constructionData,
          final String group) {
        super(constructionData);
        this.group = group;
    }


    /**
     * Returns the new group that will be set by the operation.
     * @return The new group that will be set by the operation.
     */
    public String getGroup() {
        return group;
    }
}
