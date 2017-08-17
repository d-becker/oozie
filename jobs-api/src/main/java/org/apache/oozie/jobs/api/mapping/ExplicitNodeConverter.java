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

import com.google.common.collect.ImmutableMap;
import org.apache.oozie.jobs.api.action.Action;
import org.apache.oozie.jobs.api.action.EmailAction;
import org.apache.oozie.jobs.api.action.FSAction;
import org.apache.oozie.jobs.api.action.MapReduceAction;
import org.apache.oozie.jobs.api.action.Node;
import org.apache.oozie.jobs.api.action.SubWorkflowAction;
import org.apache.oozie.jobs.api.generated.workflow.ACTION;
import org.apache.oozie.jobs.api.generated.workflow.ACTIONTRANSITION;
import org.apache.oozie.jobs.api.generated.workflow.FS;
import org.apache.oozie.jobs.api.generated.workflow.JAVA;
import org.apache.oozie.jobs.api.generated.workflow.MAPREDUCE;
import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.generated.workflow.PIG;
import org.apache.oozie.jobs.api.generated.workflow.SUBWORKFLOW;
import org.apache.oozie.jobs.api.oozie.dag.ExplicitNode;
import org.apache.oozie.jobs.api.oozie.dag.NodeBase;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

import java.util.Map;

public class ExplicitNodeConverter extends DozerConverter<ExplicitNode, ACTION> implements MapperAware{
    private static final ObjectFactory workflowObjectFactory = new ObjectFactory();

    private static final Map<Class<? extends Action>, Class<? extends Object>> actionClassMap = initActionClassMap();

    private static Map<Class<? extends Action>, Class<? extends Object>> initActionClassMap() {
        final ImmutableMap.Builder<Class<? extends Action>, Class<? extends Object>> builder = new ImmutableMap.Builder<>();

        builder.put(MapReduceAction.class, MAPREDUCE.class)
                .put(SubWorkflowAction.class, SUBWORKFLOW.class)
                .put(FSAction.class, FS.class)
                .put(EmailAction.class, org.apache.oozie.jobs.api.generated.action.email.ACTION.class);

        return builder.build();
    }

    private Mapper mapper;

    public ExplicitNodeConverter() {
        super(ExplicitNode.class, ACTION.class);
    }

    @Override
    public ACTION convertTo(ExplicitNode source, ACTION destination) {
        if (destination == null) {
            destination = workflowObjectFactory.createACTION();
        }

        mapName(source, destination);
        mapTransitions(source, destination);
        mapActionContent(source, destination);

        return destination;
    }

    @Override
    public ExplicitNode convertFrom(ACTION source, ExplicitNode destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    private void mapName(final ExplicitNode source, final ACTION destination) {
        destination.setName(source.getName());
    }

    private void mapTransitions(final ExplicitNode source, final ACTION destination) {
        // Error transitions are handled at the level of converting the Graph object to a WORKFLOWAPP object.
        final ACTIONTRANSITION ok = workflowObjectFactory.createACTIONTRANSITION();
        final NodeBase child = source.getChild();
        ok.setTo(child == null ? "" : child.getName());

        destination.setOk(ok);
    }

    private void mapActionContent(final ExplicitNode source, final ACTION destination) {
        final Node realNode = source.getRealNode();

        Object actionTypeObject = null;
        if (actionClassMap.containsKey(realNode.getClass())) {
            final Class<? extends Object> mappedClass = actionClassMap.get(realNode.getClass());
            actionTypeObject = mapper.map(realNode, mappedClass);
        }

        if (actionTypeObject instanceof MAPREDUCE) {
            destination.setMapReduce((MAPREDUCE) actionTypeObject);
        }
        else if (actionTypeObject instanceof PIG) {
            destination.setPig((PIG) actionTypeObject);
        }
        else if (actionTypeObject instanceof SUBWORKFLOW) {
            destination.setSubWorkflow((SUBWORKFLOW) actionTypeObject);
        }
        else if (actionTypeObject instanceof FS) {
            destination.setFs((FS) actionTypeObject);
        }
        else if (actionTypeObject instanceof JAVA) {
            destination.setJava((JAVA) actionTypeObject);
        }
        else {
            destination.setOther(actionTypeObject);
        }
    }
}
