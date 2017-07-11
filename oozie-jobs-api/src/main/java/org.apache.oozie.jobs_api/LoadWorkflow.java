package org.apache.oozie.jobs_api;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.oozie.jobs_api.gen.*;
import org.apache.oozie.service.SchemaService;
import org.apache.oozie.service.ServiceException;
import org.apache.oozie.service.Services;
import org.xml.sax.SAXException;

public class LoadWorkflow {
    public static void main(String[] args) throws JAXBException, SAXException, IOException, ServiceException {
        File schemaFile = new File("./client/src/main/resources/oozie-workflow-0.5.xsd");
        String appFileName = "./examples/src/main/apps/map-reduce/workflow.xml";

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema wf_schema = sf.newSchema(schemaFile);

        JAXBContext jc = JAXBContext.newInstance( "org.apache.oozie.jobs_api.gen" );
        Unmarshaller u = jc.createUnmarshaller();
        u.setSchema(wf_schema);

        String appXml = new String(Files.readAllBytes(Paths.get(appFileName)), StandardCharsets.UTF_8);


        JAXBElement o = (JAXBElement) u.unmarshal(new StringReader(appXml));
        WORKFLOWAPP wf = (WORKFLOWAPP) o.getValue();
        wf.setName("Customised-name");
        System.out.println(wf.getName());

        Marshaller m =  jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(o, System.out);
    }
}
