package org.apache.oozie.jobs.api.workflow;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestParametersBuilder {
    private ParametersBuilder builder;

    @Before
    public void setUp() {
        this.builder = new ParametersBuilder();
    }

    @Test
    public void testWithoutDescription() {
        final Parameters parameters = builder
                .withParameter("name1", "value1")
                .withParameter("name2", "value2")
                .build();

        assertEquals("name1", parameters.getParameters().get(0).getName());
        assertEquals("value1", parameters.getParameters().get(0).getValue());
        assertNull(parameters.getParameters().get(0).getDescription());
        assertEquals("name2", parameters.getParameters().get(1).getName());
        assertEquals("value2", parameters.getParameters().get(1).getValue());
        assertNull(parameters.getParameters().get(1).getDescription());
    }

    @Test
    public void testWithDescription() {
        final Parameters parameters = builder
                .withParameter("name1", "value1", "description1")
                .withParameter("name2", "value2", "description2")
                .build();

        assertEquals("name1", parameters.getParameters().get(0).getName());
        assertEquals("value1", parameters.getParameters().get(0).getValue());
        assertEquals("description1", parameters.getParameters().get(0).getDescription());
        assertEquals("name2", parameters.getParameters().get(1).getName());
        assertEquals("value2", parameters.getParameters().get(1).getValue());
        assertEquals("description2", parameters.getParameters().get(1).getDescription());
    }
}