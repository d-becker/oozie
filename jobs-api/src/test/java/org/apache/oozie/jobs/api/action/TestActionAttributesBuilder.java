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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestActionAttributesBuilder {
    @Test
    public void testAll() {
        fail();
    }

    @Test
    public void testJobTrackerAdded() {
        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withJobTracker(JOB_TRACKER);

        final MapReduceAction mrAction = builder.build();
        assertEquals(JOB_TRACKER, mrAction.getJobTracker());
    }

    @Test
    public void testJobTrackerAddedTwiceThrows() {
        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withJobTracker(JOB_TRACKER);

        expectedException.expect(IllegalStateException.class);
        builder.withJobTracker("any_string");
    }

    @Test
    public void testNameNodeAdded() {
        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withNameNode(NAME_NODE);

        final MapReduceAction mrAction = builder.build();
        assertEquals(NAME_NODE, mrAction.getNameNode());
    }

    @Test
    public void testNameNodeAddedTwiceThrows() {
        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withNameNode(NAME_NODE);

        expectedException.expect(IllegalStateException.class);
        builder.withNameNode("any_string");
    }

    @Test
    public void testPrepareAdded() {
        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withPrepare(new PrepareBuilder().withDelete(EXAMPLE_DIR).build());

        final MapReduceAction mrAction = builder.build();
        assertEquals(EXAMPLE_DIR, mrAction.getPrepare().getDeletes().get(0).getPath());
    }

    @Test
    public void testPrepareAddedTwiceThrows() {
        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withPrepare(new PrepareBuilder().withDelete(EXAMPLE_DIR).build());

        expectedException.expect(IllegalStateException.class);
        builder.withPrepare(new PrepareBuilder().withDelete("any_directory").build());
    }

    @Test
    public void testStreamingAdded() {
        final Streaming streaming = new StreamingBuilder().withMapper("mapper.sh").withReducer("reducer.sh").build();

        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withStreaming(streaming);

        final MapReduceAction mrAction = builder.build();
        assertEquals(streaming, mrAction.getStreaming());
    }

    @Test
    public void testStreamingAddedTwiceThrows() {
        final Streaming streaming1= new StreamingBuilder().withMapper("mapper1.sh").withReducer("reducer1.sh").build();
        final Streaming streaming2 = new StreamingBuilder().withMapper("mapper2.sh").withReducer("reducer2.sh").build();

        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withStreaming(streaming1);

        expectedException.expect(IllegalStateException.class);
        builder.withStreaming(streaming2);
    }

    @Test
    public void testPipesAdded() {
        final Pipes pipes = new PipesBuilder().withMap("map").withReduce("reduce").build();

        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withPipes(pipes);

        final MapReduceAction mrAction = builder.build();
        assertEquals(pipes, mrAction.getPipes());
    }

    @Test
    public void testPipesAddedTwiceThrows() {
        final Pipes pipes1 = new PipesBuilder().withMap("map1").withReduce("reduce1").build();
        final Pipes pipes2 = new PipesBuilder().withMap("map2").withReduce("reduce2").build();

        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withPipes(pipes1);

        expectedException.expect(IllegalStateException.class);
        builder.withPipes(pipes2);
    }

    @Test
    public void testConfigClassAdded() {
        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withConfigClass(CONFIG_CLASS);

        final MapReduceAction mrAction = builder.build();
        assertEquals(CONFIG_CLASS, mrAction.getConfigClass());
    }

    @Test
    public void testConfigClassAddedTwiceThrows() {
        final MapReduceActionBuilder builder = getBuilderInstance();
        builder.withConfigClass(CONFIG_CLASS);

        expectedException.expect(IllegalStateException.class);
        builder.withConfigClass("AnyClass");
    }

    @Test
    public void testSeveralJobXmlsAdded() {
        final MapReduceActionBuilder builder = getBuilderInstance();

        for (final String jobXml : JOB_XMLS) {
            builder.withJobXml(jobXml);
        }

        final MapReduceAction mrAction = builder.build();

        final List<String> jobXmlsList = mrAction.getJobXmls();
        assertEquals(JOB_XMLS.length, jobXmlsList.size());

        for (int i = 0; i < JOB_XMLS.length; ++i) {
            assertEquals(JOB_XMLS[i], jobXmlsList.get(i));
        }
    }

    @Test
    public void testWithoutJobXmls() {
        final MapReduceActionBuilder builder = getBuilderInstance();

        for (final String jobXml : JOB_XMLS) {
            builder.withJobXml(jobXml);
        }

        builder.withoutJobXml(JOB_XMLS[0]);

        final MapReduceAction mrAction = builder.build();

        final List<String> jobXmlsList = mrAction.getJobXmls();
        final String[] remainingJobXmls = Arrays.copyOfRange(JOB_XMLS, 1, JOB_XMLS.length);
        assertEquals(remainingJobXmls.length, jobXmlsList.size());

        for (int i = 0; i < remainingJobXmls.length; ++i) {
            assertEquals(remainingJobXmls[i], jobXmlsList.get(i));
        }
    }

    @Test
    public void testClearJobXmls() {
        final MapReduceActionBuilder builder = getBuilderInstance();

        for (final String jobXml : JOB_XMLS) {
            builder.withJobXml(jobXml);
        }

        builder.clearJobXmls();

        final MapReduceAction mrAction = builder.build();

        final List<String> jobXmlsList = mrAction.getJobXmls();
        assertEquals(0, jobXmlsList.size());
    }
}
