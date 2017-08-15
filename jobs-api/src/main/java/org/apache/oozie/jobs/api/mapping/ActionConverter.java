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

import org.apache.oozie.jobs.api.generated.workflow.ACTION;
import org.apache.oozie.jobs.api.oozie.dag.ExplicitNode;
import org.dozer.DozerConverter;

// TODO: Handle kill nodes.
public class ActionConverter extends DozerConverter<ExplicitNode, ACTION> {
    public ActionConverter() {
        super(ExplicitNode.class, ACTION.class);
    }

    @Override
    public ACTION convertTo(ExplicitNode source, ACTION destination) {
        return null;
    }

    @Override
    public ExplicitNode convertFrom(ACTION source, ExplicitNode destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }
}
