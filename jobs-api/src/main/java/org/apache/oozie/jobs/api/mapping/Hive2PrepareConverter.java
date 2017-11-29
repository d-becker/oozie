package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.action.Delete;
import org.apache.oozie.jobs.api.action.Mkdir;
import org.apache.oozie.jobs.api.action.Prepare;
import org.apache.oozie.jobs.api.generated.action.hive2.DELETE;
import org.apache.oozie.jobs.api.generated.action.hive2.MKDIR;
import org.apache.oozie.jobs.api.generated.action.hive2.ObjectFactory;
import org.apache.oozie.jobs.api.generated.action.hive2.PREPARE;
import org.dozer.DozerConverter;

import java.util.ArrayList;
import java.util.List;

public class Hive2PrepareConverter extends DozerConverter<Prepare, PREPARE> {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public Hive2PrepareConverter() {
        super(Prepare.class, PREPARE.class);
    }

    @Override
    public PREPARE convertTo(final Prepare source, PREPARE destination) {
        if (source == null) {
            return null;
        }

        destination = ensureDestination(destination);

        mapDeletes(source, destination);

        mapMkdirs(source, destination);

        return destination;
    }

    private PREPARE ensureDestination(final PREPARE destination) {
        if (destination == null) {
            return OBJECT_FACTORY.createPREPARE();
        }
        return destination;
    }

    private void mapDeletes(final Prepare source, final PREPARE destination) {
        if (source.getDeletes() != null) {
            final List<DELETE> targetDeletes = new ArrayList<>();

            for (final Delete sourceDelete : source.getDeletes()) {
                final DELETE targetDelete = OBJECT_FACTORY.createDELETE();
                targetDelete.setPath(sourceDelete.getPath());
                targetDeletes.add(targetDelete);
            }

            destination.setDelete(targetDeletes);
        }
    }

    private void mapMkdirs(final Prepare source, final PREPARE destination) {
        if (source.getMkdirs() != null) {
            final List<MKDIR> targetMkdirs = new ArrayList<>();

            for (final Mkdir sourceMkDir: source.getMkdirs()) {
                final MKDIR targetMkDir = OBJECT_FACTORY.createMKDIR();
                targetMkDir.setPath(sourceMkDir.getPath());
                targetMkdirs.add(targetMkDir);
            }

            destination.setMkdir(targetMkdirs);
        }
    }

    @Override
    public Prepare convertFrom(final PREPARE source, final Prepare destination) {
        throw new UnsupportedOperationException("This mapping is not bidirectional.");
    }
}
