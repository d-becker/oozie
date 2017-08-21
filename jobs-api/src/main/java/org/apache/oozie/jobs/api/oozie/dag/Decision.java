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

import com.google.common.base.Preconditions;
import org.apache.oozie.jobs.api.Condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Decision extends NodeBase {
    private NodeBase parent;
    private final List<DagNodeWithCondition> childrenWithConditions;
    private NodeBase defaultChild;

    public Decision(final String name) {
        super(name);
        this.parent = null;
        this.childrenWithConditions = new ArrayList<>();
    }

    public NodeBase getParent() {
        return parent;
    }

    @Override
    public void addParent(final NodeBase parent) {
        Preconditions.checkState(this.parent == null, "Decision nodes cannot have multiple parents.");

        this.parent = parent;
        this.parent.addChild(this);
    }

    @Override
    public void addParentWithCondition(final Decision parent, final Condition condition) {
        Preconditions.checkState(this.parent == null, "Decision nodes cannot have multiple parents.");

        this.parent = parent;
        parent.addChildWithCondition(this, condition);
    }

    @Override
    public void addParentDefaultConditional(final Decision parent) {
        Preconditions.checkState(this.parent == null, "Decision nodes cannot have multiple parents.");

        this.parent = parent;
        parent.addDefaultChild(this);
    }

    @Override
    public void removeParent(final NodeBase parent) {
        Preconditions.checkArgument(this.parent == parent, "Trying to remove a nonexistent parent.");

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
        final List<NodeBase> results = new ArrayList<>();

        for (final DagNodeWithCondition nodeWithCondition : getChildrenWithConditions()) {
            results.add(nodeWithCondition.getNode());
        }

        return Collections.unmodifiableList(results);
    }

    public List<DagNodeWithCondition> getChildrenWithConditions() {
        final List<DagNodeWithCondition> results = new ArrayList<>(childrenWithConditions);

        if (defaultChild != null) {
            results.add(new DagNodeWithCondition(defaultChild, Condition.defaultCondition()));
        }

        return Collections.unmodifiableList(results);
    }

    public NodeBase getDefaultChild() {
        return defaultChild;
    }

    @Override
    protected void addChild(final NodeBase child) {
        throw new IllegalStateException("Decision nodes cannot have normal children.");
    }

    void addChildWithCondition(final NodeBase child, final Condition condition) {
        if (condition.isDefault()) {
            addDefaultChild(child);
        }
        else {
            this.childrenWithConditions.add(new DagNodeWithCondition(child, condition));
        }
    }

    void addDefaultChild(final NodeBase child) {
        Preconditions.checkState(defaultChild == null, "Trying to add a default child to a Decision node that already has one.");

        defaultChild = child;
    }

    @Override
    protected void removeChild(final NodeBase child) {
        if (defaultChild == child) {
            defaultChild = null;
        }
        else {
            final int index = indexOfNodeBaseInChildrenWithConditions(child);

            Preconditions.checkArgument(index >= 0, "Trying to remove a nonexistent child.");

            this.childrenWithConditions.remove(index);
        }
    }

    private int indexOfNodeBaseInChildrenWithConditions(final NodeBase child) {
        for (int i = 0; i < this.childrenWithConditions.size(); ++i) {
            if (child == this.childrenWithConditions.get(i).getNode()) {
                return i;
            }
        }

        return -1;
    }
}
