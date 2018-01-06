package org.apache.oozie.jobs.client.examples;

import com.google.common.collect.Lists;
import junit.framework.TestCase;
import org.apache.oozie.jobs.api.serialization.WorkflowMarshaller;
import org.apache.oozie.jobs.api.workflow.ConfigurationEntry;
import org.apache.oozie.jobs.api.workflow.CredentialsBuilder;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.apache.oozie.util.XLog;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;

public class TestCredentialsExample extends TestCase {
    private static final XLog log = XLog.getLog(TestCredentialsExample.class);

    public void testMappingCredentials() throws JAXBException, UnsupportedEncodingException {
        final Workflow workflow = new WorkflowBuilder()
                .withName("workflow-with-credentials")
                .withCredentials(CredentialsBuilder.create()
                        .withCredential("hbase", "hbase")
                        .withCredential("hive2", "hive2",
                                Lists.newArrayList(new ConfigurationEntry("jdbcUrl", "jdbc://localhost/hive2")))
                        .build())
                .build();

        final String xml = WorkflowMarshaller.unmarshal(workflow);

        log.info("Workflow XML is:\n{0}", xml);
    }
}
