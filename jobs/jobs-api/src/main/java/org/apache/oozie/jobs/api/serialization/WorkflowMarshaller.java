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

package org.apache.oozie.jobs.api.serialization;

import org.apache.oozie.jobs.api.action.EmailActionBuilder;
import org.apache.oozie.jobs.api.action.ErrorHandler;
import org.apache.oozie.jobs.api.action.MapReduceAction;
import org.apache.oozie.jobs.api.action.MapReduceActionBuilder;
import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.generated.workflow.WORKFLOWAPP;
import org.apache.oozie.jobs.api.mapping.DozerMapperSingletonWrapper;
import org.apache.oozie.jobs.api.oozie.dag.Graph;
import org.apache.oozie.jobs.api.workflow.Workflow;
import org.apache.oozie.jobs.api.workflow.WorkflowBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class WorkflowMarshaller {
    private static final String GENERATED_PACKAGES_ALL = "org.apache.oozie.jobs.api.generated.workflow:" +
            "org.apache.oozie.jobs.api.generated.action.distcp:" +
            "org.apache.oozie.jobs.api.generated.action.email:" +
            "org.apache.oozie.jobs.api.generated.action.hive2:" +
            "org.apache.oozie.jobs.api.generated.action.hive:" +
            "org.apache.oozie.jobs.api.generated.sla:" +
            "org.apache.oozie.jobs.api.generated.action.shell:" +
            "org.apache.oozie.jobs.api.generated.action.spark:" +
            "org.apache.oozie.jobs.api.generated.action.sqoop:" +
            "org.apache.oozie.jobs.api.generated.action.ssh";


    public static String unmarshal(final Workflow workflow) throws JAXBException, UnsupportedEncodingException {
        final Graph graph = new Graph(workflow);
        final WORKFLOWAPP workflowapp = DozerMapperSingletonWrapper.instance().map(graph, WORKFLOWAPP.class);
        final String xml = marshal(workflowapp);
        return xml;
    }

    private static String marshal(final WORKFLOWAPP workflowapp) throws JAXBException, UnsupportedEncodingException {
        final JAXBElement wfElement = new ObjectFactory().createWorkflowApp(workflowapp);

        final JAXBContext jc = JAXBContext.newInstance(GENERATED_PACKAGES_ALL);
        final Marshaller m =  jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.marshal(wfElement, out);

        return out.toString(StandardCharsets.UTF_8.toString());
    }

    public static void main(String[] args) throws JAXBException, UnsupportedEncodingException {
        final MapReduceAction mr1 = MapReduceActionBuilder.create().withName("mr1").withNameNode("${nameNode}").build();
        final MapReduceAction mr2 = MapReduceActionBuilder.create().withName("mr2").withParent(mr1).build();

        final EmailActionBuilder email = EmailActionBuilder.create()
                .withName("e-mail")
                .withRecipient("somebody@company.com")
                .withSubject("Subject")
                .withBody("Email body.");
        final ErrorHandler errorHandler = ErrorHandler.buildAsErrorHandler(email);
        final MapReduceAction mr3 = MapReduceActionBuilder.create().withName("mr3")
                .withParent(mr1)
                .withErrorHandler(errorHandler)
                .build();

        Workflow workflow = new WorkflowBuilder()
                .withName("Workflow_to_map")
                .withDagContainingNode(mr1)
                .build();

        System.out.println(unmarshal(workflow));
    }
}
