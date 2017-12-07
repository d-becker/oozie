package org.apache.oozie.jobs.api.workflow;

import com.google.common.collect.ImmutableList;

public class Credentials {
    private final ImmutableList<Credential> credentials;

    public Credentials(final ImmutableList<Credential> credentials) {
        this.credentials = credentials;
    }

    public ImmutableList<Credential> getCredentials() {
        return credentials;
    }
}
