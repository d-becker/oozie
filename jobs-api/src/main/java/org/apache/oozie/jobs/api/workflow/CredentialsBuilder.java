package org.apache.oozie.jobs.api.workflow;

import com.google.common.collect.ImmutableList;
import org.apache.oozie.jobs.api.action.Builder;

import java.util.List;

public class CredentialsBuilder implements Builder<Credentials> {
    private final ImmutableList.Builder<Credential> credentials;

    public static CredentialsBuilder create() {
        return new CredentialsBuilder(new ImmutableList.Builder<Credential>());
    }

    public static CredentialsBuilder createFromExisting(final Credentials credentials) {
        return new CredentialsBuilder(new ImmutableList.Builder<Credential>().addAll(credentials.getCredentials()));
    }

    CredentialsBuilder(final ImmutableList.Builder<Credential> credentials) {
        this.credentials = credentials;
    }

    public CredentialsBuilder withCredential(final String name,
                                             final String value) {
        this.credentials.add(new Credential(name, value, ImmutableList.<ConfigurationEntry>of()));
        return this;
    }

    public CredentialsBuilder withCredential(final String name,
                                             final String type,
                                             final List<ConfigurationEntry> configurationEntries) {
        this.credentials.add(new Credential(name, type, ImmutableList.copyOf(configurationEntries)));
        return this;
    }

    @Override
    public Credentials build() {
        return new Credentials(credentials.build());
    }
}
