package io.github.manhnt217.task.sample.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.manhnt217.task.sample.LinearCompositeTask;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.CurlTask;
import io.github.manhnt217.task.sample.plugin.LogTask;
import io.github.manhnt217.task.sample.plugin.SqlTask;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.ExecutionLog;
import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.task_engine.task.CompositeTask;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author manhnguyen
 */
public class ComplexTaskTest {

    public static final String SQL = "DECLARE " +
            "    p varchar2(10);" +
            "    c number \\\\:= 12111; " +
            "BEGIN " +
            "    select DUMMY into p from dual; " +
            "    insert into TESTPLSQL(RECORD_NAME, CREATED) values ('Hell o112 PL/SQL ' || c, systimestamp);" +
            "    DBMS_OUTPUT.PUT_LINE('Got a result: ================ ' || p || chr(10) || ' ABC' ); " +
            "END;";

    @Test
    public void testComplex1() throws JsonProcessingException, ConfigurationException, TaskException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = ActivityBuilder
                .task("task1", ActivityBuilder.plugin(CurlTask.class.getName()).build())
                .inputMapping(ActivityContext.FROM_PROPS)
                .build();

        TaskBasedActivity task2 = ActivityBuilder
                .task("task2", ActivityBuilder.plugin(LogTask.class.getName()).build())
                .inputMapping("{\"severity\": \"INFO\", \"message\": \"Status code is \" + .task1.statusCode}")
                .build();

        TaskBasedActivity task3 = ActivityBuilder
                .task("task3", ActivityBuilder.plugin(SqlTask.class.getName()).build())
                .inputMapping("{\"sql\":\"" + SQL + "\"} + " + ActivityContext.FROM_PROPS)
                .build();

        LinearCompositeTask task = new LinearCompositeTask("t1", Lists.newArrayList(task1, task2, task3));

        Map<String, Object> props = ImmutableMap.of(
                "url", "https://example.com",
                "method", "GET",
                "dataSource", "foo",
                "dataSourceProperties", new HashMap<String, String>() {{
                    put("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver");
                    put("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
                    put("hibernate.hikari.connectionTimeout", "20000");
                    put("hibernate.hikari.idleTimeout", "30000");
                    put("hibernate.connection.url", "jdbc:oracle:thin:@vm:1521/FORTNAWCS");
                    put("hibernate.connection.username", "foo");
                    put("hibernate.connection.password", "foo");
                    put("hibernate.hikari.minimumIdle", "2");
                    put("hibernate.hikari.maximumPoolSize", "5");
                }}
        );
        JsonNode output = TestUtil.executeTask(task, TestUtil.OM.valueToTree(props), null, logHandler, UUID.randomUUID().toString());
        Map<String, Object> out = TestUtil.OM.treeToValue(output, Map.class);
        assertThat(out.size(), is(3));
        assertThat(out, hasKey("task1"));
        assertThat(out, hasKey("task2"));
        assertThat(out, hasKey("task3"));

        assertThat((Map<String, Object>) out.get("task1"), hasKey("statusCode"));
        assertThat(((Map) out.get("task2")).size(), is(0));

        assertThat(logHandler.getLogs().size(), greaterThanOrEqualTo(1));
        Optional<ExecutionLog> optionalExecutionLog = logHandler.getLogs().stream().filter(logLine -> "task2".equals(logLine.getActivityName())).findAny();
        assertThat(optionalExecutionLog.isPresent(), is(true));
        assertThat(optionalExecutionLog.get().getContent(), is("Status code is 200"));
    }

    @Test
    public void testComplex2_PassingInputFromParent() throws JsonProcessingException, ConfigurationException, TaskException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity act1 = ActivityBuilder
                .task("act1", ActivityBuilder.plugin(CurlTask.class.getName()).build())
                .inputMapping("{\"url\": ." + CompositeTask.START_ACTIVITY_NAME + ".url, \"method\": \"GET\"}")
                .build();

        LinearCompositeTask task = new LinearCompositeTask("c1", Lists.newArrayList(act1));

        Map<String, Object> input = ImmutableMap.of("url", "https://example.com");
        JsonNode output = TestUtil.executeTask(task, null, TestUtil.OM.valueToTree(input), logHandler, UUID.randomUUID().toString());
        Map<String, Object> out = TestUtil.OM.treeToValue(output, Map.class);
        assertThat(out.size(), is(2));
        assertThat(out, hasKey(CompositeTask.START_ACTIVITY_NAME));
        assertThat(out, hasKey("act1"));

        assertThat((Map<String, Object>) out.get("act1"), hasKey("statusCode"));
    }
}
