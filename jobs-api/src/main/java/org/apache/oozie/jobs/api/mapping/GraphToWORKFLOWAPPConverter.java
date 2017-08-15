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

package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.action.MapReduceAction;
import org.apache.oozie.jobs.api.action.Node;
import org.apache.oozie.jobs.api.generated.workflow.ACTION;
import org.apache.oozie.jobs.api.generated.workflow.DECISION;
import org.apache.oozie.jobs.api.generated.workflow.END;
import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.generated.workflow.START;
import org.apache.oozie.jobs.api.generated.workflow.WORKFLOWAPP;
import org.apache.oozie.jobs.api.oozie.dag.Decision;
import org.apache.oozie.jobs.api.oozie.dag.ExplicitNode;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.oozie.dag.NodeBase;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

import java.util.HashMap;
import java.util.Map;

public class GraphToWORKFLOWAPPConverter extends DozerConverter<Graph, WORKFLOWAPP> implements MapperAware {
    private Mapper mapper;

    private Map<Class<? extends Object>, Class<? extends Object>> classMapping = new HashMap<>();

    public GraphToWORKFLOWAPPConverter() {
        super(Graph.class, WORKFLOWAPP.class);

        classMapping.put(MapReduceAction.class, ACTION.class);
        classMapping.put(Decision.class, DECISION.class);
    }

    @Override
    public WORKFLOWAPP convertTo(Graph graph, WORKFLOWAPP workflowapp) {
        if (workflowapp == null) {
            workflowapp = new ObjectFactory().createWORKFLOWAPP();
        }

        workflowapp.setName(graph.getName());

        final START start = mapper.map(graph.getStart(), START.class);
        workflowapp.setStart(start);

        final END end = mapper.map(graph.getEnd(), END.class);
        workflowapp.setEnd(end);

        for (NodeBase nodeBase : graph.getNodes()) {
            if (nodeBase instanceof ExplicitNode) {
                ExplicitNode node = (ExplicitNode) nodeBase;

                if (classMapping.containsKey(node.getRealNode().getClass())) {
                    Object mappedObject = mapper.map(node, classMapping.get(node.getRealNode().getClass()));
                    workflowapp.getDecisionOrForkOrJoin().add(mappedObject);
                }
            }
            else {
                if (classMapping.containsKey(nodeBase.getClass())) {
                    Object mappedObject = mapper.map(nodeBase, classMapping.get(nodeBase.getClass()));
                    workflowapp.getDecisionOrForkOrJoin().add(mappedObject);
                }
            }
        }

        return workflowapp;
    }

    @Override
    public Graph convertFrom(WORKFLOWAPP workflowapp, Graph graph) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}
