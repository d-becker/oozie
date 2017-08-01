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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.oozie.jobs.api.Node;
import org.apache.oozie.jobs.api.Workflow;

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
    private int joinCounter = 1;
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

        if (finalNodes.size() == 1) {
            end.addParent(finalNodes.get(0));
        } else {
            JoinIntermediaryNode finalJoin = getNewJoinNode(null);
            for (IntermediaryNode finalNode : finalNodes) {
                finalJoin.addParent(finalNode);
            }

            end.addParent(finalJoin);
        }

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

    private void insertJoin(IntermediaryNode node, List<IntermediaryNode> parents) {
        final Map<ForkIntermediaryNode, List<NodeAndDirectionFromFork>> partitions
                = partitionParentsByNearestUpstreamFork(parents);
        final List<IntermediaryNode> newParents = new ArrayList<>();

        for (Map.Entry<ForkIntermediaryNode, List<NodeAndDirectionFromFork>> entry : partitions.entrySet()) {
            final ForkIntermediaryNode correspondingFork = entry.getKey();
            final List<NodeAndDirectionFromFork> parentsAndDirectionsInPartition = entry.getValue();

            if (parentsAndDirectionsInPartition.size() == 1) {
                newParents.add(parentsAndDirectionsInPartition.get(0).getNode());
            }
            else {
                // Check if we have to divide the fork.
                final List<IntermediaryNode> parentsInPartition = new ArrayList<>();
                final List<IntermediaryNode> directionsInPartition = new ArrayList<>();
                for (NodeAndDirectionFromFork parentAndDirection : parentsAndDirectionsInPartition) {
                    parentsInPartition.add(parentAndDirection.getNode());
                    directionsInPartition.add(parentAndDirection.getDirectionFromFork());
                }

                JoinIntermediaryNode newJoin = null;

                if (directionsInPartition.size() < correspondingFork.getChildren().size()) {
                    // Dividing the fork.
                    ForkIntermediaryNode newFork = getNewForkNode();
                    for (IntermediaryNode childOfOriginalFork : directionsInPartition) {
                        childOfOriginalFork.clearParents();
                        newFork.addParent(childOfOriginalFork);
                    }

                    newFork.addParent(correspondingFork);

                    newJoin = getNewJoinNode(newFork);

                    for (IntermediaryNode parentInPartition : parentsInPartition) {
                        newJoin.addParent(parentInPartition);

                    }

                    newParents.add(newJoin);
                }
                else {
                    // We don't divide the fork.
                    newJoin = getNewJoinNode(correspondingFork);

                    for (IntermediaryNode parentInPartition : parentsInPartition) {
                        newJoin.addParent(parentInPartition);
                    }

                    newParents.add(newJoin);
                }

                for (IntermediaryNode parent : parentsInPartition) {
                    // TODO: It is possible that we process a child multiple times, because it may have multiple parents.
                    for (IntermediaryNode childOfParent : parent.getChildren()) {
                        if (childOfParent != newJoin) {
                            childOfParent.clearParents();
                            addParentWithForkIfNeeded(childOfParent, newJoin);
                        }
                    }

                }
            }
        }

        handleJoinNodeWithParents(node, newParents);
    }

    private Map<ForkIntermediaryNode, List<NodeAndDirectionFromFork>>
    partitionParentsByNearestUpstreamFork(List<IntermediaryNode> parents) {
        Map<ForkIntermediaryNode, List<NodeAndDirectionFromFork>> partitions = new LinkedHashMap<>();

        for (IntermediaryNode parent : parents) {
            Pair<ForkIntermediaryNode, IntermediaryNode> nearestForkAndDirection = getNearestOpenUpstreamForkAndDirection(parent);
            ForkIntermediaryNode nearestFork = nearestForkAndDirection.getLeft();
            IntermediaryNode directionFromFork = nearestForkAndDirection.getRight();

            List<NodeAndDirectionFromFork> partition = partitions.get(nearestFork);

            if (partition == null) {
                partition = new ArrayList<>();
                partitions.put(nearestFork, partition);
            }

            partition.add(new NodeAndDirectionFromFork(parent, directionFromFork));
        }

        return partitions;
    }

    private Pair<ForkIntermediaryNode, IntermediaryNode> getNearestOpenUpstreamForkAndDirection(final IntermediaryNode node) {
        IntermediaryNode previous = null;
        IntermediaryNode current = node;

        while (!(current instanceof ForkIntermediaryNode) || current == node) {
            if (node instanceof JoinIntermediaryNode) {
                // Get the fork corresponding to this join and go towards that.
                ForkIntermediaryNode correspondingFork = ((JoinIntermediaryNode) node).getCorrespondingFork();
                previous = current;
                current = getSingleParent(correspondingFork);
            } else {
                previous = current;
                current = getSingleParent(current);
            }

        }

        return new ImmutablePair<>((ForkIntermediaryNode) current, previous);
    }

    private IntermediaryNode getSingleParent(final IntermediaryNode node) {
        if (node instanceof EndIntermediaryNode) {
            return ((EndIntermediaryNode) node).getParent();
        } else if (node instanceof ForkIntermediaryNode) {
            return ((ForkIntermediaryNode) node).getParent();
        } else if (node instanceof NormalIntermediaryNode) {
            return ((NormalIntermediaryNode) node).getParent();
        } else if (node instanceof StartIntermediaryNode) {
            throw new IllegalStateException("The node has no parent.");
        } else if (node instanceof JoinIntermediaryNode) {
            JoinIntermediaryNode join = (JoinIntermediaryNode) node;
            int numberOfParents = join.getParents().size();
            if (numberOfParents != 1) {
                throw new IllegalStateException("The node has " + numberOfParents + " parents instead of 1.");
            }

            return join.getParents().get(0);
        }

        throw new IllegalArgumentException("Unknown node type.");
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
                // TODO: Probably we don't really need recursion here.
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
        ForkIntermediaryNode fork = new ForkIntermediaryNode("fork" + forkCounter++);
        nodesByName.put(fork.getName(), fork);

        return fork;
    }

    private JoinIntermediaryNode getNewJoinNode(ForkIntermediaryNode correspondingFork) {
        JoinIntermediaryNode join = new JoinIntermediaryNode("join" + joinCounter++, correspondingFork);
        nodesByName.put(join.getName(), join);

        return join;
    }

    private static List<Node> getNodesInTopologicalOrder(final Workflow workflow) {
        final SetAndList<Node> nodes = new SetAndList<>(workflow.getRoots());

        for (int i = 0; i < nodes.size(); ++i) {
            final Node current  = nodes.get(i);

            for (Node child : current.getChildren()) {
                // Checking if every dependency has been processed, if not, we do not add the node to the list.
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

    private class NodeAndDirectionFromFork {
        private final IntermediaryNode node;
        private final IntermediaryNode directionFromFork;

        public NodeAndDirectionFromFork(IntermediaryNode node, IntermediaryNode directionFromFork) {
            this.node = node;
            this.directionFromFork = directionFromFork;
        }

        public IntermediaryNode getNode() {
            return node;
        }

        public IntermediaryNode getDirectionFromFork() {
            return directionFromFork;
        }
    }

}
