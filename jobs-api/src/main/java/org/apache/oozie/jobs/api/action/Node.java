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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node {
    private final String name;
    private final ImmutableList<Node> parentsWithoutConditions;
    private final ImmutableList<Node.NodeWithCondition> parentsWithConditions;
    private final ErrorHandler errorHandler;

    private final List<Node> childrenWithoutConditions; // MUTABLE!
    private final List<NodeWithCondition> childrenWithConditions; // MUTABLE!
    private Node defaultConditionalChild; // MUTABLE!

    Node(final String name,
         final ImmutableList<Node> parentsWithoutConditions,
         final ImmutableList<Node.NodeWithCondition> parentsWithConditions,
         final ErrorHandler errorHandler)
    {
        this.name = name;
        this.parentsWithoutConditions = parentsWithoutConditions;
        this.parentsWithConditions = parentsWithConditions;
        this.errorHandler = errorHandler;

        this.childrenWithoutConditions = new ArrayList<>();
        this.childrenWithConditions = new ArrayList<>();
        this.defaultConditionalChild = null;
    }

    public String getName() {
        return name;
    }

    public List<Node> getAllParents() {
        final List<Node> allParents = new ArrayList<>(parentsWithoutConditions);

        for (NodeWithCondition parentWithCondition : parentsWithConditions) {
            allParents.add(parentWithCondition.getNode());
        }

        return Collections.unmodifiableList(allParents);
    }

    public List<Node> getParentsWithoutConditions() {
        return parentsWithoutConditions;
    }

    public List<Node.NodeWithCondition> getParentsWithConditions() {
        return parentsWithConditions;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    void addChild(final Node child) {
        if (!childrenWithConditions.isEmpty()) {
            throw new IllegalStateException(
                    "Trying to add a child without condition to a node that already has at least one child with a condition.");
        }

        this.childrenWithoutConditions.add(child);
    }

    void addChildWithCondition(final Node child, final String condition) {
        if (!childrenWithoutConditions.isEmpty()) {
            throw new IllegalStateException(
                    "Trying to add a child with condition to a node that already has at least one child without a condition.");
        }

        this.childrenWithConditions.add(new NodeWithCondition(child, Condition.actualCondition(condition)));
    }

    void addChildAsDefaultConditional(final Node child) {
        if (!childrenWithoutConditions.isEmpty()) {
            throw new IllegalStateException(
                    "Trying to add a default conditional child to a node that already has at least one child without a condition.");
        }

        if (defaultConditionalChild != null) {
            throw new IllegalStateException(
                    "Trying to add a default conditional child to a node that already has one.");
        }

        this.defaultConditionalChild = child;
    }

    /**
     * Returns an unmodifiable view of list of the children of this <code>Action</code>.
     * @return An unmodifiable view of list of the children of this <code>Action</code>.
     */
    public List<Node> getAllChildren() {
        final List<Node> allChildren = new ArrayList<>(childrenWithoutConditions);

        for (NodeWithCondition nodeWithCondition : getChildrenWithConditions()) {
            allChildren.add(nodeWithCondition.getNode());
        }

        return Collections.unmodifiableList(allChildren);
    }

    /**
     * Returns an unmodifiable view of list of the children without condition of this <code>Action</code>.
     * @return An unmodifiable view of list of the children without condition of this <code>Action</code>.
     */
    public List<Node> getChildrenWithoutConditions() {
        return Collections.unmodifiableList(childrenWithoutConditions);
    }

    /**
     * Returns an unmodifiable view of list of the children with condition (including the default) of this <code>Action</code>.
     * @return An unmodifiable view of list of the children with condition (including the default) of this <code>Action</code>.
     */
    public List<NodeWithCondition> getChildrenWithConditions() {
        if (defaultConditionalChild == null) {
            return Collections.unmodifiableList(childrenWithConditions);
        }
        else {
            final List<NodeWithCondition> results = new ArrayList<>(childrenWithConditions);
            results.add(new NodeWithCondition(defaultConditionalChild, Condition.defaultCondition()));

            return Collections.unmodifiableList(results);
        }
    }

    public Node getDefaultConditionalChild() {
        return defaultConditionalChild;
    }

    public static class NodeWithCondition {
        private final Node node;
        private final Condition condition;

        public NodeWithCondition(final Node node,
                                 final Condition condition) {
            this.node = node;
            this.condition = condition;
        }

        public Node getNode() {
            return node;
        }

        public Condition getCondition() {
            return condition;
        }
    }
}
