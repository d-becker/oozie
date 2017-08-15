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

package org.apache.oozie.jobs.api.oozie.dag;

import java.util.List;

public abstract class NodeBase {
    private final String name;

    protected NodeBase(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void addParent(final NodeBase parent);

    public abstract void addParentWithCondition(final Decision parent, final String condition);

    public abstract void addParentDefaultConditional(final Decision parent);

    public abstract void removeParent(final NodeBase parent);

    public abstract void clearParents();

    public abstract List<NodeBase> getChildren();

    protected abstract void addChild(final NodeBase child);

    protected abstract void removeChild(final NodeBase child);
}
