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
import org.apache.oozie.jobs.api.action.MapReduceActionBuilder;
import org.apache.oozie.jobs.api.generated.workflow.WORKFLOWAPP;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.dozer.DozerBeanMapper;

import java.util.ArrayList;
import java.util.List;

public class Mappings {
    public static void main(String[] args) {
        final MapReduceAction mr1 = new MapReduceActionBuilder().withName("mr1").build();
        final MapReduceAction mr2 = new MapReduceActionBuilder().withName("mr2").build();

        Workflow workflow = new WorkflowBuilder()
                .withName("Workflow_to_map")
                .withDagContainingNode(mr1)
                .build();
        Graph graph = new Graph(workflow);

        List<String> mappingFiles = new ArrayList<>();
        mappingFiles.add("dozer_config.xml");
        mappingFiles.add("mappingGraphToWORKFLOWAPP.xml");

        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.setMappingFiles(mappingFiles);
        WORKFLOWAPP workflowapp = mapper.map(graph, WORKFLOWAPP.class);

        System.out.println(workflowapp.getName());
    }
}
