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

import org.apache.oozie.jobs.api.action.ErrorHandler;
import org.apache.oozie.jobs.api.action.Node;
import org.apache.oozie.jobs.api.generated.workflow.ACTION;
import org.apache.oozie.jobs.api.generated.workflow.ACTIONTRANSITION;
import org.apache.oozie.jobs.api.generated.workflow.DECISION;
import org.apache.oozie.jobs.api.generated.workflow.END;
import org.apache.oozie.jobs.api.generated.workflow.FORK;
import org.apache.oozie.jobs.api.generated.workflow.JOIN;
import org.apache.oozie.jobs.api.generated.workflow.KILL;
import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.generated.workflow.START;
import org.apache.oozie.jobs.api.generated.workflow.WORKFLOWAPP;
import org.apache.oozie.jobs.api.oozie.dag.Decision;
import org.apache.oozie.jobs.api.oozie.dag.ExplicitNode;
import org.apache.oozie.jobs.api.oozie.dag.Fork;
import org.apache.oozie.jobs.api.oozie.dag.Join;
import org.apache.oozie.jobs.api.oozie.dag.NodeBase;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

import java.util.HashMap;
import java.util.Map;

public class NodesToWORKFLOWAPPConverter extends DozerConverter<Nodes, WORKFLOWAPP> implements MapperAware {
    private static ObjectFactory objectFactory = new ObjectFactory();

    private Mapper mapper;

    private Map<Class<? extends Object>, Class<? extends Object>> classMapping = new HashMap<>();

    public NodesToWORKFLOWAPPConverter() {
        super(Nodes.class, WORKFLOWAPP.class);

        classMapping.put(Decision.class, DECISION.class);
        classMapping.put(Fork.class, FORK.class);
        classMapping.put(Join.class, JOIN.class);
        classMapping.put(ExplicitNode.class, ACTION.class);
    }

    @Override
    public WORKFLOWAPP convertTo(final Nodes nodes, WORKFLOWAPP workflowapp) {
        if (workflowapp == null) {
            workflowapp = new ObjectFactory().createWORKFLOWAPP();
        }

        workflowapp.setName(nodes.getName());

        final START start = mapper.map(nodes.getStart(), START.class);
        workflowapp.setStart(start);

        final END end = mapper.map(nodes.getEnd(), END.class);
        workflowapp.setEnd(end);

        final KILL kill = createKillNode();
        workflowapp.getDecisionOrForkOrJoin().add(kill);

        for (NodeBase nodeBase : nodes.getNodes()) {
            convertNode(nodeBase, workflowapp, kill);
        }

        return workflowapp;
    }

    @Override
    public Nodes convertFrom(WORKFLOWAPP workflowapp, Nodes nodes) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    private void convertNode(final NodeBase nodeBase, final WORKFLOWAPP workflowapp, final KILL kill) {
        Class<? extends Object> sourceClass = nodeBase.getClass();
        if (classMapping.containsKey(sourceClass)) {
            Object mappedObject = mapper.map(nodeBase, classMapping.get(sourceClass));

            if (nodeBase instanceof ExplicitNode) {
                final ACTION errorHandlerAction = addErrorTransition((ExplicitNode) nodeBase, (ACTION) mappedObject, kill);
                if (errorHandlerAction != null) {
                    workflowapp.getDecisionOrForkOrJoin().add(errorHandlerAction);
                }
            }

            workflowapp.getDecisionOrForkOrJoin().add(mappedObject);
        }
    }

    private KILL createKillNode() {
        final KILL kill = objectFactory.createKILL();
        kill.setName("kill");
        kill.setMessage("Action failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");

        return kill;
    }

    private ACTION addErrorTransition(final ExplicitNode node, final ACTION action, final KILL kill) {
        ACTIONTRANSITION error = action.getError();

        if (error == null) {
            error = objectFactory.createACTIONTRANSITION();
            action.setError(error);
        }

        final ErrorHandler errorHandler = node.getRealNode().getErrorHandler();

        if (errorHandler == null) {
            error.setTo(kill.getName());

            return null;
        }
        else {
            final Node handlerNode = errorHandler.getHandlerNode();

            final ACTION handlerAction = createErrorHandlerAction(handlerNode, kill);

            error.setTo(handlerAction.getName());

            return handlerAction;
        }
    }

    private ACTION createErrorHandlerAction(final Node handlerNode, final KILL kill) {
        final ACTION handlerAction = mapper.map(handlerNode, ACTION.class);

        ACTIONTRANSITION ok = handlerAction.getOk();

        if (ok == null) {
            ok = objectFactory.createACTIONTRANSITION();
            handlerAction.setOk(ok);
        }

        ok.setTo(kill.getName());

        ACTIONTRANSITION error = handlerAction.getError();

        if (error == null) {
            error = objectFactory.createACTIONTRANSITION();
            handlerAction.setError(error);
        }

        error.setTo(kill.getName());

        return handlerAction;
    }
}
