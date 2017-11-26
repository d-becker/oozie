package org.apache.oozie.jobs.api.mapping;

import org.apache.oozie.jobs.api.action.Delete;
import org.apache.oozie.jobs.api.action.Mkdir;
import org.apache.oozie.jobs.api.action.Prepare;
import org.apache.oozie.jobs.api.generated.action.shell.DELETE;
import org.apache.oozie.jobs.api.generated.action.shell.MKDIR;
import org.apache.oozie.jobs.api.generated.action.shell.ObjectFactory;
import org.apache.oozie.jobs.api.generated.action.shell.PREPARE;
import org.dozer.DozerConverter;

import java.util.ArrayList;
import java.util.List;

public class ShellPrepareConverter extends DozerConverter<Prepare, PREPARE> {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public ShellPrepareConverter() {
        super(Prepare.class, PREPARE.class);
    }

    @Override
    public PREPARE convertTo(final Prepare source, PREPARE destination) {
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
