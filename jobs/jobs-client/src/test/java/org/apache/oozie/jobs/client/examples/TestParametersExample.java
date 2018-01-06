package org.apache.oozie.jobs.client.examples;

import junit.framework.TestCase;
import org.apache.oozie.jobs.api.serialization.WorkflowMarshaller;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;
import org.apache.oozie.util.XLog;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;

public class TestParametersExample extends TestCase {
    private static final XLog log = XLog.getLog(TestParametersExample.class);

    @Test
    public void testParameters() throws JAXBException, UnsupportedEncodingException {
        final Workflow workflow = new WorkflowBuilder()
                .withName("workflow-with-parameters")
                .withParameter("name1", "value1")
                .withParameter("name2", "value2", "description2")
                .build();


        final String xml = WorkflowMarshaller.unmarshal(workflow);

        log.info("Workflow XML is:\n{0}", xml);
    }
}
