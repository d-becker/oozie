package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.generated.workflow.CONFIGURATION;
import org.apache.oozie.jobs.api.generated.workflow.GLOBAL;
import org.apache.oozie.jobs.api.generated.workflow.LAUNCHER;
import org.apache.oozie.jobs.api.generated.workflow.ObjectFactory;
import org.apache.oozie.jobs.api.workflow.Global;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

public class GlobalConverter extends DozerConverter<Global, GLOBAL> implements MapperAware {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private Mapper mapper;

    public GlobalConverter() {
        super(Global.class, GLOBAL.class);
    }

    @Override
    public GLOBAL convertTo(final Global source, GLOBAL destination) {
        if (source == null) {
            return null;
        }

        destination = ensureDestination(destination);

        mapFields(source, destination);

        return destination;
    }

    private GLOBAL ensureDestination(final GLOBAL destination) {
        if (destination == null) {
            return OBJECT_FACTORY.createGLOBAL();
        }

        return destination;
    }

    private void mapFields(final Global source, final GLOBAL destination) {
        destination.setJobTracker(source.getJobTracker());
        destination.setResourceManager(source.getResourceManager());
        destination.setNameNode(source.getNameNode());
        destination.getJobXml().addAll(source.getJobXmls());

        mapLauncher(source, destination);

        mapConfiguration(source, destination);
    }

    private void mapLauncher(final Global source, final GLOBAL destination) {
        if (source.getLauncher() != null) {
            destination.setLauncher(mapper.map(source.getLauncher(), LAUNCHER.class));
        }
    }

    private void mapConfiguration(final Global source, final GLOBAL destination) {
        if (source.getConfiguration() != null) {
            destination.setConfiguration(mapper.map(source.getConfiguration(), CONFIGURATION.class));
        }
    }

    @Override
    public Global convertFrom(final GLOBAL source, final Global destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }

    @Override
    public void setMapper(final Mapper mapper) {
        this.mapper = mapper;
    }
}
