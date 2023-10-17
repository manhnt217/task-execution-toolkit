package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.activity.impl.DefaultActivityLogger;
import io.github.manhnt217.task.task_executor.activity.impl.ExecutionLog;
import io.github.manhnt217.task.task_executor.activity.impl.TaskBasedActivity;
import io.github.manhnt217.task.task_executor.task.CompositeTask;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static io.github.manhnt217.task.task_executor.context.ActivityContext.OBJECT_MAPPER;
import static io.github.manhnt217.task.task_executor.common.CommonUtil.OM;
import static org.hamcrest.MatcherAssert.assertThat;
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
    public void testComplex1() throws ActivityException, JsonProcessingException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("._PROPS_");
        task1.setTask(TestUtil.loadTask("CurlTask"));

        TaskBasedActivity task2 = new TaskBasedActivity("task2");
        task2.setTask(TestUtil.loadTask("LogTask"));
        task2.setInputMapping("{\"severity\": \"INFO\", \"message\": \"Status code is \" + .task1.statusCode}");

        TaskBasedActivity task3 = new TaskBasedActivity("task3");
        task3.setTask(TestUtil.loadTask("SqlTask"));
        task3.setInputMapping("{\"sql\":\"" + SQL + "\"} + ._PROPS_");

        TaskBasedActivity complexTask = new TaskBasedActivity("complexTask");
        complexTask.setTask(new CompositeTask("doesntMatterNow", Lists.newArrayList(task1, task2, task3)));

        Map<String, Object> input = ImmutableMap.of(
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
        JsonNode output = TestUtil.executeActivity(complexTask, OBJECT_MAPPER.valueToTree(input), logHandler, UUID.randomUUID().toString());
        Map<String, Object> out = OM.treeToValue(output, Map.class);
        assertThat(out.size(), is(4));
        assertThat(out, hasKey(CompositeTask.START_DEFAULT_NAME));
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
    public void testComplex2_PassingInputFromParent() throws ActivityException, JsonProcessingException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("{\"url\": ." + CompositeTask.START_DEFAULT_NAME + ".request, \"method\": \"GET\"}");
        task1.setTask(TestUtil.loadTask("CurlTask"));

        TaskBasedActivity complexTask = new TaskBasedActivity("complexTask");
        complexTask.setTask(new CompositeTask("c1", Lists.newArrayList(task1)));
        complexTask.setInputMapping("{\"request\": ._PROPS_.url}");

        Map<String, Object> input = ImmutableMap.of("url", "https://example.com");
        JsonNode output = TestUtil.executeActivity(complexTask, OBJECT_MAPPER.valueToTree(input), logHandler, UUID.randomUUID().toString());
        Map<String, Object> out = OM.treeToValue(output, Map.class);
        assertThat(out.size(), is(2));
        assertThat(out, hasKey(CompositeTask.START_DEFAULT_NAME));
        assertThat(out, hasKey("task1"));

        assertThat((Map<String, Object>) out.get("task1"), hasKey("statusCode"));
    }
}
