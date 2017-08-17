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

public abstract class TestActionBuilderBaseImpl<A extends Action,
            B extends ActionBuilderBaseImpl<B> & Builder<A>>
        extends TestNodeBuilderBaseImpl<A, B>{

    @Test
    public void testFromExistingAction() {
        final B builder = getBuilderInstance();

        builder.withName(NAME);

        final ErrorHandler errorHandler = ErrorHandler.buildAsErrorHandler(
                MapReduceActionBuilder.create().withName("error-handler"));

        builder.withErrorHandler(errorHandler);

        final A action = builder.build();

        final B fromExistingBuilder = getBuilderInstance(action);

        final String newName = "fromExisting_" + NAME;
        fromExistingBuilder.withName(newName);

        final A modifiedAction = fromExistingBuilder.build();

        assertEquals(newName, modifiedAction.getName());
        assertEquals(errorHandler, modifiedAction.getErrorHandler());
    }
}
