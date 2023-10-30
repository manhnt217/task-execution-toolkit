package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.ExecutionLog;
import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author manhnguyen
 */
public class Main {

    public static final String SQL = "DECLARE " +
            "    p varchar2(10);" +
            "    c number \\\\:= 12111; " +
            "BEGIN " +
            "    select DUMMY into p from dual; " +
            "    insert into TESTPLSQL(RECORD_NAME, CREATED) values ('Hell o112 PL/SQL ' || c, systimestamp);" +
            "    DBMS_OUTPUT.PUT_LINE('Got a result: ================ ' || p ); " +
            "END;";

    public static void main(String[] args) throws JsonProcessingException, ConfigurationException {
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

        LinearCompositeTask task = new LinearCompositeTask("doesntMatterNow", Lists.newArrayList(task1, task2, task3));

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
                    put("hibernate.connection.username", "foo1");
                    put("hibernate.connection.password", "foo");
                    put("hibernate.hikari.minimumIdle", "2");
                    put("hibernate.hikari.maximumPoolSize", "5");
                }}
        );

        String executionId = UUID.randomUUID().toString();
        try {
            TestUtil.executeTask(task, null, TestUtil.OM.valueToTree(input), logHandler, executionId);
        } catch (TaskException e) {
            logHandler.error(executionId, "doesntmatter", "Failed to execute activity", e);
        }

        List<ExecutionLog> logs = logHandler.getLogs();
        ExecutionLog lastLog = logs.get(logs.size() - 1);
        System.out.println("Last log");
        System.out.println(lastLog.getContent());
    }
}
