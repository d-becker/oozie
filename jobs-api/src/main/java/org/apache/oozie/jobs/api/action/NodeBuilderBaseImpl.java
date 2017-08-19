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

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.Condition;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeBuilderBaseImpl <B extends NodeBuilderBaseImpl<B>> {
    private final ModifyOnce<String> name;
    private final List<Node> parents;
    private final List<Node.NodeWithCondition> parentsWithConditions;

    private final ModifyOnce<ErrorHandler> errorHandler;

    NodeBuilderBaseImpl() {
        this(null);
    }

    NodeBuilderBaseImpl(final Node node) {
        if (node == null) {
            name = new ModifyOnce<>();
            parents = new ArrayList<>();
            parentsWithConditions = new ArrayList<>();
            errorHandler = new ModifyOnce<>();
        }
        else {
            name = new ModifyOnce<>(node.getName());
            parents = new ArrayList<>(node.getParentsWithoutConditions());
            parentsWithConditions = new ArrayList<>(node.getParentsWithConditions());
            errorHandler = new ModifyOnce<>(node.getErrorHandler());
        }
    }

    public B withErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler.set(errorHandler);
        return ensureRuntimeSelfReference();
    }

    public B withoutErrorHandler() {
        errorHandler.set(null);
        return ensureRuntimeSelfReference();
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

        parentsWithConditions.add(new Node.NodeWithCondition(parent, Condition.actualCondition(condition)));
        return ensureRuntimeSelfReference();
    }

    public B withParentDefaultConditional(final Node parent) {
        parentsWithConditions.add(new Node.NodeWithCondition(parent, Condition.defaultCondition()));
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
                    "The builder type B doesn't extend NodeBuilderBaseImpl<B>.");
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
                final Condition condition = parentWithCondition.getCondition();

                if (condition.isDefault()) {
                    parent.addChildAsDefaultConditional(child);
                }
                else {
                    parent.addChildWithCondition(child, condition.getCondition());
                }
            }
        }
    }

    Node.ConstructionData getConstructionData() {
        final String nameStr = this.name.get();

        final ImmutableList<Node> parentsList = new ImmutableList.Builder<Node>().addAll(parents).build();
        final ImmutableList<Node.NodeWithCondition> parentsWithConditionsList
                = new ImmutableList.Builder<Node.NodeWithCondition>().addAll(parentsWithConditions).build();

        return new Node.ConstructionData(
                nameStr,
                parentsList,
                parentsWithConditionsList,
                errorHandler.get()
        );
    }

    protected abstract B getRuntimeSelfReference();
}
