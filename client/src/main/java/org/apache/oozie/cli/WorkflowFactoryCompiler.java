package org.apache.oozie.cli;

import com.google.common.base.Preconditions;
import org.apache.oozie.jobs.api.factory.WorkflowFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

public class WorkflowFactoryCompiler {
    private final File targetFolder;
    private final Class<? extends WorkflowFactory> workflowFactoryClass;

    public WorkflowFactoryCompiler(final File targetFolder, final Class<? extends WorkflowFactory> workflowFactoryClass) {
        this.targetFolder = targetFolder;
        this.workflowFactoryClass = workflowFactoryClass;
    }

    public void compile() {
        Preconditions.checkState(WorkflowFactory.class.isAssignableFrom(workflowFactoryClass),
                String.format("%s should be a %s", workflowFactoryClass.getName(), WorkflowFactory.class.getName()));

        final String dummyJavaFileContents = null;
        final JavaFileObject jfo = new InMemoryJavaFileObject(workflowFactoryClass.getName(), dummyJavaFileContents);
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
                Locale.ENGLISH,
                null);
        // FIXME: targetFolder isn't used
        // https://stackoverflow.com/questions/2028193/specify-output-path-for-dynamic-compilation
        final Iterable options = Arrays.asList("-d", targetFolder.getAbsolutePath());

        final JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                null, options, null,
                Arrays.asList(jfo));

        Preconditions.checkState(task.call(), "dynamic workflow compilation error");
    }

    static class InMemoryJavaFileObject extends SimpleJavaFileObject {
        private String contents = null;

        InMemoryJavaFileObject(final String className, final String contents) {
            super(URI.create("string:///" + className.replace('.', '/')
                    + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }

        public CharSequence getCharContent(final boolean ignoreEncodingErrors)
                throws IOException {
            return contents;
        }
    }
}
