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

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.oozie.jobs.api.action.Node;
import org.apache.oozie.jobs.api.workflow.Workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    private int forkCounter = 1;

    private final Map<Fork, Integer> forkNumbers = new HashMap<>();
    private final Start start = new Start("start");
    private final End end = new End("end");

    // TODO: ensure no duplicate names are present. Now Workflow ensures that.
    private final Map<String, NodeBase> nodesByName = new HashMap<>();

    // Nodes that have a join downstreams to them are closed, they should never get new children.
    private final Map<NodeBase, Join> closingJoin = new HashMap<>();

    public Graph(final Workflow workflow) {
        List<Node> nodes = getNodesInTopologicalOrder(workflow);
        nodesByName.put(start.getName(), start);
        nodesByName.put(end.getName(), end);
        convert(nodes);
    }

    public Start getStart() {
        return start;
    }

    public End getEnd() {
        return end;
    }

    public NodeBase getNodeByName(final String name) {
        return nodesByName.get(name);
    }

    public Collection<NodeBase> getNodes() {
        return nodesByName.values();
    }

    private void convert(final List<Node> nodes) {
        Map<Node, NodeBase> mappings = new HashMap<>();

        for (Node originalNode : nodes) {
            ExplicitNode convertedNode = new ExplicitNode(originalNode.getName(), originalNode);
            mappings.put(originalNode, convertedNode);
            nodesByName.put(convertedNode.getName(), convertedNode);

            final List<NodeBase> mappedParents = new ArrayList<>();

            for (Node originalParent : originalNode.getParents()) {
                mappedParents.add(mappings.get(originalParent));
            }

            handleNodeWithParents(convertedNode, mappedParents);
        }

        List<NodeBase> finalNodes = new ArrayList<>();
        for (NodeBase node : nodesByName.values()) {
            if (node.getChildren().isEmpty() && node != end) {
                finalNodes.add(node);
            }
        }

        handleNodeWithParents(end, finalNodes);
    }

    private void handleNodeWithParents(final NodeBase node, final List<NodeBase> parents) {
        if (parents.isEmpty()) {
            handleNonJoinNode(node, start);
        }
        else if (parents.size() == 1) {
            handleNonJoinNode(node, parents.get(0));
        }
        else {
            handleJoinNodeWithParents(node, parents);
        }
    }

    private void handleNonJoinNode(final NodeBase node, final NodeBase parent) {
        addParentWithForkIfNeeded(node, parent);
    }

    private void handleJoinNodeWithParents(final NodeBase node, final List<NodeBase> parents) {
        Set<NodeBase> replacementParents = new LinkedHashSet<>();
        Set<NodeBase> parentsToRemove = new LinkedHashSet<>();
        for (NodeBase parent : parents) {
            if (closingJoin.containsKey(parent)) {
                NodeBase replacementParent = closingJoin.get(parent);
                replacementParents.add(replacementParent);
                parentsToRemove.add(parent);
            }
        }

        if (replacementParents.isEmpty()) {
            if (parents.size() == 1) {
                addParentWithForkIfNeeded(node, parents.get(0));
            }
            else {
                insertJoin(node, parents);
            }
        }
        else {
            final List<NodeBase> newParents = new ArrayList<>();
            newParents.addAll(replacementParents);

            for (NodeBase parent : parents) {
                if (!parentsToRemove.contains(parent)) {
                    newParents.add(parent);
                }
            }

            handleNodeWithParents(node, newParents);
        }

    }

    private void insertJoin(final NodeBase node, final List<NodeBase> parents) {
        final List<PathInformation> paths = new ArrayList<>();
        for (NodeBase parent : parents) {
            PathInformation path = getPathInfo(parent);
            paths.add(path);
        }

        Pair<NodeBase, List<PathInformation>> toClose = getOneForkToClose(paths);

        Fork fork;

        // Eliminating redundant parents.
        if (toClose.getLeft() instanceof Fork) {
            fork = (Fork) toClose.getLeft();
        } else {
            final List<NodeBase> parentsWithoutRedundant = new ArrayList<>(parents);
            parentsWithoutRedundant.remove(toClose.getLeft());

            handleNodeWithParents(node, parentsWithoutRedundant);
            return;
        }

        if (toClose.getRight().size() == paths.size()) {
            // There are no intermediary fork / join pairs to insert, we have to join all paths in a single join.
            Join newJoin = joinPaths(fork, toClose.getRight());

            addParentWithForkIfNeeded(node, newJoin);
        } else {
            // We have to close a subset of the paths.
            List<NodeBase> newParents = new ArrayList<>(parents);
            List<NodeBase> parentsInToClose = new ArrayList<>();

            for (PathInformation path : toClose.getRight()) {
                parentsInToClose.add(path.getBottom());
                newParents.remove(path.getBottom());
            }

            Join newJoin = joinPaths(fork, toClose.getRight());

            newParents.add(newJoin);

            insertJoin(node, newParents);
        }
    }

    private Join joinPaths(final Fork correspondingFork, List<PathInformation> paths) {
        Set<NodeBase> mainBranchNodes = new HashSet<>();

        for (PathInformation path : paths) {
            mainBranchNodes.addAll(path.getNodes());
        }

        // Take care of side branches.
        Set<NodeBase> closedNodes = new HashSet<>();
        List<NodeBase> sideBranches = new ArrayList<>();
        for (PathInformation path : paths) {
            for (int i = 0; i < path.getNodes().size(); ++i) {
                NodeBase node = path.getNodes().get(i);

                if (node == correspondingFork) {
                    break;
                }

                sideBranches.addAll(cutDownSideBranches(node, mainBranchNodes));
                closedNodes.add(node);
            }
        }

        Join newJoin = null;

        // Check if we have to divide the fork.
        if (paths.size() < correspondingFork.getChildren().size()) {
            // Dividing the fork.
            newJoin = divideForkAndCloseSubFork(correspondingFork, paths);
        } else {
            // We don't divide the fork.
            newJoin = getNewJoinNode(correspondingFork);

            for (PathInformation path : paths) {
                addParentWithForkIfNeeded(newJoin, path.getBottom());
            }
        }

        // Inserting the side branches under the new join node.
        for (NodeBase sideBranch : sideBranches) {
            addParentWithForkIfNeeded(sideBranch, newJoin);
        }

        // Marking the nodes as closed.
        for (NodeBase closedNode : closedNodes) {
            markAsClosed(closedNode, newJoin);
        }

        return newJoin;
    }

    private void markAsClosed(NodeBase node, Join join) {
        closingJoin.put(node, join);
    }

    private List<NodeBase> cutDownSideBranches(final NodeBase node,
                                               final Set<NodeBase> mainBranchNodes) {
        List<NodeBase> sideBranches = new ArrayList<>();

        if (node instanceof Fork && ((Fork) node).isClosed()) {
            // Closed forks cannot have side branches.
        }
        else {
            for (NodeBase childOfForkOrParent : node.getChildren()) {
                if (!mainBranchNodes.contains(childOfForkOrParent)) {
                    removeParentWithForkIfNeeded(childOfForkOrParent, node);
                    sideBranches.add(childOfForkOrParent);
                }
            }
        }

        return sideBranches;
    }

    private Join divideForkAndCloseSubFork(final Fork correspondingFork,
                                           final List<PathInformation> paths) {
        Fork newFork = getNewForkNode();
        for (PathInformation path : paths) {
            int indexOfFork = path.getNodes().indexOf(correspondingFork);
            NodeBase childOfOriginalFork = path.getNodes().get(indexOfFork - 1);

            childOfOriginalFork.removeParent(correspondingFork);
            childOfOriginalFork.addParent(newFork);
        }

        newFork.addParent(correspondingFork);

        Join newJoin = getNewJoinNode(newFork);

        for (PathInformation path : paths) {
            newJoin.addParent(path.getBottom());
        }

        return newJoin;
    }

    private Pair<NodeBase, List<PathInformation>> getOneForkToClose(final List<PathInformation> paths) {
        int maxPathLength = 0;
        for (PathInformation path : paths) {
            maxPathLength = Math.max(maxPathLength, path.getNodes().size());
        }

        for (int i = 0; i < maxPathLength; ++i) {
            Pair<NodeBase, List<PathInformation>> foundAtThisLevel = getOneForkToCloseAtLevelN(i, paths);

            if (foundAtThisLevel != null) {
                return foundAtThisLevel;
            }
        }

        throw new IllegalStateException("We should never reach here.");
    }

    private Pair<NodeBase, List<PathInformation>> getOneForkToCloseAtLevelN(final int n,
                                                                            final List<PathInformation> paths) {
        for (PathInformation path : paths) {
            if (n < path.getNodes().size()) {
                NodeBase currentFork = path.getNodes().get(n);

                List<PathInformation> pathsMeetingAtCurrentFork = getPathsContainingNode(currentFork, paths);

                if (pathsMeetingAtCurrentFork.size() > 1) {

                    // If currentFork is not really a Fork, then it is a redundant parent.
                    return new ImmutablePair<>(currentFork, pathsMeetingAtCurrentFork);
                }
            }
        }

        return null;
    }

    private List<PathInformation> getPathsContainingNode(final NodeBase node, List<PathInformation> paths) {
        List<PathInformation> result = new ArrayList<>();

        for (PathInformation pathInfo : paths) {
            if (pathInfo.getNodes().contains(node)) {
                result.add(pathInfo);
            }
        }

        return result;
    }

    private PathInformation getPathInfo(final NodeBase node) {
        NodeBase current = node;

        final List<NodeBase> nodes = new ArrayList<>();

        while (current != start) {
            nodes.add(current);

            if (current instanceof Join) {
                // Get the fork corresponding to this join and go towards that.
                Fork correspondingFork = ((Join) current).getCorrespondingFork();
                current = correspondingFork;
            } else {
                current = getSingleParent(current);
            }
        }

        return new PathInformation(nodes);
    }

    private NodeBase getSingleParent(final NodeBase node) {
        if (node instanceof End) {
            return ((End) node).getParent();
        } else if (node instanceof Fork) {
            return ((Fork) node).getParent();
        } else if (node instanceof ExplicitNode) {
            return ((ExplicitNode) node).getParent();
        } else if (node instanceof Start) {
            throw new IllegalStateException("The start has no parent.");
        } else if (node instanceof Join) {
            Join join = (Join) node;
            int numberOfParents = join.getParents().size();
            if (numberOfParents != 1) {
                throw new IllegalStateException("The start called '" + node.getName()
                        + "' has " + numberOfParents + " parents instead of 1.");
            }

            return join.getParents().get(0);
        }

        throw new IllegalArgumentException("Unknown start type.");
    }

    private void addParentWithForkIfNeeded(NodeBase node, NodeBase parent) {
        if (parent.getChildren().isEmpty() || parent instanceof Fork) {
            node.addParent(parent);
        } else {
            // If there is no child, we never get to this point.
            // There is only one child, otherwise it is a fork and we don't get here.
            NodeBase child = parent.getChildren().get(0);

            if (child instanceof Fork) {
                node.addParent(child);
            }
            else if (child instanceof Join) {
                addParentWithForkIfNeeded(node, child);
            }
            else {
                final Fork newFork = getNewForkNode();
                child.removeParent(parent);
                child.addParent(newFork);
                node.addParent(newFork);
                newFork.addParent(parent);
            }
        }
    }

    private void removeParentWithForkIfNeeded(NodeBase node, NodeBase parent) {
        node.removeParent(parent);

        if (parent instanceof Fork && parent.getChildren().size() == 1) {
            NodeBase grandparent = ((Fork) parent).getParent();
            NodeBase child = parent.getChildren().get(0);

            removeParentWithForkIfNeeded(parent, grandparent);
            child.removeParent(parent);
            child.addParent(grandparent);
            nodesByName.remove(parent.getName());
        }
    }

    private Fork getNewForkNode() {
        Fork fork = new Fork("fork" + forkCounter);
        forkNumbers.put(fork, forkCounter);
        forkCounter++;
        nodesByName.put(fork.getName(), fork);

        return fork;
    }

    private Join getNewJoinNode(Fork correspondingFork) {
        Join join = new Join("join" + forkNumbers.get(correspondingFork), correspondingFork);
        nodesByName.put(join.getName(), join);

        return join;
    }

    private static List<Node> getNodesInTopologicalOrder(final Workflow workflow) {
        final SetAndList<Node> nodes = new SetAndList<>(workflow.getRoots());

        for (int i = 0; i < nodes.size(); ++i) {
            final Node current  = nodes.get(i);

            for (Node child : current.getChildren()) {
                // Checking if every dependency has been processed, if not, we do not add the start to the list.
                List<Node> dependencies = child.getParents();
                if (nodes.containsAll(dependencies) && !nodes.contains(child)) {
                    nodes.add(child);
                }
            }
        }

        return nodes.consumeAndReturnList();
    }

    // We need a sequential container but we want to check efficiently if it contains an element.
    private static class SetAndList<T> {
        private List<T> list;
        private Set<T> set;

        public SetAndList() {
            list = new ArrayList<>();
            set = new HashSet<>();
        }

        public SetAndList(final Collection<T> collection) {
            list = new ArrayList<>(collection);
            set = new HashSet<>(collection);
        }

        public int size() {
            return list.size();
        }

        public T get(final int i) {
            return list.get(i);
        }

        public boolean contains(final T element) {
            return set.contains(element);
        }

        public boolean containsAll(final Collection<T> elements) {
            return set.containsAll(elements);
        }

        public void add(final T element) {
            if (!set.contains(element)) {
                list.add(element);
                set.add(element);
            }
        }

        public List<T> consumeAndReturnList() {
            List<T> result = list;

            list = null;
            set = null;

            return result;
        }
    }

    private static class PathInformation {
        private final ImmutableList<NodeBase> nodes;

        public PathInformation(final List<NodeBase> nodes) {
            this.nodes = new ImmutableList.Builder<NodeBase>().addAll(nodes).build();
        }

        public NodeBase getBottom() {
            return nodes.get(0);
        }

        public List<NodeBase> getNodes() {
            return nodes;
        }


    }

}
