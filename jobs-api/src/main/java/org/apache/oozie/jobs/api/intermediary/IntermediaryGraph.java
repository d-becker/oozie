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

import org.apache.oozie.jobs.api.Node;
import org.apache.oozie.jobs.api.Workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
                handleJoinNode(originalNode, mappings);
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
            JoinIntermediaryNode finalJoin = getNewJoinNode();
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

    private void handleJoinNode(final Node originalNode, final Map<Node, IntermediaryNode> mappings) {
        IntermediaryNode convertedNode = mappings.get(originalNode);
        Set<IntermediaryNode> siblings = new HashSet<>();
        for (Node originalParent : originalNode.getParents()) {
            IntermediaryNode mappedParent = mappings.get(originalParent);
            siblings.addAll(mappedParent.getChildren());
        }

        // TODO: Reuse join if possible, take care of correct fork / join pairs.
        JoinIntermediaryNode join = getNewJoinNode();
        for (Node originalParent : originalNode.getParents()) {
            IntermediaryNode mappedParent = mappings.get(originalParent);
            join.addParent(mappedParent);
        }

        if (siblings.isEmpty()) {
            convertedNode.addParent(join);
        }
        else {
            ForkIntermediaryNode fork = getNewForkNode();
            fork.addParent(join);

            for (IntermediaryNode sibling : siblings) {
                // sibling.removeParent();
                sibling.addParent(fork);
            }

            convertedNode.addParent(fork);
        }
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
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        for (IntermediaryNode node : nodesByName.values()) {
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

    private JoinIntermediaryNode getNewJoinNode() {
        JoinIntermediaryNode join = new JoinIntermediaryNode("join" + joinCounter++);
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

}
