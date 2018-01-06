package org.apache.oozie.jobs.client.examples;

import junit.framework.TestCase;
import org.apache.oozie.jobs.api.action.LauncherBuilder;
import org.apache.oozie.jobs.api.serialization.WorkflowMarshaller;
import org.apache.oozie.jobs.api.workflow.GlobalBuilder;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.apache.oozie.util.XLog;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;

public class TestGlobalExample extends TestCase {
    private static final XLog log = XLog.getLog(TestGlobalExample.class);

    @Test
    public void testGlobal() throws JAXBException, UnsupportedEncodingException {
        final Workflow workflow = new WorkflowBuilder()
                .withName("workflow-with-global")
                .withGlobal(GlobalBuilder.create()
                        .withJobTracker("${jobTracker}")
                        .withNameNode("${nameNode}")
                        .withJobXml("job.xml")
                        .withConfigProperty("key1", "value1")
                        .withLauncher(new LauncherBuilder()
                                .withMemoryMb(1024L)
                                .withVCores(1L)
                                .build())
                        .build())
                .build();

        final String xml = WorkflowMarshaller.unmarshal(workflow);

        log.info("Workflow XML is:\n{0}", xml);
    }
}
