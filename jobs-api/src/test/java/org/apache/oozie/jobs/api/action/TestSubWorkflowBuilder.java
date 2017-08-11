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

package org.apache.oozie.jobs.api.action;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestSubWorkflowBuilder extends TestActionBuilderBaseImpl<SubWorkflowAction, SubWorkflowActionBuilder> {
    @Override
    protected SubWorkflowActionBuilder getBuilderInstance() {
        return new SubWorkflowActionBuilder();
    }

    @Override
    protected SubWorkflowActionBuilder getBuilderInstance(SubWorkflowAction action) {
        return new SubWorkflowActionBuilder(action);
    }

    @Test
    public void testWithAppPath() {
        final String appPath = "/path/to/app";

        final SubWorkflowActionBuilder builder = new SubWorkflowActionBuilder();
        builder.withAppPath(appPath);

        final SubWorkflowAction action = builder.build();
        assertEquals(appPath, action.getAppPath());
    }

    @Test
    public void testWithAppPathCalledTwiceThrows() {
        final String appPath1 = "/path/to/app1";
        final String appPath2 = "/path/to/app2";

        final SubWorkflowActionBuilder builder = new SubWorkflowActionBuilder();
        builder.withAppPath(appPath1);

        expectedException.expect(IllegalStateException.class);
        builder.withAppPath(appPath2);
    }

    @Test
    public void testWithPropagatingConfiguration() {
        final SubWorkflowActionBuilder builder = new SubWorkflowActionBuilder();
        builder.withPropagatingConfiguration();

        final SubWorkflowAction action = builder.build();
        assertEquals(true, action.isPropagatingConfiguration());
    }

    @Test
    public void testWithPropagatingConfigurationCalledTwiceThrows() {
        final SubWorkflowActionBuilder builder = new SubWorkflowActionBuilder();
        builder.withPropagatingConfiguration();

        expectedException.expect(IllegalStateException.class);
        builder.withPropagatingConfiguration();
    }

    @Test
    public void testWithoutPropagatingConfiguration() {
        final SubWorkflowActionBuilder builder = new SubWorkflowActionBuilder();
        builder.withPropagatingConfiguration();

        final SubWorkflowAction action = builder.build();

        final SubWorkflowActionBuilder fromExistingBuilder = new SubWorkflowActionBuilder(action);

        fromExistingBuilder.withoutPropagatingConfiguration();

        final SubWorkflowAction modifiedAction = fromExistingBuilder.build();
        assertEquals(false, modifiedAction.isPropagatingConfiguration());
    }

    @Test
    public void testWithoutPropagatingConfigurationCalledTwiceThrows() {
        final SubWorkflowActionBuilder builder = new SubWorkflowActionBuilder();
        builder.withPropagatingConfiguration();

        final SubWorkflowAction action = builder.build();

        final SubWorkflowActionBuilder fromExistingBuilder = new SubWorkflowActionBuilder(action);

        fromExistingBuilder.withoutPropagatingConfiguration();

        expectedException.expect(IllegalStateException.class);
        fromExistingBuilder.withoutPropagatingConfiguration();
    }
}
