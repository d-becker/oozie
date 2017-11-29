package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.generated.action.sqoop.CONFIGURATION;
import org.apache.oozie.jobs.api.generated.action.sqoop.ObjectFactory;
import org.dozer.DozerConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqoopConfigurationConverter extends DozerConverter<Map, CONFIGURATION> {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public SqoopConfigurationConverter() {
        super(Map.class, CONFIGURATION.class);
    }

    @Override
    public CONFIGURATION convertTo(final Map source, CONFIGURATION destination) {
        if (source == null) {
            return null;
        }

        destination = ensureDestination(destination);

        mapEntries(source, destination);

        return destination;
    }

    private CONFIGURATION ensureDestination(CONFIGURATION destination) {
        if (destination == null) {
            destination = OBJECT_FACTORY.createCONFIGURATION();
        }

        return destination;
    }

    private void mapEntries(final Map source, final CONFIGURATION destination) {
        if (source != null) {
            final List<CONFIGURATION.Property> targetProperties = new ArrayList<>();

            for (final Object objectKey : source.keySet()) {
                final String name = objectKey.toString();
                final String value = source.get(name).toString();
                final CONFIGURATION.Property targetProperty = OBJECT_FACTORY.createCONFIGURATIONProperty();
                targetProperty.setName(name);
                targetProperty.setValue(value);
                targetProperties.add(targetProperty);
            }

            destination.setProperty(targetProperties);
        }
    }

    @Override
    public Map convertFrom(final CONFIGURATION source, final Map destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }
}
