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

package org.apache.oozie.jobs.api.intermediary;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.oozie.jobs.api.Node;
import org.apache.oozie.jobs.api.Workflow;
import org.apache.oozie.jobs.api.generated.FORK;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IntermediaryGraph {
    private int forkCounter = 1;

    private final Map<ForkIntermediaryNode, Integer> forkNumbers = new HashMap<>();
    private final StartIntermediaryNode start = new StartIntermediaryNode("start");
    private final EndIntermediaryNode end = new EndIntermediaryNode("end");

    // TODO: ensure no duplicate names are present.
    private Map<String, IntermediaryNode> nodesByName;

    public IntermediaryGraph(final Workflow workflow) {
        List<Node> nodes = getNodesInTopologicalOrder(workflow);
        nodesByName = new HashMap<>();
        nodesByName.put(start.getName(), start);
        nodesByName.put(end.getName(), end);
        convert(nodes);
    }

    public StartIntermediaryNode getStart() {
        return start;
    }

    public EndIntermediaryNode getEnd() {
        return end;
    }

    public IntermediaryNode getNodeByName(final String name) {
        return nodesByName.get(name);
    }

    public Collection<IntermediaryNode> getNodes() {
        return nodesByName.values();
    }

    private void convert(final List<Node> nodes) {
        Map<Node, IntermediaryNode> mappings = new HashMap<>();

        for (Node originalNode : nodes) {
            NormalIntermediaryNode convertedNode = new NormalIntermediaryNode(originalNode.getName(), originalNode);
            mappings.put(originalNode, convertedNode);
            nodesByName.put(convertedNode.getName(), convertedNode);

            // We are not a join.
            if (originalNode.getParents().size() <= 1) {
                handleNonJoinNode(originalNode, mappings);
            }

            // We are a join.
            else {
                final List<IntermediaryNode> mappedParents = new ArrayList<>();

                for (Node originalParent : originalNode.getParents()) {
                    mappedParents.add(mappings.get(originalParent));
                }

                handleJoinNodeWithParents(convertedNode, mappedParents);
            }
        }

        List<IntermediaryNode> finalNodes = new ArrayList<>();
        for (IntermediaryNode node : nodesByName.values()) {
            if (node.getChildren().isEmpty() && node != end) {
                finalNodes.add(node);
            }
        }

        handleJoinNodeWithParents(end, finalNodes);
    }

    private void handleNonJoinNode(final Node originalNode, final Map<Node, IntermediaryNode> mappings) {
        IntermediaryNode convertedNode = mappings.get(originalNode);
        Node parent = originalNode.getParents().isEmpty() ? null : originalNode.getParents().get(0);
        IntermediaryNode mappedParent = parent == null ? start : mappings.get(parent);

        addParentWithForkIfNeeded(convertedNode, mappedParent);
    }

    private void handleJoinNodeWithParents(final IntermediaryNode node, final List<IntermediaryNode> parents) {
        Set<JoinIntermediaryNode> joinSiblings = new LinkedHashSet<>();
        for (IntermediaryNode parent : parents) {
            for (IntermediaryNode sibling : parent.getChildren()) {
                if (sibling instanceof JoinIntermediaryNode) {
                    joinSiblings.add((JoinIntermediaryNode) sibling);
                }
            }
        }

        if (joinSiblings.isEmpty()) {
            if (parents.size() == 1) {
                addParentWithForkIfNeeded(node, parents.get(0));
            }
            else {
                insertJoin(node, parents);
            }
        }
        else {
            final List<IntermediaryNode> parentsToRemove = new ArrayList<>();

            for (JoinIntermediaryNode join : joinSiblings) {
                parentsToRemove.addAll(join.getParents());
            }

            final List<IntermediaryNode> newParents = new ArrayList<>();
            newParents.addAll(joinSiblings);

            for (IntermediaryNode parent : parents) {
                if (!parentsToRemove.contains(parent)) {
                    newParents.add(parent);
                }
            }

            handleJoinNodeWithParents(node, newParents);
        }

    }

    private void insertJoin(final IntermediaryNode node, final List<IntermediaryNode> parents) {
        final List<PathInformation> paths = new ArrayList<>();
        for (IntermediaryNode parent : parents) {
            PathInformation path = getPathInfo(parent);
            paths.add(path);
        }

        Pair<ForkIntermediaryNode, List<PathInformation>> toClose = getOneForkToClose(paths);

        if (toClose.getRight().size() == paths.size()) {
            // There are no intermediary fork / join pairs to insert, we have to join all paths in a single join.
            // TODO.

            JoinIntermediaryNode newJoin = joinPaths(toClose.getLeft(), toClose.getRight());

            node.addParent(newJoin);
        } else {
            // We have to close a subset of the paths.
            List<IntermediaryNode> newParents = new ArrayList<>(parents);
            List<IntermediaryNode> parentsInToClose = new ArrayList<>();

            for (PathInformation path : toClose.getRight()) {
                parentsInToClose.add(path.getBottom());
                newParents.remove(path.getBottom());
            }

            JoinIntermediaryNode newJoin = joinPaths(toClose.getLeft(), toClose.getRight());

            newParents.add(newJoin);

            insertJoin(node, newParents);
        }
    }

    private JoinIntermediaryNode joinPaths(final ForkIntermediaryNode fork, List<PathInformation> paths) {
        JoinIntermediaryNode newJoin = null;

        // Check if we have to divide the fork.
        if (paths.size() < fork.getChildren().size()) {
            // Dividing the fork.
            newJoin = divideForkAndCloseSubFork(fork, paths);
        } else {
            // We don't divide the fork.
            newJoin = getNewJoinNode(fork);

            for (PathInformation path : paths) {
                newJoin.addParent(path.getBottom());
            }
        }

        // TODO: take care of not just siblings but uncles etc..
        for (PathInformation path : paths) {
            IntermediaryNode parent = path.getBottom();
            // TODO: It is possible that we process a child multiple times, because it may have multiple parents.
            for (IntermediaryNode childOfParent : parent.getChildren()) {
                if (childOfParent != newJoin) {
                    childOfParent.clearParents();
                    addParentWithForkIfNeeded(childOfParent, newJoin);
                }
            }
        }

        return newJoin;
    }

    private JoinIntermediaryNode divideForkAndCloseSubFork(final ForkIntermediaryNode correspondingFork,
                                                           final List<PathInformation> paths) {
        ForkIntermediaryNode newFork = getNewForkNode();
        for (PathInformation path : paths) {
            int indexOfFork = path.indexOfFork(correspondingFork);
            IntermediaryNode childOfOriginalFork = path.getForksAndDirections().get(indexOfFork).getDirectionDownstreams();

            childOfOriginalFork.clearParents();
            childOfOriginalFork.addParent(newFork);
        }

        newFork.addParent(correspondingFork);

        JoinIntermediaryNode newJoin = getNewJoinNode(newFork);

        for (PathInformation path : paths) {
            newJoin.addParent(path.getBottom());
        }

        return newJoin;
    }

//    private JoinIntermediaryNode divideForkAndCloseSubFork(final ForkIntermediaryNode correspondingFork,
//                                                           final List<IntermediaryNode> parentsInPartition,
//                                                           final List<IntermediaryNode> directionsInPartition) {
//        ForkIntermediaryNode newFork = getNewForkNode();
//        for (IntermediaryNode childOfOriginalFork : directionsInPartition) {
//            childOfOriginalFork.clearParents();
//            childOfOriginalFork.addParent(newFork);
//        }
//
//        newFork.addParent(correspondingFork);
//
//        JoinIntermediaryNode newJoin = getNewJoinNode(newFork);
//
//        for (IntermediaryNode parentInPartition : parentsInPartition) {
//            newJoin.addParent(parentInPartition);
//        }
//
//        return newJoin;
//    }

    @Deprecated
    private Map<ForkIntermediaryNode, List<PathInformation>>
    partitionParentsByUpstreamForks(List<IntermediaryNode> parents) {
        final List<PathInformation> paths = new ArrayList<>();
        int longestPathLength = 0;

        for (IntermediaryNode parent : parents) {
            final PathInformation pathInfo = getPathInfo(parent);
            paths.add(pathInfo);
            longestPathLength = Math.max(longestPathLength, pathInfo.getForksAndDirections().size());
        }

        Map<ForkIntermediaryNode, List<PathInformation>> partitions = new LinkedHashMap<>();

        for (int i = 0; i < longestPathLength && !paths.isEmpty(); ++i) { // TODO: We're doing much more work than necessary here.
            final Map<ForkIntermediaryNode, List<PathInformation>> partitionsFoundAtThisLevel = getPartitionsFoundAtLevelN(i, paths);
            partitions.putAll(partitionsFoundAtThisLevel);
        }

        return partitions;
    }

    private Pair<ForkIntermediaryNode, List<PathInformation>> getOneForkToClose(final List<PathInformation> paths) {
        for (int i = 0; ; ++i) { // Upper bound: we always found a fork eventually, if not else then the uppermost join.
            Pair<ForkIntermediaryNode, List<PathInformation>> foundAtThisLevel = getOneForkToCloseAtLevelN(i, paths);

            if (foundAtThisLevel != null) {
                return foundAtThisLevel;
            }
        }
    }

    private Pair<ForkIntermediaryNode, List<PathInformation>> getOneForkToCloseAtLevelN(final int n,
                                                                                        final List<PathInformation> paths) {
        for (PathInformation path : paths) {
            if (n < path.getForksAndDirections().size()) {
                ForkIntermediaryNode currentFork = path.getForksAndDirections().get(n).getFork();
                List<PathInformation> pathsMeetingAtCurrentFork = getPathsContainingFork(currentFork, paths);

                if (pathsMeetingAtCurrentFork.size() > 1) {
                    return new ImmutablePair<>(currentFork, pathsMeetingAtCurrentFork);
                }
            }
        }

        return null;
    }

    @Deprecated
    private Map<ForkIntermediaryNode, List<PathInformation>> getPartitionsFoundAtLevelN(final int n, final List<PathInformation> paths) {
        final Map<ForkIntermediaryNode, List<PathInformation>> result = new HashMap<>();

        List<PathInformation> removeAfterForLoop = null;
        while (true) {
            for (PathInformation path : paths) {
                if (n < path.getForksAndDirections().size()) {
                    ForkIntermediaryNode currentFork = path.getForksAndDirections().get(n).getFork();
                    List<PathInformation> pathsMeetingAtCurrentFork = getPathsContainingFork(currentFork, paths);

                    if (pathsMeetingAtCurrentFork.size() > 1) {
                        removeAfterForLoop = pathsMeetingAtCurrentFork;
                        result.put(currentFork, pathsMeetingAtCurrentFork);
                        break;
                    }
                }
            }

            if (removeAfterForLoop == null) {
                break;
            } else {
                paths.removeAll(removeAfterForLoop);
                removeAfterForLoop = null;
            }
        }

        return result;
    }

    private List<PathInformation> getPathsContainingFork(final ForkIntermediaryNode fork, List<PathInformation> paths) {
        List<PathInformation> result = new ArrayList<>();

        for (PathInformation pathInfo : paths) {
            if (pathInfo.containsFork(fork)) {
                result.add(pathInfo);
            }
        }

        return result;
    }

    private PathInformation getPathInfo(final IntermediaryNode node) {
        IntermediaryNode previous = null;
        IntermediaryNode current = node;

        List<ForkAndDirection> forksAndDirections = new ArrayList<>();

        while (current != start) {
            if (current instanceof JoinIntermediaryNode) {
                // Get the fork corresponding to this join and go towards that.
                ForkIntermediaryNode correspondingFork = ((JoinIntermediaryNode) current).getCorrespondingFork();
                previous = correspondingFork;
                current = getSingleParent(correspondingFork);
            } else {
                if (current instanceof ForkIntermediaryNode && current != node) {
                    ForkAndDirection forkAndDirection = new ForkAndDirection((ForkIntermediaryNode) current, previous);
                    forksAndDirections.add(forkAndDirection);
                }

                previous = current;
                current = getSingleParent(current);
            }
        }

        return new PathInformation(node, forksAndDirections);
    }

    private IntermediaryNode getSingleParent(final IntermediaryNode node) {
        if (node instanceof EndIntermediaryNode) {
            return ((EndIntermediaryNode) node).getParent();
        } else if (node instanceof ForkIntermediaryNode) {
            return ((ForkIntermediaryNode) node).getParent();
        } else if (node instanceof NormalIntermediaryNode) {
            return ((NormalIntermediaryNode) node).getParent();
        } else if (node instanceof StartIntermediaryNode) {
            throw new IllegalStateException("The start has no parent.");
        } else if (node instanceof JoinIntermediaryNode) {
            JoinIntermediaryNode join = (JoinIntermediaryNode) node;
            int numberOfParents = join.getParents().size();
            if (numberOfParents != 1) {
                throw new IllegalStateException("The start called '" + node.getName()
                        + "' has " + numberOfParents + " parents instead of 1.");
            }

            return join.getParents().get(0);
        }

        throw new IllegalArgumentException("Unknown start type.");
    }

    private void addParentWithForkIfNeeded(IntermediaryNode node, IntermediaryNode parent) {
        if (parent.getChildren().isEmpty() || parent instanceof ForkIntermediaryNode) {
            node.addParent(parent);
        } else {
            // If there is no child, we never get to this point.
            // There is only one child, otherwise it is a fork and we don't get here.
            IntermediaryNode child = parent.getChildren().get(0);

            if (child instanceof ForkIntermediaryNode) {
                node.addParent(child);
            }
            else if (child instanceof JoinIntermediaryNode) {
                // TODO: It's possible that we don't really need recursion here.
                addParentWithForkIfNeeded(node, child);
            }
            else {
                final ForkIntermediaryNode newFork = getNewForkNode();
                child.removeParent(parent);
                child.addParent(newFork);
                node.addParent(newFork);
                newFork.addParent(parent);
            }
        }
    }

    public String toDot() {
        return toDot(nodesByName.values());
    }

    public static String toDot(Collection<IntermediaryNode> nodes) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        for (IntermediaryNode node : nodes) {
            List<IntermediaryNode> children = node.getChildren();

            for (IntermediaryNode child : children) {
                builder.append(node.getName() + "->" + child.getName() + "\n");
            }
        }

        builder.append("}");

        return builder.toString();
    }

    private ForkIntermediaryNode getNewForkNode() {
        ForkIntermediaryNode fork = new ForkIntermediaryNode("fork" + forkCounter);
        forkNumbers.put(fork, forkCounter);
        forkCounter++;
        nodesByName.put(fork.getName(), fork);

        return fork;
    }

    private JoinIntermediaryNode getNewJoinNode(ForkIntermediaryNode correspondingFork) {
        JoinIntermediaryNode join = new JoinIntermediaryNode("join" + forkNumbers.get(correspondingFork), correspondingFork);
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
        private final IntermediaryNode bottom;
        private final List<ForkAndDirection> forksAndDirections;

        public PathInformation(final IntermediaryNode start,
                               final List<ForkAndDirection> forksAndDirections) {
            this.bottom = start;
            this.forksAndDirections = new ImmutableList.Builder<ForkAndDirection>().addAll(forksAndDirections).build();
        }

        public IntermediaryNode getBottom() {
            return bottom;
        }

        public List<ForkAndDirection> getForksAndDirections() {
            return forksAndDirections;
        }

        public int indexOfFork(final ForkIntermediaryNode fork) {
            for (int i = 0; i < getForksAndDirections().size(); ++i) {
                if (getForksAndDirections().get(i).getFork() == fork) {
                    return i;
                }
            }

            return -1;
        }

        public boolean containsFork(final ForkIntermediaryNode fork) {
            for (ForkAndDirection forkAndDirection : forksAndDirections) {
                if (forkAndDirection.getFork() == fork) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class ForkAndDirection {
        private final ForkIntermediaryNode fork;
        private final IntermediaryNode directionDownstreams;

        public ForkAndDirection(final ForkIntermediaryNode fork, final IntermediaryNode directionDownstreams) {
            this.fork = fork;
            this.directionDownstreams = directionDownstreams;
        }

        public ForkIntermediaryNode getFork() {
            return fork;
        }

        public IntermediaryNode getDirectionDownstreams() {
            return directionDownstreams;
        }
    }

}
