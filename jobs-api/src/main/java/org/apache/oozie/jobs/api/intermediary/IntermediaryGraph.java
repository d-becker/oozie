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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class IntermediaryGraph {
    private final StartIntermediaryNode start = new StartIntermediaryNode("start");
    private final EndIntermediaryNode end = new EndIntermediaryNode("end");

    public IntermediaryGraph(final Workflow w) {
        toIntermediaryGraphMultipleRoots(w.getRoots().asList());
    }

    public StartIntermediaryNode getStart() {
        return start;
    }

    public EndIntermediaryNode getEnd() {
        return end;
    }

    public void convertToForkJoinFriendly() {
        new IntermediaryGraphConverter()
    }

    private IntermediaryNode getClosestMatchingFork(IntermediaryNode join,
                                                    Map<IntermediaryNode, List<IntermediaryNode>> upstreamForks) {
        List<IntermediaryNode> parents = join.getParents();

        int maxUpstreamForksLength = getMaxUpstreamForksLength(parents, upstreamForks);

        IntermediaryNode result = null;
        Set<IntermediaryNode> parentsToResult = new HashSet<>();

        for (int i = 0; i < maxUpstreamForksLength; ++i) {
            for (IntermediaryNode parent : parents) {
                if (i < upstreamForks.get(parent).size()) {
                    IntermediaryNode fork = upstreamForks.get(parent).get(i);

                    for (IntermediaryNode otherParent : parents) {
                        if (parent != otherParent && upstreamForks.get(otherParent).contains(fork)) {
                            if (result == null) {
                                result = fork;
                                parentsToResult.add(parent);
                                parentsToResult.add(otherParent);
                            } else if (result == fork) {
                                parentsToResult.add(otherParent);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private int getMaxUpstreamForksLength(List<IntermediaryNode> nodes,
                                          Map<IntermediaryNode, List<IntermediaryNode>> upstreamForks) {
        int max = 0;
        for (IntermediaryNode node : nodes) {
            List<IntermediaryNode> forksList = upstreamForks.get(node);
            if (forksList != null && forksList.size() > max) {
                max = forksList.size();
            }
        }

        return max;
    }



    private void toIntermediaryGraphMultipleRoots(final List<Node> roots) {
        final Map<Node, IntermediaryNode> cache = new HashMap<>();

        for (Node root : roots) {
            IntermediaryNode transformed = toIntermediaryNodeSingleRoot(root, cache);
            this.start.addChild(transformed);
        }
    }

    private RealIntermediaryNode toIntermediaryNodeSingleRoot(final Node root, final Map<Node, IntermediaryNode> cache) {
        final RealIntermediaryNode result = new RealIntermediaryNode(root.getName(), root);

        for (Node child : root.getChildren()) {
            IntermediaryNode IChild = null;
            if (child != null) {
                if (cache.containsKey(child)) {
                    IChild = cache.get(child);
                }
                else {
                    IChild = toIntermediaryNodeSingleRoot(child, cache);
                    cache.put(child, IChild);
                }

                IChild.addParent(result);
            }

        }

        if (root.getChildren().isEmpty()) {
            result.addChild(this.end);
        }

        return result;
    }
}
