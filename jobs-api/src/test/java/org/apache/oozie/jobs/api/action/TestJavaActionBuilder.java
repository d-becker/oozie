package org.apache.oozie.jobs.api.action;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestJavaActionBuilder extends TestNodeBuilderBaseImpl<JavaAction, JavaActionBuilder> {
    private static final String NAME = "hive2-name";
    private static final String JOB_TRACKER = "${jobTracker}";
    private static final String NAME_NODE = "${nameNode}";
    private static final String EXAMPLE_DIR = "/path/to/directory";
    private static final String[] ARGS = {"arg1", "arg2", "arg3"};
    private static final String MAPRED_JOB_QUEUE_NAME = "mapred.job.queue.name";
    private static final String DEFAULT = "default";
    private static final String RESOURCE_MANAGER = "${resourceManager}";
    private static final String PATH_TO_DELETE = "/path/to/delete";
    private static final String PATH_TO_MKDIR = "/path/to/mkdir";

    @Override
    protected JavaActionBuilder getBuilderInstance() {
        return JavaActionBuilder.create();
    }

    @Override
    protected JavaActionBuilder getBuilderInstance(final JavaAction action) {
        return JavaActionBuilder.createFromExistingAction(action);
    }

    @Test
    public void testJobTrackerAdded() {
        final JavaActionBuilder builder = getBuilderInstance();
        builder.withJobTracker(JOB_TRACKER);

        final JavaAction action = builder.build();
        assertEquals(JOB_TRACKER, action.getJobTracker());
    }

    @Test
    public void testResourceManagerAdded() {
        final JavaActionBuilder builder = getBuilderInstance();
        builder.withResourceManager(JOB_TRACKER);

        final JavaAction action = builder.build();
        assertEquals(JOB_TRACKER, action.getResourceManager());
    }

    @Test
    public void testNameNodeAdded() {
        final JavaActionBuilder builder = getBuilderInstance();
        builder.withNameNode(NAME_NODE);

        final JavaAction action = builder.build();
        assertEquals(NAME_NODE, action.getNameNode());
    }

    @Test
    public void testPrepareAdded() {
        final JavaActionBuilder builder = getBuilderInstance();
        builder.withPrepare(new PrepareBuilder().withDelete(EXAMPLE_DIR).build());

        final JavaAction action = builder.build();
        assertEquals(EXAMPLE_DIR, action.getPrepare().getDeletes().get(0).getPath());
    }

    @Test
    public void testSameConfigPropertyAddedTwiceThrows() {
        final JavaActionBuilder builder = getBuilderInstance();
        builder.withConfigProperty(MAPRED_JOB_QUEUE_NAME, DEFAULT);

        expectedException.expect(IllegalStateException.class);
        builder.withConfigProperty(MAPRED_JOB_QUEUE_NAME, DEFAULT);
    }

    @Test
    public void testSeveralArgsAdded() {
        final JavaActionBuilder builder = getBuilderInstance();

        for (final String arg : ARGS) {
            builder.withArg(arg);
        }

        final JavaAction action = builder.build();

        final List<String> argList = action.getArgs();
        assertEquals(ARGS.length, argList.size());

        for (int i = 0; i < ARGS.length; ++i) {
            assertEquals(ARGS[i], argList.get(i));
        }
    }

    @Test
    public void testRemoveArgs() {
        final JavaActionBuilder builder = getBuilderInstance();

        for (final String file : ARGS) {
            builder.withArg(file);
        }

        builder.withoutArg(ARGS[0]);

        final JavaAction action = builder.build();

        final List<String> argList = action.getArgs();
        final String[] remainingArgs = Arrays.copyOfRange(ARGS, 1, ARGS.length);
        assertEquals(remainingArgs.length, argList.size());

        for (int i = 0; i < remainingArgs.length; ++i) {
            assertEquals(remainingArgs[i], argList.get(i));
        }
    }

    @Test
    public void testClearArgs() {
        final JavaActionBuilder builder = getBuilderInstance();

        for (final String file : ARGS) {
            builder.withArg(file);
        }

        builder.clearArgs();

        final JavaAction action = builder.build();

        final List<String> argList = action.getArgs();
        assertEquals(0, argList.size());
    }

    @Test
    public void testFromExistingJavaAction() {
        final JavaActionBuilder builder = getBuilderInstance();

        builder.withName(NAME)
                .withResourceManager(RESOURCE_MANAGER)
                .withNameNode(NAME_NODE)
                .withConfigProperty(MAPRED_JOB_QUEUE_NAME, DEFAULT)
                .withPrepare(new PrepareBuilder()
                        .withDelete(PATH_TO_DELETE)
                        .withMkdir(PATH_TO_MKDIR)
                        .build())
                .withLauncher(new LauncherBuilder()
                        .withMemoryMb(1024L)
                        .withVCores(2L)
                        .withQueue(DEFAULT)
                        .withSharelib(DEFAULT)
                        .withViewAcl(DEFAULT)
                        .withModifyAcl(DEFAULT)
                        .build())
                .withMainClass(DEFAULT)
                .withJavaOptsString(DEFAULT)
                .withJavaOpt(DEFAULT)
                .withArg(ARGS[0])
                .withArg(ARGS[1])
                .withArchive(DEFAULT)
                .withFile(DEFAULT)
                .withCaptureOutput(true);

        final JavaAction action = builder.build();

        final JavaActionBuilder fromExistingBuilder = getBuilderInstance(action);

        final String newName = "fromExisting_" + NAME;
        fromExistingBuilder.withName(newName)
                .withoutArg(ARGS[1])
                .withArg(ARGS[2]);

        final JavaAction modifiedAction = fromExistingBuilder.build();

        assertEquals(newName, modifiedAction.getName());
        assertEquals(action.getNameNode(), modifiedAction.getNameNode());

        final Map<String, String> expectedConfiguration = new LinkedHashMap<>();
        expectedConfiguration.put(MAPRED_JOB_QUEUE_NAME, DEFAULT);
        assertEquals(expectedConfiguration, modifiedAction.getConfiguration());

        assertEquals(Arrays.asList(ARGS[0], ARGS[2]), modifiedAction.getArgs());

        assertEquals(PATH_TO_DELETE, modifiedAction.getPrepare().getDeletes().get(0).getPath());
        assertEquals(PATH_TO_MKDIR, modifiedAction.getPrepare().getMkdirs().get(0).getPath());

        assertEquals(1024L, modifiedAction.getLauncher().getMemoryMb());
        assertEquals(2L, modifiedAction.getLauncher().getVCores());
        assertEquals(DEFAULT, modifiedAction.getLauncher().getQueue());
        assertEquals(DEFAULT, modifiedAction.getLauncher().getSharelib());
        assertEquals(DEFAULT, modifiedAction.getLauncher().getViewAcl());
        assertEquals(DEFAULT, modifiedAction.getLauncher().getModifyAcl());

        assertEquals(action.getMainClass(), modifiedAction.getMainClass());
        assertEquals(action.getJavaOptsString(), modifiedAction.getJavaOptsString());
        assertEquals(action.getJavaOpts().get(0), modifiedAction.getJavaOpts().get(0));
        assertEquals(action.isCaptureOutput(), modifiedAction.isCaptureOutput());
    }
}