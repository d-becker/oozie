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

package org.apache.oozie.jobs.api;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestMapReduceActionBuilder extends TestActionBuilderBaseImpl<MapReduceAction, MapReduceActionBuilder> {
    public static final String NAME = "map-reduce-name";
    public static final String JOB_TRACKER = "${jobTracker}";
    public static final String NAME_NODE = "${nameNode}";

    public static final String EXAMPLE_DIR = "/path/to/directory";
    public static final String CONFIG_CLASS = "AnyConfigClass.class";
    public static final String[] JOB_XMLS = {"jobXml1.xml", "jobXml2.xml", "jobXml3.xml", "jobXml4.xml"};
    public static final String[] FILES = {"file1.xml", "file2.xml", "file3.xml", "file4.xml"};
    public static final String[] ARCHIVES = {"archive1.jar", "archive2.jar", "archive3.jar", "archive4.jar"};

    @Override
    protected MapReduceActionBuilder getBuilderInstance() {
        return new MapReduceActionBuilder();
    }

    @Override
    protected MapReduceActionBuilder getBuilderInstance(MapReduceAction action) {
        return new MapReduceActionBuilder(action);
    }

    @Test
    public void testJobTrackerAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withJobTracker(JOB_TRACKER);

        MapReduceAction mrAction = builder.build();
        assertEquals(JOB_TRACKER, mrAction.getJobTracker());
    }

    @Test
    public void testJobTrackerAddedTwiceThrows() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withJobTracker(JOB_TRACKER);

        expectedException.expect(IllegalStateException.class);
        builder.withJobTracker("any_string");
    }

    @Test
    public void testNameNodeAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withNameNode(NAME_NODE);

        MapReduceAction mrAction = builder.build();
        assertEquals(NAME_NODE, mrAction.getNameNode());
    }

    @Test
    public void testNameNodeAddedTwiceThrows() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withNameNode(NAME_NODE);

        expectedException.expect(IllegalStateException.class);
        builder.withNameNode("any_string");
    }

    @Test
    public void testPrepareAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withPrepare(new PrepareBuilder().withDelete(EXAMPLE_DIR).build());

        MapReduceAction mrAction = builder.build();
        assertEquals(EXAMPLE_DIR, mrAction.getPrepare().getDeletes().get(0).getPath());
    }

    @Test
    public void testPrepareAddedTwiceThrows() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withPrepare(new PrepareBuilder().withDelete(EXAMPLE_DIR).build());

        expectedException.expect(IllegalStateException.class);
        builder.withPrepare(new PrepareBuilder().withDelete("any_directory").build());
    }

    @Test
    public void testConfigClassAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withConfigClass(CONFIG_CLASS);

        MapReduceAction mrAction = builder.build();
        assertEquals(CONFIG_CLASS, mrAction.getConfigClass());
    }

    @Test
    public void testConfigClassAddedTwiceThrows() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();
        builder.withConfigClass(CONFIG_CLASS);

        expectedException.expect(IllegalStateException.class);
        builder.withConfigClass("AnyClass");
    }

    @Test
    public void testSeveralJobXmlsAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String jobXml : JOB_XMLS) {
            builder.withJobXml(jobXml);
        }

        MapReduceAction mrAction = builder.build();

        List<String> jobXmlsList = mrAction.getJobXmls();
        assertEquals(JOB_XMLS.length, jobXmlsList.size());

        for (int i = 0; i < JOB_XMLS.length; ++i) {
            assertEquals(JOB_XMLS[i], jobXmlsList.get(i));
        }
    }

    @Test
    public void testWithoutJobXmls() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String jobXml : JOB_XMLS) {
            builder.withJobXml(jobXml);
        }

        builder.withoutJobXml(JOB_XMLS[0]);

        MapReduceAction mrAction = builder.build();

        List<String> jobXmlsList = mrAction.getJobXmls();
        String[] remainingJobXmls = Arrays.copyOfRange(JOB_XMLS, 1, JOB_XMLS.length);
        assertEquals(remainingJobXmls.length, jobXmlsList.size());

        for (int i = 0; i < remainingJobXmls.length; ++i) {
            assertEquals(remainingJobXmls[i], jobXmlsList.get(i));
        }
    }

    @Test
    public void testClearJobXmls() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String jobXml : JOB_XMLS) {
            builder.withJobXml(jobXml);
        }

        builder.clearJobXmls();

        MapReduceAction mrAction = builder.build();

        List<String> jobXmlsList = mrAction.getJobXmls();
        assertEquals(0, jobXmlsList.size());
    }

    @Test
    public void testSeveralFilesAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String file : FILES) {
            builder.withFile(file);
        }

        MapReduceAction mrAction = builder.build();

        List<String> filesList = mrAction.getFiles();
        assertEquals(FILES.length, filesList.size());

        for (int i = 0; i < FILES.length; ++i) {
            assertEquals(FILES[i], filesList.get(i));
        }
    }

    @Test
    public void testRemoveFiles() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String file : FILES) {
            builder.withFile(file);
        }

        builder.withoutFile(FILES[0]);

        MapReduceAction mrAction = builder.build();

        List<String> filesList = mrAction.getFiles();
        String[] remainingFiles = Arrays.copyOfRange(FILES, 1, FILES.length);
        assertEquals(remainingFiles.length, filesList.size());

        for (int i = 0; i < remainingFiles.length; ++i) {
            assertEquals(remainingFiles[i], filesList.get(i));
        }
    }

    @Test
    public void testClearFiles() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String file : FILES) {
            builder.withFile(file);
        }

        builder.clearFiles();

        MapReduceAction mrAction = builder.build();

        List<String> filesList = mrAction.getFiles();
        assertEquals(0, filesList.size());
    }

    @Test
    public void testSeveralArchivesAdded() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String archive : ARCHIVES) {
            builder.withArchive(archive);
        }

        MapReduceAction mrAction = builder.build();

        List<String> filesList = mrAction.getArchives();
        assertEquals(ARCHIVES.length, filesList.size());

        for (int i = 0; i < ARCHIVES.length; ++i) {
            assertEquals(ARCHIVES[i], filesList.get(i));
        }
    }

    @Test
    public void testRemoveArchives() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String archive : ARCHIVES) {
            builder.withArchive(archive);
        }

        builder.withoutArchive(ARCHIVES[0]);

        MapReduceAction mrAction = builder.build();

        List<String> archivesList = mrAction.getArchives();
        String[] remainingArchives = Arrays.copyOfRange(ARCHIVES, 1, ARCHIVES.length);
        assertEquals(remainingArchives.length, archivesList.size());

        for (int i = 0; i < remainingArchives.length; ++i) {
            assertEquals(remainingArchives[i], archivesList.get(i));
        }
    }

    @Test
    public void testClearArchives() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        for (String archive : ARCHIVES) {
            builder.withArchive(archive);
        }

        builder.clearArchives();

        MapReduceAction mrAction = builder.build();

        List<String> archivesList = mrAction.getArchives();
        assertEquals(0, archivesList.size());
    }

    @Test
    public void testFromExistingActionMapReduceSpecific() {
        MapReduceActionBuilder builder = new MapReduceActionBuilder();

        builder.withName(NAME)
                .withNameNode(NAME_NODE)
                .withFile(FILES[0])
                .withFile(FILES[1]);

        MapReduceAction mrAction = builder.build();

        MapReduceActionBuilder fromExistingBuilder = new MapReduceActionBuilder(mrAction);

        final String newName = "fromExisting_" + NAME;
        fromExistingBuilder.withName(newName)
                .withoutFile(FILES[1])
                .withFile(FILES[2]);

        MapReduceAction modifiedMrAction = fromExistingBuilder.build();

        assertEquals(newName, modifiedMrAction.getName());
        assertEquals(mrAction.getNameNode(), modifiedMrAction.getNameNode());
        assertEquals(Arrays.asList(FILES[0], FILES[2]), modifiedMrAction.getFiles());
    }

}
