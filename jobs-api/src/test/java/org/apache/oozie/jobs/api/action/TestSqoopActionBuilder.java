package org.apache.oozie.jobs.api.action;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestSqoopActionBuilder extends TestNodeBuilderBaseImpl<SqoopAction, SqoopActionBuilder> {
    private static final String NAME = "sqoop-name";
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
    protected SqoopActionBuilder getBuilderInstance() {
        return SqoopActionBuilder.create();
    }

    @Override
    protected SqoopActionBuilder getBuilderInstance(final SqoopAction action) {
        return SqoopActionBuilder.createFromExistingAction(action);
    }

    @Test
    public void testJobTrackerAdded() {
        final SqoopActionBuilder builder = getBuilderInstance();
        builder.withJobTracker(JOB_TRACKER);

        final SqoopAction action = builder.build();
        assertEquals(JOB_TRACKER, action.getJobTracker());
    }

    @Test
    public void testResourceManagerAdded() {
        final SqoopActionBuilder builder = getBuilderInstance();
        builder.withResourceManager(JOB_TRACKER);

        final SqoopAction action = builder.build();
        assertEquals(JOB_TRACKER, action.getResourceManager());
    }

    @Test
    public void testNameNodeAdded() {
        final SqoopActionBuilder builder = getBuilderInstance();
        builder.withNameNode(NAME_NODE);

        final SqoopAction action = builder.build();
        assertEquals(NAME_NODE, action.getNameNode());
    }

    @Test
    public void testPrepareAdded() {
        final SqoopActionBuilder builder = getBuilderInstance();
        builder.withPrepare(new PrepareBuilder().withDelete(EXAMPLE_DIR).build());

        final SqoopAction action = builder.build();
        assertEquals(EXAMPLE_DIR, action.getPrepare().getDeletes().get(0).getPath());
    }

    @Test
    public void testSameConfigPropertyAddedTwiceThrows() {
        final SqoopActionBuilder builder = getBuilderInstance();
        builder.withConfigProperty(MAPRED_JOB_QUEUE_NAME, DEFAULT);

        expectedException.expect(IllegalStateException.class);
        builder.withConfigProperty(MAPRED_JOB_QUEUE_NAME, DEFAULT);
    }

    @Test
    public void testSeveralArgumentsAdded() {
        final SqoopActionBuilder builder = getBuilderInstance();

        for (final String arg : ARGS) {
            builder.withArgument(arg);
        }

        final SqoopAction action = builder.build();

        final List<String> argList = action.getArguments();
        assertEquals(ARGS.length, argList.size());

        for (int i = 0; i < ARGS.length; ++i) {
            assertEquals(ARGS[i], argList.get(i));
        }
    }

    @Test
    public void testRemoveArguments() {
        final SqoopActionBuilder builder = getBuilderInstance();

        for (final String file : ARGS) {
            builder.withArgument(file);
        }

        builder.withoutArgument(ARGS[0]);

        final SqoopAction action = builder.build();

        final List<String> argList = action.getArguments();
        final String[] remainingArgs = Arrays.copyOfRange(ARGS, 1, ARGS.length);
        assertEquals(remainingArgs.length, argList.size());

        for (int i = 0; i < remainingArgs.length; ++i) {
            assertEquals(remainingArgs[i], argList.get(i));
        }
    }

    @Test
    public void testClearArguments() {
        final SqoopActionBuilder builder = getBuilderInstance();

        for (final String file : ARGS) {
            builder.withArgument(file);
        }

        builder.clearArguments();

        final SqoopAction action = builder.build();

        final List<String> argList = action.getArguments();
        assertEquals(0, argList.size());
    }

    @Test
    public void testFromExistingSqoopAction() {
        final SqoopActionBuilder builder = getBuilderInstance();

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
                .withCommand(DEFAULT)
                .withArgument(ARGS[0])
                .withArgument(ARGS[1])
                .withArchive(DEFAULT)
                .withFile(DEFAULT);

        final SqoopAction action = builder.build();

        final SqoopActionBuilder fromExistingBuilder = getBuilderInstance(action);

        final String newName = "fromExisting_" + NAME;
        fromExistingBuilder.withName(newName)
                .withoutArgument(ARGS[1])
                .withArgument(ARGS[2]);

        final SqoopAction modifiedAction = fromExistingBuilder.build();

        assertEquals(newName, modifiedAction.getName());
        assertEquals(action.getNameNode(), modifiedAction.getNameNode());

        final Map<String, String> expectedConfiguration = new LinkedHashMap<>();
        expectedConfiguration.put(MAPRED_JOB_QUEUE_NAME, DEFAULT);
        assertEquals(expectedConfiguration, modifiedAction.getConfiguration());

        assertEquals(Arrays.asList(ARGS[0], ARGS[2]), modifiedAction.getArguments());

        assertEquals(PATH_TO_DELETE, modifiedAction.getPrepare().getDeletes().get(0).getPath());
        assertEquals(PATH_TO_MKDIR, modifiedAction.getPrepare().getMkdirs().get(0).getPath());

        assertEquals(1024L, modifiedAction.getLauncher().getMemoryMb());
        assertEquals(2L, modifiedAction.getLauncher().getVCores());
        assertEquals(DEFAULT, modifiedAction.getLauncher().getQueue());
        assertEquals(DEFAULT, modifiedAction.getLauncher().getSharelib());
        assertEquals(DEFAULT, modifiedAction.getLauncher().getViewAcl());
        assertEquals(DEFAULT, modifiedAction.getLauncher().getModifyAcl());

        assertEquals(action.getCommand(), modifiedAction.getCommand());
    }
}