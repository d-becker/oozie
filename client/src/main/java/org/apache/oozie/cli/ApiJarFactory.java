package org.apache.oozie.cli;

import com.google.common.base.Preconditions;
import org.apache.oozie.jobs.api.factory.WorkflowFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ApiJarFactory {
    private final File classFolder;
    private final String apiJarName;
    private final Class<? extends WorkflowFactory> apiFactoryClass;

    public ApiJarFactory(final File classFolder,
                         final String apiJarName,
                         final Class<? extends WorkflowFactory> apiFactoryClass) {
        this.classFolder = classFolder;
        this.apiJarName = apiJarName;
        this.apiFactoryClass = apiFactoryClass;
    }

    public JarFile create() throws IOException {
        Preconditions.checkState(WorkflowFactory.class.isAssignableFrom(apiFactoryClass),
                String.format("%s should be a %s", apiFactoryClass.getName(), WorkflowFactory.class.getName()));

        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, apiFactoryClass.getName());

        try (final JarOutputStream target = new JarOutputStream(new FileOutputStream(apiJarName), manifest)) {
            addWorkflowJarEntry(classFolder, target);
        }

        return new JarFile(apiJarName);
    }

    private void addWorkflowJarEntry(final File source, final JarOutputStream target) throws IOException {
        BufferedInputStream in = null;
        try {
            if (source.isDirectory()) {
                String name = source.getPath().replace("\\", "/");
                if (!name.isEmpty()) {
                    if (!name.endsWith("/"))
                        name += "/";
                    final JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (final File nestedFile : source.listFiles())
                    addWorkflowJarEntry(nestedFile, target);
                return;
            }

            final JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            final byte[] buffer = new byte[1024];
            while (true) {
                final int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        } finally {
            if (in != null)
                in.close();
        }
    }
}
