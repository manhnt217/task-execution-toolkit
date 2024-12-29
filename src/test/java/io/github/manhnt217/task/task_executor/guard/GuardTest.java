package io.github.manhnt217.task.task_executor.guard;

import io.github.manhnt217.task.task_executor.TestUtil;
import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.activity.ContainerActivity;
import io.github.manhnt217.task.task_executor.process.DefaultLogger;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.TaskExecutionException;
import io.github.manhnt217.task.task_executor.task.TemplateTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.UUID;

public class GuardTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testSimpleGuard.png">
     * @throws TaskExecutionException
     */
    @Test
    public void testSimpleGuard() throws ActivityException {
        DefaultLogger logHandler = new DefaultLogger();

        TemplateTask task1 = new TemplateTask("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}");
        task1.setTemplateName("LogTemplate");

        TemplateTask task2 = new TemplateTask("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}");
        task2.setTemplateName("LogTemplate");


        CompoundTask compoundTask = new CompoundTask();

        compoundTask.addActivity(task1);
        compoundTask.addActivity(task2);

        compoundTask.linkFromStart(task1, "3 > 5");
        compoundTask.linkFromStart(task2, "10 - 3 == 7");

        compoundTask.linkToEnd(task1);
        compoundTask.linkToEnd(task2);

        TestUtil.executeTask(compoundTask, null, logHandler, UUID.randomUUID().toString());
        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(1));
        assertThat(logs.get(0).getContent(), is("task2"));
    }

    /**
     * <img src="{@docRoot}/doc-files/images/testOtherwise.png">
     * @throws TaskExecutionException
     */
    @Test
    public void testOtherwise() throws ActivityException {
        DefaultLogger logHandler = new DefaultLogger();

        TemplateTask task1 = new TemplateTask("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}");
        task1.setTemplateName("LogTemplate");

        TemplateTask task2 = new TemplateTask("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}");
        task2.setTemplateName("LogTemplate");

        TemplateTask task3 = new TemplateTask("task3");
        task3.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task3\"}");
        task3.setTemplateName("LogTemplate");

        CompoundTask compoundTask = new CompoundTask();

        compoundTask.addActivity(task1);
        compoundTask.addActivity(task2);
        compoundTask.addActivity(task3);

        compoundTask.linkFromStart(task1, "3 > 5");
        compoundTask.linkFromStart(task2, ContainerActivity.OTHERWISE_GUARD_EXP);
        compoundTask.linkFromStart(task3, "3 == 3");

        compoundTask.linkToEnd(task1);
        compoundTask.linkToEnd(task2);
        compoundTask.linkToEnd(task3);

        TestUtil.executeTask(compoundTask, null, logHandler, UUID.randomUUID().toString());
        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(1));
        assertThat(logs.get(0).getContent(), is("task3"));
    }

    @Test
    public void testConflictedGuards() {
        TemplateTask task1 = new TemplateTask("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}");
        task1.setTemplateName("LogTemplate");

        TemplateTask task2 = new TemplateTask("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}");
        task2.setTemplateName("LogTemplate");


        CompoundTask compoundTask = new CompoundTask();

        compoundTask.addActivity(task1);
        compoundTask.addActivity(task2);

        compoundTask.linkFromStart(task1, "3 > 5");

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> compoundTask.linkFromStart(task2, "3 > 5"));
        assertThat(ex.getMessage(), containsString("already been added for activity"));
    }
}
