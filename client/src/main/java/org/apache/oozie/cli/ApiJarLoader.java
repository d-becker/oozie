package org.apache.oozie.cli;

import com.google.common.base.Preconditions;
import org.apache.oozie.jobs.api.factory.WorkflowFactory;
import org.apache.oozie.jobs.api.workflow.Workflow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

class ApiJarLoader {
    private final File apiJarFile;

    ApiJarLoader(final File apiJarFile) {
        Preconditions.checkArgument(apiJarFile.exists(), "API JAR should exist");
        Preconditions.checkArgument(apiJarFile.isFile(), "API JAR should be a file");
        Preconditions.checkArgument(apiJarFile.getName().endsWith(".jar"), "API JAR should be a JAR file");

        this.apiJarFile = apiJarFile;
    }

    Workflow loadAndGenerate() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        final JarFile apiJar = new JarFile(apiJarFile);
        final String mainClassName = apiJar.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);

        final URLClassLoader workflowFactoryClassLoader = URLClassLoader.newInstance(new URL[]{apiJarFile.toURI().toURL()});
        final Class mainClass = workflowFactoryClassLoader.loadClass(mainClassName);

        Preconditions.checkNotNull(mainClass, "API JAR file should have a main class");
        Preconditions.checkState(WorkflowFactory.class.isAssignableFrom(mainClass),
                "API JAR main class should be an " + WorkflowFactory.class.getName());
        final Method mainMethod = mainClass.getDeclaredMethod(WorkflowFactory.class.getDeclaredMethods()[0].getName());

        return (Workflow) mainMethod.invoke(mainClass.newInstance());
    }
}
