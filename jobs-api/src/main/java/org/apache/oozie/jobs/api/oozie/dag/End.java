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

import java.util.Arrays;
import java.util.List;

public class End extends NodeBase {
    private NodeBase parent;

    public End(final String name) {
        super(name);
    }

    public NodeBase getParent() {
        return parent;
    }

    @Override
    public void addParent(final NodeBase parent) {
        if (this.parent != null) {
            throw new IllegalStateException("End nodes cannot have multiple parents.");
        }

        this.parent = parent;
        parent.addChild(this);
    }

    @Override
    public void addParentWithCondition(Decision parent, String condition) {
        if (this.parent != null) {
            throw new IllegalStateException("End nodes cannot have multiple parents.");
        }

        this.parent = parent;
        parent.addChildWithCondition(this, condition);
    }

    @Override
    public void addParentDefaultConditional(Decision parent) {
        if (this.parent != null) {
            throw new IllegalStateException("End nodes cannot have multiple parents.");
        }

        this.parent = parent;
        parent.addDefaultChild(this);
    }

    @Override
    public void removeParent(final NodeBase parent) {
        if (this.parent != parent) {
            throw new IllegalArgumentException("Trying to remove a nonexistent parent.");
        }

        if (this.parent != null) {
            this.parent.removeChild(this);
        }

        this.parent = null;
    }

    @Override
    public void clearParents() {
        removeParent(parent);
    }

    @Override
    public List<NodeBase> getChildren() {
        return Arrays.asList();
    }

    @Override
    protected void addChild(final NodeBase child) {
        throw new IllegalStateException("End nodes cannot have children.");
    }

    @Override
    protected void removeChild(final NodeBase child) {
        throw new IllegalStateException("End nodes cannot have children.");
    }
}
