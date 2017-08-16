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

import org.apache.oozie.jobs.api.action.SubWorkflowAction;
import org.apache.oozie.jobs.api.action.SubWorkflowActionBuilder;
import org.apache.oozie.jobs.api.generated.workflow.SUBWORKFLOW;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestSubWorkflowActionMapping {
    @Test
    public void testMappingSubWorkflowAction() {
        final String appPath = "/path/to/app";

        final SubWorkflowAction action = new SubWorkflowActionBuilder()
                .withAppPath(appPath)
                .withPropagatingConfiguration()
                .withConfigProperty("propertyName", "propertyValue")
                .build();

        final SUBWORKFLOW subWorkflowAction = DozerMapperSingletonWrapper.getMapperInstance().map(action, SUBWORKFLOW.class);

        assertEquals(appPath, subWorkflowAction.getAppPath());
        assertNotNull(subWorkflowAction.getPropagateConfiguration());
        assertNotNull(subWorkflowAction.getConfiguration());
    }
}