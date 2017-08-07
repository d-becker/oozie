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

import org.apache.oozie.jobs.api.action.Node;

import java.util.Arrays;
import java.util.List;

public class ExplicitNode extends NodeBase {
    private NodeBase parent;
    private NodeBase child;
    private final Node realNode;

    ExplicitNode(final String name, final Node realNode) {
        super(name);
        this.realNode = realNode;
    }

    Node getRealNode() {
        return realNode;
    }

    public NodeBase getParent() {
        return parent;
    }

    public NodeBase getChild() {
        return child;
    }

    @Override
    public void addParent(final NodeBase parent) {
        if (this.parent != null) {
            throw new IllegalStateException("A normal node cannot have multiple parents.");
        }

        this.parent = parent;
        this.parent.addChild(this);
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
        if (child == null) {
            return Arrays.asList();
        }

        return Arrays.asList(child);
    }

    @Override
    protected void addChild(final NodeBase child) {
        if (this.child != null) {
            throw new IllegalStateException("Normal nodes cannot have multiple children.");
        }

        this.child = child;
    }

    @Override
    protected void removeChild(final NodeBase child) {
        if (this.child != child) {
            throw new IllegalArgumentException("Trying to remove a nonexistent child.");
        }

        this.child = null;
    }
}
