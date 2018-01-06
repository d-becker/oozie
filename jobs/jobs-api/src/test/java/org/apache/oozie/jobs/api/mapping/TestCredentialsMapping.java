package org.apache.oozie.jobs.api.mapping;

import com.google.common.collect.Lists;
import org.apache.oozie.jobs.api.generated.workflow.CREDENTIALS;
import org.apache.oozie.jobs.api.workflow.ConfigurationEntry;
import org.apache.oozie.jobs.api.workflow.Credentials;
import org.apache.oozie.jobs.api.workflow.CredentialsBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCredentialsMapping {

    @Test
    public void testMappingCredentials() {
        final Credentials source = CredentialsBuilder.create()
                .withCredential("hbase", "hbase")
                .withCredential("hive2", "hive2",
                        Lists.newArrayList(new ConfigurationEntry("jdbcUrl", "jdbc://localhost/hive2")))
                .build();

        final CREDENTIALS destination = DozerMapperSingletonWrapper.instance().map(source, CREDENTIALS.class);

        assertEquals("hbase", destination.getCredential().get(0).getName());
        assertEquals("hbase", destination.getCredential().get(0).getType());
        assertEquals(0, destination.getCredential().get(0).getProperty().size());
        assertEquals("hive2", destination.getCredential().get(1).getName());
        assertEquals("hive2", destination.getCredential().get(1).getType());
        assertEquals("jdbcUrl", destination.getCredential().get(1).getProperty().get(0).getName());
        assertEquals("jdbc://localhost/hive2", destination.getCredential().get(1).getProperty().get(0).getValue());
    }
}
