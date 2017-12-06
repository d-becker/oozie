package org.apache.oozie.jobs.api.workflow;

import org.apache.oozie.jobs.api.action.LauncherBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestGlobalBuilder {

    public static final String DEFAULT = "default";

    @Test
    public void testAfterCopyFieldsAreSetCorrectly() {
        final Global original = GlobalBuilder.create()
                .withJobTracker(DEFAULT)
                .withNameNode(DEFAULT)
                .withJobXml(DEFAULT)
                .withConfigProperty("key1", "value1")
                .withLauncher(new LauncherBuilder()
                        .withMemoryMb(1024L)
                        .withVCores(1L)
                        .build())
                .build();

        assertEquals(DEFAULT, original.getJobTracker());
        assertEquals(DEFAULT, original.getNameNode());
        assertEquals(DEFAULT, original.getJobXmls().get(0));
        assertEquals("value1", original.getConfigProperty("key1"));
        assertEquals(1024L, original.getLauncher().getMemoryMb());
        assertEquals(1L, original.getLauncher().getVCores());

        final Global copied = GlobalBuilder.createFromExisting(original)
                .withoutJobXml(DEFAULT)
                .withConfigProperty("key1", null)
                .build();

        assertEquals(DEFAULT, copied.getJobTracker());
        assertEquals(DEFAULT, copied.getNameNode());
        assertEquals(0, copied.getJobXmls().size());
        assertNull(copied.getConfigProperty("key1"));
        assertEquals(1024L, copied.getLauncher().getMemoryMb());
        assertEquals(1L, copied.getLauncher().getVCores());
    }
}