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

package org.apache.oozie.jobs_api;

import junit.framework.TestCase;
import org.apache.oozie.jobs_api.gen.*;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.*;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This class tests whether the workflow.xml files are parsed correctly into JAXB objects and whether the JAXB objects
 * are serialized correctly to xml.
 */
public class TestJAXBWorkflow extends TestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests whether a workflow.xml object is parsed correctly into a JAXB element tree by checking some of the main
     * properties.
     * @throws SAXException If a SAX error occurs during parsing the schema file.
     * @throws JAXBException If an error was encountered while creating the <tt>JAXBContext</tt> or the
     *         <tt>Unmarshaller</tt> objects.
     */
    public void testUnmarshallingWorkflow() throws SAXException, JAXBException {
        final Schema wf_schema = getSchema();

        final JAXBContext jc = JAXBContext.newInstance("org.apache.oozie.jobs_api.gen");
        final Unmarshaller u = jc.createUnmarshaller();
        u.setSchema(wf_schema);

        final URL wf_url = getClass().getResource("/workflow.xml");

        final JAXBElement element = (JAXBElement) u.unmarshal(wf_url);
        final WORKFLOWAPP wf = (WORKFLOWAPP) element.getValue();

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
     * Tests whether a programatically built JAXB element tree is serialized correctly to xml.
     *
     * @throws JAXBException If an error was encountered while creating the <tt>JAXBContext</tt>
     *         or during the marshalling.
     */
    public void testMarshallingWorkflow() throws JAXBException, URISyntaxException, IOException,
                                                 ParserConfigurationException, SAXException {
        // Marshalling the JAXB tree representing the workflow definition.
        final WORKFLOWAPP wfApp = getWfApp();
        final JAXBElement wfElement = new ObjectFactory().createWorkflowApp(wfApp);

        final JAXBContext jc = JAXBContext.newInstance("org.apache.oozie.jobs_api.gen");
        final Marshaller m =  jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.marshal(wfElement, out);

        final String outputXml = out.toString(StandardCharsets.UTF_8.toString());

        // Checking if the marshalled xml differs from the reference.
        final Diff diff = DiffBuilder.compare(Input.fromURL(getClass().getResource("/workflow.xml")))
                .withTest(Input.fromString(outputXml))
                .ignoreComments()
                .withDifferenceEvaluator(DifferenceEvaluators.chain(DifferenceEvaluators.Default,
                                                                    new DifferenceEvaluator() {
                    @Override
                    public ComparisonResult evaluate(Comparison comparison, ComparisonResult comparisonResult) {
                        // We want to ignore whitespace differences in TEXT_VALUE comparisons but not anywhere else,
                        // for example not in attribute names.
                        if (!comparisonResult.equals(ComparisonResult.EQUAL)
                                && comparison.getType().equals(ComparisonType.TEXT_VALUE)
                                && comparison.getControlDetails().getTarget().getNodeValue().trim().equals(
                                        comparison.getTestDetails().getTarget().getNodeValue().trim())) {
                                comparisonResult = ComparisonResult.EQUAL;
                        }

                        return comparisonResult;
                    }
                }))
                .build();

        assertFalse(diff.hasDifferences());
    }

    private Schema getSchema() throws SAXException {
        final URL schemaURL = getClass().getResource("/oozie-workflow-0.5.xsd");

        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(schemaURL);
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
