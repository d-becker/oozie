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
import org.apache.oozie.jobs.api.action.*;
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
import com.google.common.base.Preconditions;

import javax.xml.bind.JAXBElement;
import java.util.Map;

public class ExplicitNodeConverter extends DozerConverter<ExplicitNode, ACTION> implements MapperAware {
    private static final ObjectFactory WORKFLOW_OBJECT_FACTORY = new ObjectFactory();

    private static final Map<Class<? extends Node>, Class<? extends Object>> ACTION_CLASSES = initActionClasses();

    private static Map<Class<? extends Node>, Class<? extends Object>> initActionClasses() {
        final ImmutableMap.Builder<Class<? extends Node>, Class<? extends Object>> builder = new ImmutableMap.Builder<>();

        builder.put(MapReduceAction.class, MAPREDUCE.class)
                .put(SubWorkflowAction.class, SUBWORKFLOW.class)
                .put(FSAction.class, FS.class)
                .put(EmailAction.class, org.apache.oozie.jobs.api.generated.action.email.ACTION.class)
                .put(DistcpAction.class, org.apache.oozie.jobs.api.generated.action.distcp.ACTION.class)
                .put(HiveAction.class, org.apache.oozie.jobs.api.generated.action.hive.ACTION.class)
                .put(Hive2Action.class, org.apache.oozie.jobs.api.generated.action.hive2.ACTION.class)
                .put(SparkAction.class, org.apache.oozie.jobs.api.generated.action.spark.ACTION.class);

        return builder.build();
    }

    private Mapper mapper;

    public ExplicitNodeConverter() {
        super(ExplicitNode.class, ACTION.class);
    }

    @Override
    public ACTION convertTo(final ExplicitNode source, ACTION destination) {
        destination = ensureDestination(destination);

        mapName(source, destination);

        mapTransitions(source, destination);

        mapActionContent(source, destination);

        return destination;
    }

    private ACTION ensureDestination(ACTION destination) {
        if (destination == null) {
            destination = WORKFLOW_OBJECT_FACTORY.createACTION();
        }
        return destination;
    }

    @Override
    public ExplicitNode convertFrom(final ACTION source, final ExplicitNode destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }

    @Override
    public void setMapper(final Mapper mapper) {
        this.mapper = mapper;
    }

    private void mapName(final ExplicitNode source, final ACTION destination) {
        destination.setName(source.getName());
    }

    private void mapTransitions(final ExplicitNode source, final ACTION destination) {
        // Error transitions are handled at the level of converting the Graph object to a WORKFLOWAPP object.
        final ACTIONTRANSITION ok = WORKFLOW_OBJECT_FACTORY.createACTIONTRANSITION();
        final NodeBase child = source.getChild();
        ok.setTo(child == null ? "" : child.getName());

        destination.setOk(ok);
    }

    private void mapActionContent(final ExplicitNode source, final ACTION destination) {
        final Node realNode = source.getRealNode();

        Object actionTypeObject = null;
        if (ACTION_CLASSES.containsKey(realNode.getClass())) {
            final Class<? extends Object> mappedClass = ACTION_CLASSES.get(realNode.getClass());
            actionTypeObject = mapper.map(realNode, mappedClass);
        }

        Preconditions.checkNotNull(actionTypeObject, "actionTypeObject");

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
        else if (actionTypeObject instanceof org.apache.oozie.jobs.api.generated.action.email.ACTION) {
            setEmail((org.apache.oozie.jobs.api.generated.action.email.ACTION) actionTypeObject, destination);
        }
        else if (actionTypeObject instanceof org.apache.oozie.jobs.api.generated.action.distcp.ACTION) {
            setDistcp((org.apache.oozie.jobs.api.generated.action.distcp.ACTION) actionTypeObject, destination);
        }
        else if (actionTypeObject instanceof org.apache.oozie.jobs.api.generated.action.hive.ACTION) {
            setHive((org.apache.oozie.jobs.api.generated.action.hive.ACTION) actionTypeObject, destination);
        }
        else if (actionTypeObject instanceof org.apache.oozie.jobs.api.generated.action.hive2.ACTION) {
            setHive2((org.apache.oozie.jobs.api.generated.action.hive2.ACTION) actionTypeObject, destination);
        }
        else if (actionTypeObject instanceof org.apache.oozie.jobs.api.generated.action.spark.ACTION) {
                setSpark((org.apache.oozie.jobs.api.generated.action.spark.ACTION) actionTypeObject, destination);
        }
    }

    private void setEmail(final org.apache.oozie.jobs.api.generated.action.email.ACTION source, final ACTION destination) {
        final JAXBElement jaxbElement
                = new org.apache.oozie.jobs.api.generated.action.email.ObjectFactory().createEmail(
                source);
        destination.setOther(jaxbElement);
    }

    private void setDistcp(final org.apache.oozie.jobs.api.generated.action.distcp.ACTION source, final ACTION destination) {
        final JAXBElement<org.apache.oozie.jobs.api.generated.action.distcp.ACTION> jaxbElement
                = new org.apache.oozie.jobs.api.generated.action.distcp.ObjectFactory().createDistcp(
                source);

        destination.setOther(jaxbElement);
    }

    private void setHive(final org.apache.oozie.jobs.api.generated.action.hive.ACTION source, final ACTION destination) {
        final JAXBElement<org.apache.oozie.jobs.api.generated.action.hive.ACTION> jaxbElement
                = new org.apache.oozie.jobs.api.generated.action.hive.ObjectFactory().createHive(
                source);

        destination.setOther(jaxbElement);
    }

    private void setHive2(final org.apache.oozie.jobs.api.generated.action.hive2.ACTION source, final ACTION destination) {
        final JAXBElement<org.apache.oozie.jobs.api.generated.action.hive2.ACTION> jaxbElement
                = new org.apache.oozie.jobs.api.generated.action.hive2.ObjectFactory().createHive2(
                source);

        destination.setOther(jaxbElement);
    }

    private void setSpark(final org.apache.oozie.jobs.api.generated.action.spark.ACTION source, final ACTION destination) {
        final JAXBElement<org.apache.oozie.jobs.api.generated.action.spark.ACTION> jaxbElement
                = new org.apache.oozie.jobs.api.generated.action.spark.ObjectFactory().createSpark(
                source);

        destination.setOther(jaxbElement);
    }
}