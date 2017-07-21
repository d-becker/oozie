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

package org.apache.oozie.jobs.api;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class WorkflowBuilder {
    private final ModifyOnce<String> name;
    private final List<Action> addedActions;

    public WorkflowBuilder() {
        name = new ModifyOnce<>();
        addedActions = new ArrayList<>();
    }

    public WorkflowBuilder withName(String name) {
        this.name.set(name);
        return this;
    }

    public WorkflowBuilder withDagContainingAction(Action action) {
        addedActions.add(action);
        return this;
    }

    public Workflow build() {
        final Set<Action> nodes = new HashSet<>();
        for (Action node : addedActions) {
            if (!nodes.contains(node)) {
                nodes.addAll(getNodesInDag(node));
            }
        }

        final ImmutableSet.Builder<Action> builder = new ImmutableSet.Builder<>();
        builder.addAll(nodes);

        return new Workflow(name.get(), builder.build());
    }

    private static Set<Action> getNodesInDag(Action action) {
        final Set<Action> visited = new HashSet<>();
        final Queue<Action> queue = new ArrayDeque<>();
        visited.add(action);
        queue.add(action);

        Action current;
        while ((current = queue.poll()) != null) {
            visit(current.getParents(), visited, queue);
            visit(current.getChildren(), visited, queue);
        }

        return visited;
    }

    private static void visit(List<Action> toVisit, Set<Action> visited, Queue<Action> queue) {
        for (Action node : toVisit) {
            if (!visited.contains(node)) {
                visited.add(node);
                queue.add(node);
            }
        }
    }
}
