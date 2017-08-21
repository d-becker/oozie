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

    public Workflow build() {
        final Set<Node> nodes = new HashSet<>();
        for (final Node node : addedActions) {
            if (!nodes.contains(node)) {
                nodes.addAll(getNodesInDag(node));
            }
        }

        final ImmutableSet.Builder<Node> builder = new ImmutableSet.Builder<>();
        builder.addAll(nodes);

        return new Workflow(name.get(), builder.build());
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
