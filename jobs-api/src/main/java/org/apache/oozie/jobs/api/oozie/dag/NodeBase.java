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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NodeBase {
    private final List<NodeBase> parentsWithoutCondition;
    private final List<DagNodeWithCondition> parentsWithCondition;
    private final String name;

    public NodeBase(final String name) {
        this.name = name;
        this.parentsWithoutCondition = new ArrayList<>();
        this.parentsWithCondition = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addParent(final NodeBase parent) {
        if (parent != null) {
            parent.addChild(this);
        }

        parentsWithoutCondition.add(parent);
    }

    public void addParentWithCondition(final Decision parent, final String condition) {
        if (parent != null) {
            parent.addChildWithCondition(this, condition);
        }

        parentsWithCondition.add(new DagNodeWithCondition(parent, condition));
    }

    public final List<NodeBase> getAllParents() {
        final List<NodeBase> results = new ArrayList<>(parentsWithoutCondition);

        for (DagNodeWithCondition parentWithCondition : parentsWithCondition) {
            results.add(parentWithCondition.getNode());
        }

        return Collections.unmodifiableList(results);
    }

    public final List<NodeBase> getParentsWithoutCondition() {
        return Collections.unmodifiableList(parentsWithoutCondition);
    }

    public final List<DagNodeWithCondition> getParentsWithCondition() {
        return Collections.unmodifiableList(parentsWithCondition);
    }

    public void removeParent(final NodeBase parent) {
        if (parentsWithoutCondition.remove(parent)) {
            parent.removeChild(this);
            return;
        }

        int index = indexOfNodeBaseInChildrenWithConditions(parentsWithCondition, parent);
        if (index < 0) {
            throw new IllegalArgumentException("Trying to remove a nonexistent parent.");
        }

        parentsWithCondition.remove(index);

        parent.removeChild(this);
    }

    public final  void clearParents() {
        final List<NodeBase> oldParents = getAllParents();
        for (final NodeBase parent : oldParents) {
            removeParent(parent);
        }
    }

    public abstract List<NodeBase> getChildren();

    protected abstract void addChild(final NodeBase child);

    protected abstract void removeChild(final NodeBase child);


    protected int indexOfNodeBaseInChildrenWithConditions(final List<DagNodeWithCondition> nodesWithCondition,
                                                        final NodeBase child) {
        for (int i = 0; i < nodesWithCondition.size(); ++i) {
            if (child == nodesWithCondition.get(i).getNode()) {
                return i;
            }
        }

        return -1;
    }

    public static class DagNodeWithCondition {
        private final NodeBase node;
        private final String condition;

        public DagNodeWithCondition(final NodeBase node,
                                    final String condition) {
            this.node = node;
            this.condition = condition;
        }

        public NodeBase getNode() {
            return node;
        }

        public String getCondition() {
            return condition;
        }
    }
}
