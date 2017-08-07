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
    private final String name;
    private final Start start = new Start("start");
    private final End end = new End("end");
    private final Map<String, NodeBase> nodesByName = new HashMap<>();
    private final Map<Fork, Integer> forkNumbers = new HashMap<>();
    private int forkCounter = 1;

    /**
     * Nodes that have a join downstream to them are closed, they should never get new children.
     */
    private final Map<NodeBase, Join> closingJoins = new HashMap<>();

    public Graph(final Workflow workflow) {
        this.name = workflow.getName();

        final List<Node> nodesInTopologicalOrder = getNodesInTopologicalOrder(workflow);

        storeNode(start);
        storeNode(end);

        convert(nodesInTopologicalOrder);
    }

    public String getName() {
        return name;
    }

    public Start getStart() {
        return start;
    }

    public End getEnd() {
        return end;
    }

    NodeBase getNodeByName(final String name) {
        return nodesByName.get(name);
    }

    public Collection<NodeBase> getNodes() {
        return nodesByName.values();
    }

    private void convert(final List<Node> nodesInTopologicalOrder) {
        final Map<Node, NodeBase> nodeToNodeBase = new HashMap<>();

        for (final Node originalNode : nodesInTopologicalOrder) {
            final ExplicitNode convertedNode = new ExplicitNode(originalNode.getName(), originalNode);
            nodeToNodeBase.put(originalNode, convertedNode);
            storeNode(convertedNode);

            final List<NodeBase> mappedParents = new ArrayList<>();
            for (final Node parent : originalNode.getParents()) {
                mappedParents.add(nodeToNodeBase.get(parent));
            }

            handleNodeWithParents(mappedParents, convertedNode);
        }

        final List<NodeBase> finalNodes = new ArrayList<>();
        for (final NodeBase maybeFinalNode : nodesByName.values()) {
            final boolean hasNoChildrenAndIsNotEnd = maybeFinalNode.getChildren().isEmpty() && maybeFinalNode != end;
            if (hasNoChildrenAndIsNotEnd) {
                finalNodes.add(maybeFinalNode);
            }
        }

        handleNodeWithParents(finalNodes, end);
    }

    private void storeNode(final NodeBase node) {
        final String name = node.getName();

        final boolean isPresent = nodesByName.containsKey(name);
        if (isPresent) {
            final String errorMessage = String.format("Duplicate name '%s' found in graph '%s'", node.getName(), this.getName());
            throw new IllegalArgumentException(errorMessage);
        }

        nodesByName.put(node.getName(), node);
    }

    private void handleNodeWithParents(final List<NodeBase> parents, final NodeBase node) {
        // Avoiding adding children to nodes that are inside a closed fork / join pair
        final List<NodeBase> newParents = new ArrayList<>();
        for (final NodeBase parent : parents) {
            final NodeBase newParent = getNearestNonClosedDescendant(parent);
            if (!newParents.contains(newParent)) {
                newParents.add(newParent);
            }
        }

        if (newParents.isEmpty()) {
            handleNonJoinNode(start, node);
        }
        else if (newParents.size() == 1) {
            handleNonJoinNode(newParents.get(0), node);
        }
        else {
            handleJoinNodeWithParents(node, newParents);
        }
    }

    private void handleNonJoinNode(final NodeBase parent, final NodeBase node) {
        addParentWithForkIfNeeded(node, parent);
    }

    private void handleJoinNodeWithParents(final NodeBase node, final List<NodeBase> parents) {
        final List<PathInformation> paths = new ArrayList<>();
        for (final NodeBase parent : parents) {
            paths.add(getPathInfo(parent));
        }

        final ForkToClose toClose = chooseForkToClose(paths);

        // Eliminating redundant parents.
        if (toClose.isRedundantParent()) {
            final List<NodeBase> parentsWithoutRedundant = new ArrayList<>(parents);
            parentsWithoutRedundant.remove(toClose.getRedundantParent());

            handleNodeWithParents(parentsWithoutRedundant, node);
        }
        else {
            insertJoin(parents, node, toClose);
        }
    }

    private void insertJoin(final List<NodeBase> parents, final NodeBase node, final ForkToClose forkToClose) {
        if (forkToClose.isSplittingJoinNeeded()) {
            // We have to close a subset of the paths.
            final List<NodeBase> newParents = new ArrayList<>(parents);
            final List<NodeBase> parentsInToClose = new ArrayList<>();

            for (final PathInformation path : forkToClose.getPaths()) {
                parentsInToClose.add(path.getBottom());
                newParents.remove(path.getBottom());
            }

            final Join newJoin = joinPaths(forkToClose.getFork(), forkToClose.getPaths());

            newParents.add(newJoin);

            handleJoinNodeWithParents(node, newParents);
        }
        else {
            // There are no intermediary fork / join pairs to insert, we have to join all paths in a single join.
            final Join newJoin = joinPaths(forkToClose.getFork(), forkToClose.getPaths());

            addParentWithForkIfNeeded(node, newJoin);
        }
    }

    private Join joinPaths(final Fork fork, final List<PathInformation> pathsToJoin) {
        final Set<NodeBase> mainBranchNodes = new LinkedHashSet<>();
        for (final PathInformation pathInformation : pathsToJoin) {
            mainBranchNodes.addAll(pathInformation.getNodes());
        }

        // Taking care of side branches.
        final Set<NodeBase> closedNodes = new HashSet<>();
        final List<NodeBase> sideBranches = new ArrayList<>();
        for (final PathInformation path : pathsToJoin) {
            for (int ixNodeOnPath = 0; ixNodeOnPath < path.getNodes().size(); ++ixNodeOnPath) {
                final NodeBase nodeOnPath = path.getNodes().get(ixNodeOnPath);

                if (nodeOnPath == fork) {
                    break;
                }

                sideBranches.addAll(cutSideBranches(nodeOnPath, mainBranchNodes));
                closedNodes.add(nodeOnPath);
            }
        }

        final Join newJoin;

        // Check if we have to divide the fork.
        final boolean hasMoreForkedChildren = pathsToJoin.size() < fork.getChildren().size();
        if (hasMoreForkedChildren) {
            // Dividing the fork.
            newJoin = divideForkAndCloseSubFork(fork, pathsToJoin);
        }
        else {
            // We don't divide the fork.
            newJoin = newJoin(fork);

            for (final PathInformation path : pathsToJoin) {
                addParentWithForkIfNeeded(newJoin, path.getBottom());
            }
        }

        // Inserting the side branches under the new join node.
        for (final NodeBase sideBranch : sideBranches) {
            addParentWithForkIfNeeded(sideBranch, newJoin);
        }

        // Marking the nodes as closed.
        for (final NodeBase closedNode : closedNodes) {
            markAsClosed(closedNode, newJoin);
        }

        return newJoin;
    }

    private void markAsClosed(final NodeBase node, final Join join) {
        closingJoins.put(node, join);
    }

    private List<NodeBase> cutSideBranches(final NodeBase node,
                                           final Set<NodeBase> mainBranchNodes) {
        final List<NodeBase> sideBranches = new ArrayList<>();

        // Closed forks cannot have side branches.
        final boolean isClosedFork = node instanceof Fork && ((Fork) node).isClosed();
        if (!isClosedFork) {
            for (final NodeBase childOfForkOrParent : node.getChildren()) {
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
        final Fork newFork = newFork();
        for (final PathInformation path : paths) {
            final int indexOfFork = path.getNodes().indexOf(correspondingFork);
            final NodeBase childOfOriginalFork = path.getNodes().get(indexOfFork - 1);

            childOfOriginalFork.removeParent(correspondingFork);
            childOfOriginalFork.addParent(newFork);
        }

        newFork.addParent(correspondingFork);

        final Join newJoin = newJoin(newFork);

        for (final PathInformation path : paths) {
            newJoin.addParent(path.getBottom());
        }

        return newJoin;
    }

    private ForkToClose chooseForkToClose(final List<PathInformation> paths) {
        int maxPathLength = 0;
        for (final PathInformation pathInformation : paths) {
            if (maxPathLength < pathInformation.getNodes().size()) {
                maxPathLength = pathInformation.getNodes().size();
            }
        }

        for (int ixLevel = 0; ixLevel < maxPathLength; ++ixLevel) {
            final ForkToClose foundAtThisLevel = chooseForkToClose(paths, ixLevel);

            if (foundAtThisLevel != null) {
                return foundAtThisLevel;
            }
        }

        throw new IllegalStateException("We should never reach here.");
    }

    private ForkToClose chooseForkToClose(final List<PathInformation> paths, final int ixLevel) {
        for (final PathInformation path : paths) {
            if (ixLevel < path.getNodes().size()) {
                final NodeBase currentFork = path.getNodes().get(ixLevel);

                final List<PathInformation> pathsMeetingAtCurrentFork = getPathsContainingNode(currentFork, paths);

                if (pathsMeetingAtCurrentFork.size() > 1) {
                    final boolean needToSplitJoin = pathsMeetingAtCurrentFork.size() < paths.size();

                    // If currentFork is not really a Fork, then it is a redundant parent.
                    if (currentFork instanceof Fork) {
                        return ForkToClose.withFork((Fork) currentFork, pathsMeetingAtCurrentFork, needToSplitJoin);
                    }

                    return ForkToClose.withRedundantParent(currentFork, pathsMeetingAtCurrentFork, needToSplitJoin);
                }
            }
        }

        return null;
    }

    private List<PathInformation> getPathsContainingNode(final NodeBase node, final List<PathInformation> paths) {
        final List<PathInformation> pathsContainingNode = new ArrayList<>();

        for (final PathInformation pathInformationMaybeContaining : paths) {
            if (pathInformationMaybeContaining.getNodes().contains(node)) {
                pathsContainingNode.add(pathInformationMaybeContaining);
            }
        }

        return pathsContainingNode;
    }

    private PathInformation getPathInfo(final NodeBase node) {
        NodeBase current = node;

        final List<NodeBase> nodes = new ArrayList<>();

        while (current != start) {
            nodes.add(current);

            if (current instanceof Join) {
                // Get the fork pair of this join and go towards that
                final Fork forkPair = ((Join) current).getForkPair();
                current = forkPair;
            }
            else {
                current = getSingleParent(current);
            }
        }

        return new PathInformation(nodes);
    }

    private NodeBase getSingleParent(final NodeBase node) {
        if (node instanceof End) {
            return ((End) node).getParent();
        }
        else if (node instanceof Fork) {
            return ((Fork) node).getParent();
        }
        else if (node instanceof ExplicitNode) {
            return ((ExplicitNode) node).getParent();
        }
        else if (node instanceof Start) {
            throw new IllegalStateException("Start nodes have no parent.");
        }
        else if (node instanceof Join) {
            final Join join = (Join) node;
            final int numberOfParents = join.getParents().size();
            if (numberOfParents != 1) {
                throw new IllegalStateException("The join node called '" + node.getName()
                        + "' has " + numberOfParents + " parents instead of 1.");
            }

            return join.getParents().get(0);
        }

        throw new IllegalArgumentException("Unknown node type.");
    }

    // Returns the first descendant that is not inside a closed fork / join pair.
    private NodeBase getNearestNonClosedDescendant(final NodeBase node) {
        NodeBase current = node;

        while (closingJoins.containsKey(current)) {
            current = closingJoins.get(current);
        }

        return current;
    }

    private void addParentWithForkIfNeeded(final NodeBase node, final NodeBase parent) {
        if (parent.getChildren().isEmpty() || parent instanceof Fork) {
            node.addParent(parent);
        }
        else {
            // If there is no child, we never get to this point.
            // There is only one child, otherwise it is a fork and we don't get here.
            final NodeBase child = parent.getChildren().get(0);

            if (child instanceof Fork) {
                node.addParent(child);
            }
            else if (child instanceof Join) {
                addParentWithForkIfNeeded(node, child);
            }
            else {
                final Fork newFork = newFork();

                child.removeParent(parent);
                child.addParent(newFork);
                node.addParent(newFork);
                newFork.addParent(parent);
            }
        }
    }

    private void removeParentWithForkIfNeeded(final NodeBase node, final NodeBase parent) {
        node.removeParent(parent);

        final boolean isParentForkAndHasOneChild = parent instanceof Fork && parent.getChildren().size() == 1;
        if (isParentForkAndHasOneChild) {
            final NodeBase grandparent = ((Fork) parent).getParent();
            final NodeBase child = parent.getChildren().get(0);

            removeParentWithForkIfNeeded(parent, grandparent);
            child.removeParent(parent);
            child.addParent(grandparent);
            nodesByName.remove(parent.getName());
        }
    }

    private Fork newFork() {
        final Fork fork = new Fork("fork" + forkCounter);

        forkNumbers.put(fork, forkCounter);
        forkCounter++;

        storeNode(fork);

        return fork;
    }

    private Join newJoin(final Fork correspondingFork) {
        final Join join = new Join("join" + forkNumbers.get(correspondingFork), correspondingFork);

        storeNode(join);

        return join;
    }

    private static List<Node> getNodesInTopologicalOrder(final Workflow workflow) {
        final List<Node> nodes = new ArrayList<>(workflow.getRoots());

        for (int i = 0; i < nodes.size(); ++i) {
            final Node current  = nodes.get(i);

            for (final Node child : current.getChildren()) {
                // Checking if every dependency has been processed, if not, we do not add the start to the list.
                final List<Node> dependencies = child.getParents();
                if (nodes.containsAll(dependencies) && !nodes.contains(child)) {
                    nodes.add(child);
                }
            }
        }

        return nodes;
    }

    private static class PathInformation {
        private final ImmutableList<NodeBase> nodes;

        PathInformation(final List<NodeBase> nodes) {
            this.nodes = new ImmutableList.Builder<NodeBase>().addAll(nodes).build();
        }

        NodeBase getBottom() {
            return nodes.get(0);
        }

        public List<NodeBase> getNodes() {
            return nodes;
        }

    }

    private static class ForkToClose {
        private final Fork fork;
        private final NodeBase redundantParent;
        private final ImmutableList<PathInformation> paths;
        private final boolean needToSplitJoin;

        static ForkToClose withFork(final Fork fork,
                                    final List<PathInformation> paths,
                                    final boolean needToSplitJoin) {
            return new ForkToClose(fork, null, paths, needToSplitJoin);
        }

        static ForkToClose withRedundantParent(final NodeBase redundantParent,
                                               final List<PathInformation> paths,
                                               final boolean needToSplitJoin) {
            return new ForkToClose(null, redundantParent, paths, needToSplitJoin);
        }

        private ForkToClose(final Fork fork,
                            final NodeBase redundantParent,
                            final List<PathInformation> paths,
                            final boolean needToSplitJoin) {
            if ((fork != null && redundantParent != null)
                    || (fork == null && redundantParent == null)) {
                throw new IllegalArgumentException("Exactly one of 'fork' and 'redundantParent' must be non-null.");
            }

            this.fork = fork;
            this.redundantParent = redundantParent;
            this.paths = ImmutableList.copyOf(paths);
            this.needToSplitJoin = needToSplitJoin;
        }

        public Fork getFork() {
            return fork;
        }

        NodeBase getRedundantParent() {
            return redundantParent;
        }

        List<PathInformation> getPaths() {
            return paths;
        }

        boolean isRedundantParent() {
            return redundantParent != null;
        }

        boolean isSplittingJoinNeeded() {
            return needToSplitJoin;
        }
    }
}
