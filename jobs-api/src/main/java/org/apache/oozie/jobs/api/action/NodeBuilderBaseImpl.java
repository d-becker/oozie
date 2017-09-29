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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.Condition;
import org.apache.oozie.jobs.api.ModifyOnce;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base class for builders that build concrete instances of subclasses of {@link Node}. This class doesn't
 * implement the {@link Builder} interface as no type information as to the concrete node to build. The concrete node
 * builder classes of course should implement {@link Builder}.
 *
 * The concrete builders should provide a fluent API, and to facilitate this, the methods in this base class have to
 * return the concrete builder. Therefore it is templated on the type of the concrete builder class. Although it cannot
 * be enforced that the provided generic parameter is the same as the class deriving from this class, it definitely
 * should be, and the constraint on the type parameter tries to minimize the chance that the class is subclassed
 * incorrectly.
 *
 * The properties of the builder can only be set once, an attempt to set them a second time will trigger
 * an {@link IllegalStateException}. The properties that are lists are an exception to this rule, of course multiple
 * elements can be added / removed.
 *
 * @param <B> The type of the concrete builder class deriving from this class.
 */
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

    /**
     * Registers an error handler with this builder.
     * @param errorHandler The error handler to register.
     * @return This builder.
     */
    public B withErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler.set(errorHandler);
        return ensureRuntimeSelfReference();
    }

    /**
     * Removes the currently registered error handler if any.
     * @return This builder.
     */
    public B withoutErrorHandler() {
        errorHandler.set(null);
        return ensureRuntimeSelfReference();
    }

    /**
     * Registers a name that will be the name of the action built by this builder.
     * @param name The name of the action that will be built.
     * @return This builder.
     */
    public B withName(final String name) {
        this.name.set(name);
        return ensureRuntimeSelfReference();
    }

    /**
     * Registers an unconditional parent with this builder. If the parent is already registered with this builder,
     * {@link IllegalArgumentException} is thrown.
     * @param parent The node that will be the parent of the built action.
     * @return This builder.
     *
     * @throws IllegalArgumentException if the provided node is already registered as a parent.
     */
    public B withParent(final Node parent) {
        checkNoDuplicateParent(parent);

        parents.add(parent);
        return ensureRuntimeSelfReference();
    }

    /**
     * Registers a conditional parent with this builder. If the parent is already registered with this builder,
     * {@link IllegalArgumentException} is thrown.
     * @param parent The node that will be the parent of the built action.
     * @param condition The condition of the parent.
     * @return This builder.
     *
     * @throws IllegalArgumentException if the provided node is already registered as a parent.
     */
    public B withParentWithCondition(final Node parent, final String condition) {
        checkNoDuplicateParent(parent);

        parentsWithConditions.add(new Node.NodeWithCondition(parent, Condition.actualCondition(condition)));
        return ensureRuntimeSelfReference();
    }

    /**
     * Registers a conditional parent for which this node is the default transition. If the parent is already registered
     * with this builder, {@link IllegalArgumentException} is thrown.
     * {@link IllegalArgumentException} is thrown.
     * @param parent The node that will be the parent of the built action.
     * @return This builder.
     *
     * @throws IllegalArgumentException if the provided node is already registered as a parent.
     */
    public B withParentDefaultConditional(final Node parent) {
        checkNoDuplicateParent(parent);

        parentsWithConditions.add(new Node.NodeWithCondition(parent, Condition.defaultCondition()));
        return ensureRuntimeSelfReference();
    }

    /**
     * Removes a parent registered with this builder. If the parent is not registered with this builder, this method
     * does nothing.
     * @param parent The parent to remove.
     * @return This builder.
     */
    public B withoutParent(final Node parent) {
        if (parents.contains(parent)) {
            parents.remove(parent);
        } else {
            int index = indexOfParentAmongParentsWithConditions(parent);
            parentsWithConditions.remove(index);
        }

        return ensureRuntimeSelfReference();
    }

    /**
     * Removes all parents registered with this builder.
     * @return This builder.
     */
    public B clearParents() {
        parents.clear();
        parentsWithConditions.clear();
        return ensureRuntimeSelfReference();
    }

    final B ensureRuntimeSelfReference() {
        final B runtimeSelfReference = getRuntimeSelfReference();

        Preconditions.checkState(runtimeSelfReference == this, "The builder type B doesn't extend NodeBuilderBaseImpl<B>.");

        return runtimeSelfReference;
    }

    private void checkNoDuplicateParent(final Node parent) {
        boolean parentsContains = parents.contains(parent);
        boolean parentsWithConditionsContains = indexOfParentAmongParentsWithConditions(parent) != -1;

        Preconditions.checkArgument(!parentsContains && !parentsWithConditionsContains,
                "Trying to add a parent that is already a parent of this node.");
    }

    private int indexOfParentAmongParentsWithConditions(final Node parent) {
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