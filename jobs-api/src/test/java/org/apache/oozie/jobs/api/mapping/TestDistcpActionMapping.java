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

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.action.*;
import org.apache.oozie.jobs.api.generated.action.distcp.ACTION;
import org.apache.oozie.jobs.api.generated.workflow.FS;
import org.apache.oozie.jobs.api.generated.workflow.MAPREDUCE;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestDistcpActionMapping {
    @Test
    public void testMappingDistcpAction() {
        final String jobTracker = "${jobTracker}";
        final String nameNode = "${nameNode}";
        final String javaOpts = "-Dopt1 -Dopt2";
        final List<String> args = Arrays.asList("arg1", "arg2");

        final DistcpActionBuilder builder = DistcpActionBuilder.create();

        builder.withJobTracker(jobTracker)
                .withNameNode(nameNode)
                .withPrepare(new PrepareBuilder().build())
                .withJavaOpts(javaOpts);

        for (final String arg : args) {
            builder.withArg(arg);
        }

        builder.withConfigProperty("propertyName1", "propertyValue1")
                .withConfigProperty("propertyName2", "propertyValue2");

        final DistcpAction action = builder.build();

        final ACTION distcp = DozerMapperSingletonWrapper.instance().map(action, ACTION.class);

        assertEquals(jobTracker, distcp.getJobTracker());
        assertEquals(nameNode, distcp.getNameNode());
        assertNotNull(distcp.getPrepare());
        assertNotNull(distcp.getConfiguration());
        assertEquals(javaOpts, distcp.getJavaOpts());
        assertEquals(args, distcp.getArg());
    }
}
