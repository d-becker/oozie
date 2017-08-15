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

import org.apache.oozie.jobs.api.generated.workflow.CASE;
import org.apache.oozie.jobs.api.generated.workflow.DECISION;
import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.generated.workflow.SWITCH;
import org.apache.oozie.jobs.api.oozie.dag.DagNodeWithCondition;
import org.apache.oozie.jobs.api.oozie.dag.Decision;
import org.apache.oozie.jobs.api.oozie.dag.NodeBase;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

import java.util.List;

public class DecisionConverter extends DozerConverter<Decision, DECISION> implements MapperAware {
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private Mapper mapper;

    public DecisionConverter() {
        super(Decision.class, DECISION.class);
    }

    @Override
    public DECISION convertTo(Decision source, DECISION destination) {
        destination = assureNonNull(destination);

        mapName(source, destination);

        mapTransitions(source, destination);

        return destination;
    }

    @Override
    public Decision convertFrom(DECISION source, Decision destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    private void mapName(final Decision source, final DECISION destination) {
        final String name = source.getName();
        destination.setName(name);
    }

    private void mapTransitions(final Decision source, final DECISION destination) {
        final List<DagNodeWithCondition> children = source.getChildrenWithConditions();
        final List<CASE> cases = destination.getSwitch().getCase();

        for (DagNodeWithCondition childWithCondition : children) {
            final NodeBase child = childWithCondition.getNode();
            final NodeBase realChild = MappingUtils.getRealChild(child);

            final String condition = childWithCondition.getCondition();

            final DagNodeWithCondition realChildWithCondition = new DagNodeWithCondition(realChild, condition);

            final CASE mappedCase = mapper.map(realChildWithCondition, CASE.class);
            cases.add(mappedCase);
        }
    }

    private DECISION assureNonNull(final DECISION destination) {
        DECISION result = destination;
        if (result == null) {
            result = objectFactory.createDECISION();
        }

        if (result.getSwitch() == null) {
            final SWITCH _switch = objectFactory.createSWITCH();
            result.setSwitch(_switch);
        }

        return result;
    }
}
