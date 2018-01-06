package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.generated.workflow.PARAMETERS;
import org.apache.oozie.jobs.api.workflow.Parameters;
import org.apache.oozie.jobs.api.workflow.ParametersBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestParametersMapping {

    @Test
    public void testMappingParameters() {
        final Parameters source = ParametersBuilder.create()
                .withParameter("name1", "value1")
                .withParameter("name2", "value2", "description2")
                .build();

        final PARAMETERS destination = DozerMapperSingletonWrapper.instance().map(source, PARAMETERS.class);

        assertEquals("name1", destination.getProperty().get(0).getName());
        assertEquals("value1", destination.getProperty().get(0).getValue());
        assertNull(destination.getProperty().get(0).getDescription());
        assertEquals("name2", destination.getProperty().get(1).getName());
        assertEquals("value2", destination.getProperty().get(1).getValue());
        assertEquals("description2", destination.getProperty().get(1).getDescription());
    }
}
