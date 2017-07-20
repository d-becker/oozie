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

import static org.junit.Assert.assertEquals;

public class TestPrepareBuilder {
    public static final String[] TEST_DIRS = {"/user/testpath/testdir1", "/user/testpath/testdir2", "/user/testpath/testdir3"};

    @Test
    public void testOneDeleteIsAddedWithSkipTrashTrue() {
        PrepareBuilder pb = new PrepareBuilder();
        pb.withDelete(TEST_DIRS[0], true);

        Prepare prepare = pb.build();

        assertEquals(1, prepare.getDeletes().size());

        Delete delete = prepare.getDeletes().get(0);
        assertEquals(TEST_DIRS[0], delete.getPath());
        assertEquals(true, delete.getSkipTrash());

        assertEquals(0, prepare.getMkdirs().size());
    }

    @Test
    public void testSeveralDeletesAreAddedWithSkipTrashNotSpecified() {
        PrepareBuilder pb = new PrepareBuilder();

        for (String testDir : TEST_DIRS) {
            pb.withDelete(testDir);
        }

        Prepare prepare = pb.build();

        assertEquals(TEST_DIRS.length, prepare.getDeletes().size());

        for (int i = 0; i < TEST_DIRS.length; ++i) {
            Delete delete = prepare.getDeletes().get(i);
            assertEquals(TEST_DIRS[i], delete.getPath());
            assertEquals(null, delete.getSkipTrash());
        }

        assertEquals(0, prepare.getMkdirs().size());
    }

    @Test
    public void testOneMkdirIsAdded() {
        PrepareBuilder pb = new PrepareBuilder();
        pb.withMkdir(TEST_DIRS[0]);

        Prepare prepare = pb.build();

        assertEquals(1, prepare.getMkdirs().size());

        Mkdir mkdir = prepare.getMkdirs().get(0);
        assertEquals(TEST_DIRS[0], mkdir.getPath());

        assertEquals(0, prepare.getDeletes().size());
    }

    @Test
    public void testSeveralMkdirsAreAdded() {
        PrepareBuilder pb = new PrepareBuilder();

        for (String testDir : TEST_DIRS) {
            pb.withMkdir(testDir);
        }

        Prepare prepare = pb.build();

        assertEquals(TEST_DIRS.length, prepare.getMkdirs().size());

        for (int i = 0; i < TEST_DIRS.length; ++i) {
            Mkdir mkdir = prepare.getMkdirs().get(i);
            assertEquals(TEST_DIRS[i], mkdir.getPath());
        }

        assertEquals(0, prepare.getDeletes().size());
    }
}
