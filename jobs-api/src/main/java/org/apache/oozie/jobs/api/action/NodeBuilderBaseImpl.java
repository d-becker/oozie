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

import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeBuilderBaseImpl <B extends NodeBuilderBaseImpl<B>> {
    protected final ModifyOnce<String> name;
    protected final List<Node> parents;
    protected final List<Node.NodeWithCondition> parentsWithConditions;

    NodeBuilderBaseImpl() {
        name = new ModifyOnce<>();
        parents = new ArrayList<>();
        parentsWithConditions = new ArrayList<>();
    }

    NodeBuilderBaseImpl(final Node node) {
        name = new ModifyOnce<>(node.getName());
        parents = new ArrayList<>(node.getParentsWithoutConditions());
        parentsWithConditions = new ArrayList<>(node.getParentsWithConditions());
    }

    public B withName(final String name) {
        this.name.set(name);
        return ensureRuntimeSelfReference();
    }

    public B withParent(final Node parent) {
        checkNoDuplicateParent(parent);

        parents.add(parent);
        return ensureRuntimeSelfReference();
    }

    public B withParentWithCondition(final Node parent, final String condition) {
        checkNoDuplicateParent(parent);

        parentsWithConditions.add(new Node.NodeWithCondition(parent, condition));
        return ensureRuntimeSelfReference();
    }

    B withoutParent(final Node parent) {
        if (parents.contains(parent)) {
            parents.remove(parent);
        } else {
            int index = indexOfParent(parent);
            parentsWithConditions.remove(index);
        }

        return ensureRuntimeSelfReference();
    }

    public B clearParents() {
        parents.clear();
        parentsWithConditions.clear();
        return ensureRuntimeSelfReference();
    }

    final B ensureRuntimeSelfReference() {
        final B concrete = getRuntimeSelfReference();
        if (concrete != this) {
            throw new IllegalStateException(
                    "The builder type B doesn't extend ActionBuilderBaseImpl<B>.");
        }

        return concrete;
    }

    private void checkNoDuplicateParent(final Node parent) {
        if (parents.contains(parent) || indexOfParent(parent) != -1) {
            throw new IllegalArgumentException("Trying to add a parent that is already a parent of this node.");
        }
    }

    private int indexOfParent(final Node parent) {
        for (int i = 0; i < parentsWithConditions.size(); ++i) {
            if (parent == parentsWithConditions.get(i).getNode()) {
                return i;
            }
        }

        return -1;
    }

    protected void addAsChildToAllParents(final Node child) {
        final List<Node> parentsList = child.getParentsWithoutConditions();
        if (parentsList != null) {
            for (final Node parent : parentsList) {
                parent.addChild(child);
            }
        }

        final List<Node.NodeWithCondition> parentsWithConditionsList = child.getParentsWithConditions();
        if (parentsWithConditionsList != null) {
            for (final Node.NodeWithCondition parentWithCondition : parentsWithConditionsList) {
                final Node parent = parentWithCondition.getNode();
                final String condition = parentWithCondition.getCondition();
                parent.addChildWithCondition(child, condition);
            }
        }
    }

    protected abstract B getRuntimeSelfReference();
}