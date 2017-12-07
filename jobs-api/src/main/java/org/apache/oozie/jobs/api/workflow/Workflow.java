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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.apache.oozie.jobs.api.action.Node;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Workflow {
    private final String name;
    private final ImmutableSet<Node> nodes;
    private final ImmutableSet<Node> roots;
    private final Parameters parameters;
    private final Global global;
    private final Credentials credentials;

    Workflow(final String name,
             final ImmutableSet<Node> nodes,
             final Parameters parameters,
             final Global global,
             final Credentials credentials) {
        this.global = global;
        checkUniqueNames(nodes);

        this.name = name;
        this.nodes = nodes;

        final Set<Node> mutableRoots = findMutableRoots(nodes);

        this.roots = ImmutableSet.copyOf(mutableRoots);

        this.parameters = parameters;

        this.credentials = credentials;
    }

    public String getName() {
        return name;
    }

    public ImmutableSet<Node> getNodes() {
        return nodes;
    }

    public ImmutableSet<Node> getRoots() {
        return roots;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public Global getGlobal() {
        return global;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    private void checkUniqueNames(final Set<Node> nodes) {
        final Set<String> names = new HashSet<>();

        for (final Node node : nodes) {
            Preconditions.checkArgument(!names.contains(node.getName()),
                    String.format("Duplicate name '%s' found in workflow '%s'", node.getName(), getName()));

            names.add(node.getName());
        }
    }

    private Set<Node> findMutableRoots(ImmutableSet<Node> nodes) {
        final Set<Node> mutableRoots = new LinkedHashSet<>();

        for (final Node node : nodes) {
            if (node.getAllParents().isEmpty()) {
                mutableRoots.add(node);
            }
        }

        return mutableRoots;
    }
}
