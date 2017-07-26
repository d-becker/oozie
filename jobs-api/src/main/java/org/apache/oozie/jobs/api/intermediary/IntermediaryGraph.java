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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void toIntermediaryGraphMultipleRoots(List<Node> roots) {
        Map<Node, IntermediaryNode> cache = new HashMap<>();

        List<IntermediaryNode> transformedList = new ArrayList<>();

        for (Node root : roots) {
            IntermediaryNode transformed = toIntermediaryNodeSingleRoot(root, cache);

            this.start.addChild(transformed);

            transformedList.add(transformed);
        }
    }

    private RealIntermediaryNode toIntermediaryNodeSingleRoot(Node root, Map<Node, IntermediaryNode> cache) {
        RealIntermediaryNode result = new RealIntermediaryNode(root.getName(), root);

        for (Node child : root.getChildren()) {
            IntermediaryNode IChild = null;
            if (child != null) {
                if (cache.containsKey(child)) {
                    IChild = cache.get(child);
                } else {
                    IChild = toIntermediaryNodeSingleRoot(child, cache);
                    cache.put(child, IChild);
                }

                IChild.addParent(result);
            }

            result.addChild(IChild);
        }

        if (root.getChildren().isEmpty()) {
            result.addChild(this.end);
        }

        return result;
    }
}
