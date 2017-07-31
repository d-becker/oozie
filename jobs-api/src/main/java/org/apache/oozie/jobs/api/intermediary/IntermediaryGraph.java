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

        for (IntermediaryNode node : nodesByName.values()) {
            if (node.getChildren().isEmpty() && node != end) {
                // TODO: add join if necessary.
                end.addParent(node);
            }
        }

    }

    private void handleNonJoinNode(final Node originalNode, final Map<Node, IntermediaryNode> mappings) {
        IntermediaryNode convertedNode = mappings.get(originalNode);
        Node parent = originalNode.getParents().isEmpty() ? null : originalNode.getParents().get(0);
        IntermediaryNode mappedParent = parent == null ? start : mappings.get(parent);

        IntermediaryNode newParent = reachableThrough(mappedParent);
        if (newParent == null) {
            List<IntermediaryNode> children = mappedParent.getChildren();

            newParent = getNewForkNode();

            for (IntermediaryNode child : children) {
                child.removeParent(mappedParent);
                child.addParent(newParent);
            }

            newParent.addParent(mappedParent);
        }

        convertedNode.addParent(newParent);
    }

    private void handleJoinNode(final Node originalNode, final Map<Node, IntermediaryNode> mappings) {
        IntermediaryNode convertedNode = mappings.get(originalNode);
        Set<IntermediaryNode> siblings = new HashSet<>();
        for (Node originalParent : originalNode.getParents()) {
            IntermediaryNode mappedParent = mappings.get(originalParent);
            siblings.addAll(mappedParent.getChildren());
        }

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

    private IntermediaryNode reachableThrough(IntermediaryNode node) {
        if (isDirectlyReachable(node)) {
            return node;
        }
        else {
            return reachableThroughJoinAndFork(node);
        }
    }

    private ForkIntermediaryNode reachableThroughJoinAndFork(IntermediaryNode node) {
        if (node instanceof EndIntermediaryNode) {
            return null;
        }
        else if (node instanceof ForkIntermediaryNode) {
            return (ForkIntermediaryNode) node;
        }
        else if (node instanceof JoinIntermediaryNode) {
            JoinIntermediaryNode join = (JoinIntermediaryNode) node;
            IntermediaryNode child = join.getChild();
            if (child instanceof ForkIntermediaryNode) {
                return (ForkIntermediaryNode) child;
            }
        }
        else if (node instanceof NormalIntermediaryNode) {
            NormalIntermediaryNode normal = (NormalIntermediaryNode) node;
            IntermediaryNode child = normal.getChild();

            if (child instanceof ForkIntermediaryNode) {
                return (ForkIntermediaryNode) child;
            }
            else if (child instanceof JoinIntermediaryNode) {
                IntermediaryNode grandchild = ((JoinIntermediaryNode) child).getChild();

                if (grandchild instanceof ForkIntermediaryNode) {
                    return (ForkIntermediaryNode) grandchild;
                }
                else {
                    ForkIntermediaryNode fork = getNewForkNode();
                    grandchild.removeParent(child);
                    fork.addParent(child);
                    grandchild.addParent(fork);
                    return fork;
                }
            }
        }
        else if (node instanceof StartIntermediaryNode) {
            StartIntermediaryNode start = (StartIntermediaryNode) node;
            IntermediaryNode child = start.getChild();

            if (child instanceof ForkIntermediaryNode) {
                return (ForkIntermediaryNode) child;
            }
            else if (child instanceof JoinIntermediaryNode) {
                IntermediaryNode grandchild = ((JoinIntermediaryNode) child).getChild();

                if (grandchild instanceof ForkIntermediaryNode) {
                    return (ForkIntermediaryNode) grandchild;
                }
                else {
                    return null;
                }
            }
        }
        else {
            return null;
        }

        return null;
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

    private static boolean isDirectlyReachable(IntermediaryNode node) {
        if (node instanceof EndIntermediaryNode) {
            return true;
        }
        else if (node instanceof ForkIntermediaryNode) {
            ForkIntermediaryNode fork = (ForkIntermediaryNode) node;
            return fork.getChildren().isEmpty();
        }
        else if (node instanceof JoinIntermediaryNode) {
            JoinIntermediaryNode join = (JoinIntermediaryNode) node;
            return join.getChild() == null;
        }
        else if (node instanceof NormalIntermediaryNode) {
            NormalIntermediaryNode normal = (NormalIntermediaryNode) node;
            return normal.getChild() == null;
        }
        else if (node instanceof StartIntermediaryNode) {
            StartIntermediaryNode start = (StartIntermediaryNode) node;
            return start.getChild() == null;
        }
        else {
            return false;
        }
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
