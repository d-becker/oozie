package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.action.LauncherBuilder;
import org.apache.oozie.jobs.api.generated.workflow.GLOBAL;
import org.apache.oozie.jobs.api.workflow.Global;
import org.apache.oozie.jobs.api.workflow.GlobalBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGlobalMapping {
    private static final String DEFAULT = "default";

    @Test
    public void testMappingGlobal() {
        final Global source = GlobalBuilder.create()
                .withJobTracker(DEFAULT)
                .withNameNode(DEFAULT)
                .withJobXml(DEFAULT)
                .withConfigProperty("key1", "value1")
                .withLauncher(new LauncherBuilder()
                        .withMemoryMb(1024L)
                        .withVCores(1L)
                        .build())
                .build();

        final GLOBAL destination = DozerMapperSingletonWrapper.instance().map(source, GLOBAL.class);

        assertEquals(DEFAULT, destination.getJobTracker());
        assertEquals(DEFAULT, destination.getNameNode());
        assertEquals(DEFAULT, destination.getJobXml().get(0));
        assertEquals("key1", destination.getConfiguration().getProperty().get(0).getName());
        assertEquals("value1", destination.getConfiguration().getProperty().get(0).getValue());
        assertEquals(1024L, destination.getLauncher().getMemoryMbOrVcoresOrJavaOpts().get(0).getValue());
        assertEquals(1L, destination.getLauncher().getMemoryMbOrVcoresOrJavaOpts().get(1).getValue());
    }
}
