package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.generated.workflow.CREDENTIAL;
import org.apache.oozie.jobs.api.generated.workflow.CREDENTIALS;
import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.workflow.ConfigurationEntry;
import org.apache.oozie.jobs.api.workflow.Credential;
import org.apache.oozie.jobs.api.workflow.Credentials;
import org.dozer.DozerConverter;

public class CredentialsConverter extends DozerConverter<Credentials, CREDENTIALS> {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public CredentialsConverter() {
        super(Credentials.class, CREDENTIALS.class);
    }

    @Override
    public CREDENTIALS convertTo(final Credentials source, CREDENTIALS destination) {
        if (source == null) {
            return null;
        }

        destination = ensureDestination(destination);

        mapCredentials(source, destination);

        return destination;
    }

    private CREDENTIALS ensureDestination(final CREDENTIALS destination) {
        if (destination == null) {
            return OBJECT_FACTORY.createCREDENTIALS();
        }

        return destination;
    }

    private void mapCredentials(final Credentials source, final CREDENTIALS destination) {
        if (source.getCredentials() == null) {
            return;
        }

        for (final Credential credential : source.getCredentials()) {
            final CREDENTIAL mappedCredential = OBJECT_FACTORY.createCREDENTIAL();
            mappedCredential.setName(credential.getName());
            mappedCredential.setType(credential.getType());
            mapConfigurationEntries(credential, mappedCredential);

            destination.getCredential().add(mappedCredential);
        }
    }

    private void mapConfigurationEntries(final Credential source, final CREDENTIAL destination) {
        if (source.getConfigurationEntries() == null) {
            return;
        }

        for (final ConfigurationEntry configurationEntry : source.getConfigurationEntries()) {
            final CREDENTIAL.Property mappedProperty = OBJECT_FACTORY.createCREDENTIALProperty();
            mappedProperty.setName(configurationEntry.getName());
            mappedProperty.setValue(configurationEntry.getValue());
            mappedProperty.setDescription(configurationEntry.getDescription());

            destination.getProperty().add(mappedProperty);
        }
    }

    @Override
    public Credentials convertFrom(final CREDENTIALS source, final Credentials destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }
}
