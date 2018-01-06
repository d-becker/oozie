package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.generated.workflow.PARAMETERS;
import org.apache.oozie.jobs.api.workflow.Parameter;
import org.apache.oozie.jobs.api.workflow.Parameters;
import org.dozer.DozerConverter;

public class ParametersConverter extends DozerConverter<Parameters, PARAMETERS> {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public ParametersConverter() {
        super(Parameters.class, PARAMETERS.class);
    }

    @Override
    public PARAMETERS convertTo(final Parameters source, PARAMETERS destination) {
        if (source == null) {
            return null;
        }

        destination = ensureDestination(destination);

        mapParameters(source, destination);

        return destination;
    }

    private PARAMETERS ensureDestination(final PARAMETERS destination) {
        if (destination == null) {
            return OBJECT_FACTORY.createPARAMETERS();
        }

        return destination;
    }

    private void mapParameters(final Parameters source, final PARAMETERS destination) {
        for (final Parameter parameter : source.getParameters()) {
            final PARAMETERS.Property property = OBJECT_FACTORY.createPARAMETERSProperty();
            property.setName(parameter.getName());
            property.setValue(parameter.getValue());
            property.setDescription(parameter.getDescription());

            destination.getProperty().add(property);
        }
    }

    @Override
    public Parameters convertFrom(final PARAMETERS source, final Parameters destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }
}
