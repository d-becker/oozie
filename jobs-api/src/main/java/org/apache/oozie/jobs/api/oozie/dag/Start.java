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

public class Start extends NodeBase {
    private NodeBase child;

    Start(final String name) {
        super(name);
    }

    @Override
    public void addParent(final NodeBase parent) {
        throw new IllegalStateException("Start nodes cannot have parents.");
    }

    @Override
    public void addParentWithCondition(final Decision parent, final String condition) {
        throw new IllegalStateException("Start nodes cannot have parents.");
    }

    @Override
    public void removeParent(final NodeBase parent) {
        throw new IllegalStateException("Start nodes cannot have parents.");
    }

    public NodeBase getChild() {
        return child;
    }

    @Override
    public List<NodeBase> getChildren() {
        if (child == null) {
            return Arrays.asList();
        } else {
            return Arrays.asList(child);
        }
    }

    @Override
    protected void addChild(final NodeBase child) {
        if (this.child != null) {
            throw new IllegalStateException("Start nodes cannot have multiple children.");
        }

        this.child =  child;
    }

    @Override
    protected void removeChild(final NodeBase child) {
        if (this.child != child) {
            throw new IllegalArgumentException("Trying to remove a nonexistent child.");
        }

        this.child = null;
    }
}
