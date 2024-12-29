package io.github.manhnt217.task.sample.test.guard;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.activity.simple.FromLastActivity;
import io.github.manhnt217.task.core.activity.simple.MapperActivity;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.persistence.model.ActivityGroupDto;
import io.github.manhnt217.task.persistence.model.ActivityLinkDto;
import io.github.manhnt217.task.persistence.model.FunctionDto;
import io.github.manhnt217.task.persistence.model.activity.FromLastActivityDto;
import io.github.manhnt217.task.persistence.model.activity.MapperActivityDto;
import io.github.manhnt217.task.persistence.service.TaskService;
import io.github.manhnt217.task.plugin.Log;
import io.github.manhnt217.task.sample.SimpleEngineRepository;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import io.github.manhnt217.task.sample.test.helper.SampleInput;
import io.github.manhnt217.task.sample.test.helper.SampleOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;

import static io.github.manhnt217.task.core.context.ActivityContext.from;
import static io.github.manhnt217.task.core.task.function.Function.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * @author manh nguyen
 */
@ExtendWith(MockitoExtension.class)
public class GuardTest extends AbstractEngineTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testSimpleGuard.png">
     */
    @Test
    public void testSimpleGuard() throws ConfigurationException, TaskException, IOException, ActivityException {

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p1\"}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p2\"}")
                .build();

        Function<Void, Void> func = ActivityBuilder
                .routine("c1")
                .linkFromStart(p1, "3 > 5")
                .linkFromStart(p2, "10 - 3 == 7")
                .linkToEnd(p1)
                .linkToEnd(p2)
                .build();

        RootContext context = new RootContext(null, repo, logger);
        func.exec(null, context);

        verify(logger).info(any(), any(), any(), eq("p2"));
    }

    /**
     * <img src="{@docRoot}/doc-files/images/testOtherwise.png">
     */
    @Test
    public void testOtherwise() throws ConfigurationException, TaskException, IOException, ActivityException {
        PluginActivity task1 = ActivityBuilder
                .plugin("task1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}")
                .build();

        PluginActivity task2 = ActivityBuilder
                .plugin("task2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}")
                .build();

        PluginActivity task3 = ActivityBuilder
                .plugin("task3", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task3\"}")
                .build();

        Function<Void, Void> func = ActivityBuilder
                .routine("c1")
                .linkFromStart(task1, "3 > 5")
                .linkFromStart(task2, Group.OTHERWISE_GUARD_EXP)
                .linkFromStart(task3, "3 == 3")
                .linkToEnd(task1, null)
                .linkToEnd(task2, null)
                .linkToEnd(task3, null)
                .build();

        func.exec(null, new RootContext(null, repo, logger));
        verify(logger).info(any(), any(), any(), eq("task3"));
    }

    @Test
    public void testConflictedGuards() {

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p1\"}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p2\"}")
                .build();

        ConfigurationException ex = assertThrows(ConfigurationException.class,
                () -> ActivityBuilder
                        .routine("c1")
                        .linkFromStart(p1, "3 > 5")
                        .linkFromStart(p2, "3 > 5")
                        .linkToEnd(p1, null)
                        .linkToEnd(p2, null)
                        .build()
        );
        assertThat(ex.getMessage(), is("Configuration failed. Message = Guard '3 > 5' already been added for activity '" + START_ACTIVITY_NAME + "'"));
    }

    @Test
    public void testNoTrueGuard(@Mock EngineRepository repo, @Mock TaskLogger logger) throws ConfigurationException {
        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p1\"}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p2\"}")
                .build();

        Function<Void, Void> func = ActivityBuilder
                .routine("c1")
                .linkFromStart(p1, "3 > 5")
                .linkFromStart(p2, "10 / 7 == 1")
                .linkToEnd(p1, null)
                .linkToEnd(p2, null)
                .build();

        assertThrows(ActivityException.class, () -> func.exec(null, new RootContext(null, repo, logger)));
    }

    @Test
    public void testFromLastActivity() throws TaskException, ActivityException, ConfigurationException {

        MapperActivity p1 = new MapperActivity("p1");
        p1.setInputMapping("{\"category\": \"high\", \"important\": true, \"rate\": -5.0}");
        MapperActivity p2 = new MapperActivity("p2");
        p2.setInputMapping("{\"category\": \"low\"}");

        FromLastActivity pickFirst = new FromLastActivity("pickFirst");

        Function<SampleInput, SampleOutput> func = ActivityBuilder
                .function("c1", SampleInput.class, SampleOutput.class)
                .linkFromStart(p1, from(START_ACTIVITY_NAME) + ".age > 10")
                .linkFromStart(p2, Group.OTHERWISE_GUARD_EXP)
                .link(p1, pickFirst)
                .link(p2, pickFirst)
                .linkToEnd(pickFirst)
                .outputMapping(from("pickFirst"))
                .build();

        RootContext context = new RootContext(null, repo, logger);
        SampleOutput result1 = func.exec(new SampleInput("Kevin", 15, "London"), context);
        SampleOutput result2 = func.exec(new SampleInput("Stacy", 4, "New Jersey"), context);

        assertThat(result1.getCategory(), is("high"));
        assertThat(result1.isImportant(), is(true));
        assertThat(result1.getRate(), is(-5.0));

        assertThat(result2.getCategory(), is("low"));
        assertThat(result2.isImportant(), is(false));
        assertThat(result2.getRate(), is(0.0));
    }

    @Test
    public void testFromLastActivity_Dto() throws TaskException, ActivityException, ConfigurationException, JsonProcessingException {

        MapperActivityDto p1 = new MapperActivityDto();
        p1.setName("p1");
        p1.setInputMapping("{\"category\": \"high\", \"important\": true, \"rate\": -5.0}");

        MapperActivityDto p2 = new MapperActivityDto();
        p2.setName("p2");
        p2.setInputMapping("{\"category\": \"low\"}");

        FromLastActivityDto pickFirst = new FromLastActivityDto();
        pickFirst.setName("pickFirst");

        ActivityGroupDto groupDto = new ActivityGroupDto();

        groupDto.setActivities(Arrays.asList(p1, p2, pickFirst));
        groupDto.setLinks(Arrays.asList(
                new ActivityLinkDto(){{ setFrom(START_ACTIVITY_NAME); setTo(p1.getName()); setGuard(from(START_ACTIVITY_NAME) + ".age > 10"); }},
                new ActivityLinkDto(){{ setFrom(START_ACTIVITY_NAME); setTo(p2.getName()); setGuard(Group.OTHERWISE_GUARD_EXP); }},
                new ActivityLinkDto(){{ setFrom(p1.getName()); setTo(pickFirst.getName());  }},
                new ActivityLinkDto(){{ setFrom(p2.getName()); setTo(pickFirst.getName());  }},
                new ActivityLinkDto(){{ setFrom(pickFirst.getName()); setTo(END_ACTIVITY_NAME); }}
        ));

        FunctionDto functionDto = new FunctionDto();
        functionDto.setName("c1");
        functionDto.setInputClass(SampleInput.class.getName());
        functionDto.setOutputClass(SampleOutput.class.getName());
        functionDto.setGroup(groupDto);
        functionDto.setOutputMapping(from("pickFirst"));

        System.out.println(TestUtil.OM.writeValueAsString(functionDto));

        @SuppressWarnings("unchecked")
        Function<SampleInput, SampleOutput> func = TaskService.instance().buildFunction(functionDto);

        RootContext context = new RootContext(null, repo, logger);
        SampleOutput result1 = func.exec(new SampleInput("Kevin", 15, "London"), context);
        SampleOutput result2 = func.exec(new SampleInput("Stacy", 4, "New Jersey"), context);

        assertThat(result1.getCategory(), is("high"));
        assertThat(result1.isImportant(), is(true));
        assertThat(result1.getRate(), is(-5.0));

        assertThat(result2.getCategory(), is("low"));
        assertThat(result2.isImportant(), is(false));
        assertThat(result2.getRate(), is(0.0));
    }
}
