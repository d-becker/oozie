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

package org.apache.oozie.jobs.api.workflow;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.oozie.jobs.api.ModifyOnce;
import org.apache.oozie.jobs.api.action.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class WorkflowBuilder {
    private final ModifyOnce<String> name;
    private final List<Node> addedActions;
    private ParametersBuilder parametersBuilder;
    private GlobalBuilder globalBuilder;
    private CredentialsBuilder credentialsBuilder;

    public WorkflowBuilder() {
        this.name = new ModifyOnce<>();
        this.addedActions = new ArrayList<>();
    }

    public WorkflowBuilder withName(final String name) {
        this.name.set(name);
        return this;
    }

    public WorkflowBuilder withDagContainingNode(final Node node) {
        this.addedActions.add(node);
        return this;
    }

    public WorkflowBuilder withParameter(final String name, final String value) {
        ensureParametersBuilder();
        this.parametersBuilder.withParameter(name, value);
        return this;
    }

    public WorkflowBuilder withParameter(final String name, final String value, final String description) {
        this.parametersBuilder.withParameter(name, value, description);
        return this;
    }

    private void ensureParametersBuilder() {
        if (this.parametersBuilder == null) {
            this.parametersBuilder = ParametersBuilder.create();
        }
    }

    public WorkflowBuilder withGlobal(final Global global) {
        this.globalBuilder = GlobalBuilder.createFromExisting(global);
        return this;
    }

    public WorkflowBuilder withCredentials(final Credentials credentials) {
        this.credentialsBuilder = CredentialsBuilder.createFromExisting(credentials);
        return this;
    }

    public Workflow build() {
        ensureName();

        final Set<Node> nodes = new HashSet<>();
        for (final Node node : addedActions) {
            if (!nodes.contains(node)) {
                nodes.addAll(getNodesInDag(node));
            }
        }

        final ImmutableSet.Builder<Node> builder = new ImmutableSet.Builder<>();
        builder.addAll(nodes);

        final Parameters parameters;
        if (parametersBuilder != null) {
            parameters = parametersBuilder.build();
        }
        else {
            parameters = null;
        }

        final Global global;
        if (globalBuilder != null) {
            global = globalBuilder.build();
        }
        else {
            global = null;
        }

        final Credentials credentials;
        if (credentialsBuilder != null) {
            credentials = credentialsBuilder.build();
        }
        else {
            credentials = null;
        }

        return new Workflow(name.get(), builder.build(), parameters, global, credentials);
    }

    private void ensureName() {
        if (Strings.isNullOrEmpty(this.name.get())) {
            final String type = "workflow";
            final int randomSuffix = Double.valueOf(Math.round(Math.random() * 1_000_000_000)).intValue();

            this.name.set(String.format("%s-%d", type, randomSuffix));
        }
    }

    private static Set<Node> getNodesInDag(final Node node) {
        final Set<Node> visited = new HashSet<>();
        final Queue<Node> queue = new ArrayDeque<>();
        visited.add(node);
        queue.add(node);

        Node current;
        while ((current = queue.poll()) != null) {
            visit(current.getAllParents(), visited, queue);
            visit(current.getAllChildren(), visited, queue);
        }

        return visited;
    }

    // TODO: encapsulate into a more specific nested class, e.g. DagWalker#walk
    private static void visit(final List<Node> toVisit, final Set<Node> visited, final Queue<Node> queue) {
        for (final Node node : toVisit) {
            if (!visited.contains(node)) {
                visited.add(node);
                queue.add(node);
            }
        }
    }
}
