package org.apache.oozie.jobs.api.workflow;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCredentialsBuilder {

    @Test
    public void testCreate() {
        final Credentials credentials = CredentialsBuilder.create()
                .withCredential("hive2",
                        "hive",
                        Lists.newArrayList(
                                new ConfigurationEntry("jdbcUrl", "jdbc://localhost/hive")))
                .build();

        assertEquals("hive2", credentials.getCredentials().get(0).getName());
        assertEquals("hive", credentials.getCredentials().get(0).getType());
        assertEquals("jdbcUrl", credentials.getCredentials().get(0).getConfigurationEntries().get(0).getName());
        assertEquals("jdbc://localhost/hive", credentials.getCredentials().get(0).getConfigurationEntries().get(0).getValue());
    }

    @Test
    public void testCreateFromExisting() {
        final Credentials credentials = CredentialsBuilder.create()
                .withCredential("hive2",
                        "hive",
                        Lists.newArrayList(
                                new ConfigurationEntry("jdbcUrl", "jdbc://localhost/hive")))
                .build();

        final Credentials fromExisting = CredentialsBuilder.createFromExisting(credentials)
                .withCredential("hbase",
                        "hbase")
                .build();

        assertEquals("hive2", fromExisting.getCredentials().get(0).getName());
        assertEquals("hive", fromExisting.getCredentials().get(0).getType());
        assertEquals("jdbcUrl", fromExisting.getCredentials().get(0).getConfigurationEntries().get(0).getName());
        assertEquals("jdbc://localhost/hive", fromExisting.getCredentials().get(0).getConfigurationEntries().get(0).getValue());

        assertEquals("hbase", fromExisting.getCredentials().get(1).getName());
        assertEquals("hbase", fromExisting.getCredentials().get(1).getType());
        assertEquals(0, fromExisting.getCredentials().get(1).getConfigurationEntries().size());
    }
}