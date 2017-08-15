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

import org.apache.oozie.jobs.api.generated.workflow.FORK;
import org.apache.oozie.jobs.api.generated.workflow.FORKTRANSITION;
import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.oozie.dag.Fork;
import org.apache.oozie.jobs.api.oozie.dag.NodeBase;
import org.dozer.DozerConverter;

import java.util.List;

public class ForkConverter extends DozerConverter<Fork, FORK> {
    public ForkConverter() {
        super(Fork.class, FORK.class);
    }

    @Override
    public FORK convertTo(Fork source, FORK destination) {
        if (destination == null) {
            destination = new ObjectFactory().createFORK();
        }

        destination.setName(source.getName());

        List<FORKTRANSITION> transitions = destination.getPath();
        for (NodeBase child : source.getChildren()) {
            final NodeBase realChild = MappingUtils.getRealChild(child);
            transitions.add(convertToFORKTRANSITION(realChild));
        }

        return destination;
    }

    @Override
    public Fork convertFrom(FORK source, Fork destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }

    private FORKTRANSITION convertToFORKTRANSITION(NodeBase source) {
        final FORKTRANSITION destination = new ObjectFactory().createFORKTRANSITION();

        final NodeBase realChild = MappingUtils.getRealChild(source);

        destination.setStart(realChild.getName());

        return destination;
    }
}
