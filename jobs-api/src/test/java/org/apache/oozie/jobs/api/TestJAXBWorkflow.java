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

import org.apache.oozie.jobs.api.generated.ACTION;
import org.apache.oozie.jobs.api.generated.ACTIONTRANSITION;
import org.apache.oozie.jobs.api.generated.CONFIGURATION;
import org.apache.oozie.jobs.api.generated.DELETE;
import org.apache.oozie.jobs.api.generated.END;
import org.apache.oozie.jobs.api.generated.KILL;
import org.apache.oozie.jobs.api.generated.MAPREDUCE;
import org.apache.oozie.jobs.api.generated.ObjectFactory;
import org.apache.oozie.jobs.api.generated.PREPARE;
import org.apache.oozie.jobs.api.generated.START;
import org.apache.oozie.jobs.api.generated.WORKFLOWAPP;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class tests whether the workflow.xml files are parsed correctly into JAXB objects and whether the JAXB objects
 * are serialized correctly to xml.
 */
public class TestJAXBWorkflow {
    private static final String GENERATED_PACKAGE = "org.apache.oozie.jobs.api.generated";
    private static final String EXAMPLE_WORKFLOW_RESOURCE_NAME = "/workflow.xml";

    /**
     * Tests whether a workflow.xml object is parsed correctly into a JAXB element tree by checking some of the main
     * properties.
     * @throws SAXException If a SAX error occurs during parsing the schema file.
     * @throws JAXBException If an error was encountered while creating the <tt>JAXBContext</tt> or the
     *         <tt>Unmarshaller</tt> objects.
     */
    @Test
    public void whenWorkflowXmlIsUnmarshaledAttributesArePreserved() throws SAXException, JAXBException {
        final WORKFLOWAPP wf = unmarshalExampleWorkflow();

        assertEquals("jaxb-example-wf", wf.getName());
        assertEquals("mr-node", wf.getStart().getTo());
        assertEquals("end", wf.getEnd().getName());

        final List<Object> actions = wf.getDecisionOrForkOrJoin();

        final KILL kill = (KILL) actions.get(1);
        assertEquals("fail", kill.getName());
        assertEquals("Map/Reduce failed, error message[${wf:errorMessage(wf:lastErrorNode())}]", kill.getMessage());

        final MAPREDUCE mr = ((ACTION) actions.get(0)).getMapReduce();

        final PREPARE prepare = mr.getPrepare();
        assertEquals(0, prepare.getMkdir().size());

        final List<DELETE> deleteList = prepare.getDelete();
        assertEquals(1, deleteList.size());

        final DELETE delete = deleteList.get(0);
        assertEquals("${nameNode}/user/${wf:user()}/${examplesRoot}/output-data/${outputDir}", delete.getPath());

        final CONFIGURATION conf = mr.getConfiguration();
        final List<CONFIGURATION.Property> properties = conf.getProperty();

        final CONFIGURATION.Property mapper = properties.get(1);
        assertEquals("mapred.mapper.class", mapper.getName());
        assertEquals("org.apache.oozie.example.SampleMapper", mapper.getValue());
    }


    /**
     * Tests whether a programmatically built JAXB element tree is serialized correctly to xml.
     *
     * @throws JAXBException If an error was encountered while creating the <tt>JAXBContext</tt>
     *         or during the marshalling.
     */
    @Test
    public void marshallingWorkflowProducesCorrectXml() throws JAXBException, URISyntaxException, IOException,
                                                 ParserConfigurationException, SAXException {
        WORKFLOWAPP programmaticallyCreatedWfApp = getWfApp();
        final String outputXml = marshalWorkflowApp(programmaticallyCreatedWfApp);

        final Diff diff = DiffBuilder.compare(Input.fromURL(getClass().getResource(EXAMPLE_WORKFLOW_RESOURCE_NAME)))
                .withTest(Input.fromString(outputXml))
                .ignoreComments()
                .withDifferenceEvaluator(DifferenceEvaluators.chain(
                        DifferenceEvaluators.Default,
                        new IgnoreWhitespaceInTextValueDifferenceEvaluator()))
                .build();

        assertFalse(diff.hasDifferences());
    }

    private static class IgnoreWhitespaceInTextValueDifferenceEvaluator implements DifferenceEvaluator {
        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult comparisonResult) {
            // We want to ignore whitespace differences in TEXT_VALUE comparisons but not anywhere else,
            // for example not in attribute names.
            if (isTextValueComparison(comparison) && expectedAndActualValueTrimmedAreEqual(comparison)) {
                return ComparisonResult.EQUAL;
            } else {
                return comparisonResult;
            }
        }

        private boolean isTextValueComparison(Comparison comparison) {
            return comparison.getType().equals(ComparisonType.TEXT_VALUE);
        }

        private boolean expectedAndActualValueTrimmedAreEqual(Comparison comparison) {
            String expectedNodeValue = comparison.getControlDetails().getTarget().getNodeValue();
            String actualNodeValue = comparison.getTestDetails().getTarget().getNodeValue();

            if (expectedNodeValue == null || actualNodeValue == null) {
                return false;
            }

            return expectedNodeValue.trim().equals(actualNodeValue.trim());
        }
    }

    private WORKFLOWAPP unmarshalExampleWorkflow() throws SAXException, JAXBException {
        final JAXBContext jc = JAXBContext.newInstance(GENERATED_PACKAGE);
        final Unmarshaller u = jc.createUnmarshaller();
        final Schema wfSchema = getSchema();
        u.setSchema(wfSchema);

        final URL wfUrl = getClass().getResource(EXAMPLE_WORKFLOW_RESOURCE_NAME);
        final JAXBElement element = (JAXBElement) u.unmarshal(wfUrl);

        return (WORKFLOWAPP) element.getValue();
    }

    private Schema getSchema() throws SAXException {
        final URL schemaURL = getClass().getResource("/oozie-workflow-0.5.xsd");

        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(schemaURL);
    }

    private String marshalWorkflowApp(WORKFLOWAPP wfApp) throws JAXBException, UnsupportedEncodingException {
        final JAXBElement wfElement = new ObjectFactory().createWorkflowApp(wfApp);

        final JAXBContext jc = JAXBContext.newInstance(GENERATED_PACKAGE);
        final Marshaller m =  jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.marshal(wfElement, out);

        return out.toString(StandardCharsets.UTF_8.toString());
    }
    private WORKFLOWAPP getWfApp() {
        final START start = new START();
        start.setTo("mr-node");

        final KILL kill = new KILL();
        kill.setName("fail");
        kill.setMessage("Map/Reduce failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");

        final END end = new END();
        end.setName("end");

        WORKFLOWAPP wfApp = new WORKFLOWAPP();
        wfApp.setName("jaxb-example-wf");
        wfApp.setStart(start);
        wfApp.getDecisionOrForkOrJoin().add(getAction());
        wfApp.getDecisionOrForkOrJoin().add(kill);
        wfApp.setEnd(end);

        return wfApp;
    }

    private ACTION getAction() {
        final ACTION action = new ACTION();

        action.setName("mr-node");
        action.setMapReduce(getMapreduce());

        final ACTIONTRANSITION okTransition = new ACTIONTRANSITION();
        okTransition.setTo("end");
        action.setOk(okTransition);

        final ACTIONTRANSITION errorTransition = new ACTIONTRANSITION();
        errorTransition.setTo("fail");
        action.setError(errorTransition);

        return action;
    }

    private MAPREDUCE getMapreduce() {
        final MAPREDUCE mr = new MAPREDUCE();

        mr.setJobTracker("${jobTracker}");
        mr.setNameNode("${nameNode}");
        mr.setPrepare(getPrepare());
        mr.setConfiguration(getConfiguration());

        return mr;
    }

    private CONFIGURATION getConfiguration() {
        final String[][] nameValuePairs = {
                {"mapred.job.queue.name", "${queueName}"},
                {"mapred.mapper.class", "org.apache.oozie.example.SampleMapper"},
                {"mapred.reducer.class", "org.apache.oozie.example.SampleReducer"},
                {"mapred.map.tasks", "1"},
                {"mapred.input.dir", "/user/${wf:user()}/${examplesRoot}/input-data/text"},
                {"mapred.output.dir", "/user/${wf:user()}/${examplesRoot}/output-data/${outputDir}"}
        };

        final CONFIGURATION config = new CONFIGURATION();
        final List<CONFIGURATION.Property> properties = config.getProperty();

        for (String[] pair : nameValuePairs) {
            final CONFIGURATION.Property property = new CONFIGURATION.Property();
            property.setName(pair[0]);
            property.setValue(pair[1]);

            properties.add(property);
        }

        return config;
    }

    private PREPARE getPrepare() {
        final PREPARE prepare = new PREPARE();

        final DELETE delete = new DELETE();
        delete.setPath("${nameNode}/user/${wf:user()}/${examplesRoot}/output-data/${outputDir}");

        prepare.getDelete().add(delete);

        return prepare;
    }
}
