package io.github.manhnt217.task.sample;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.manhnt217.task.sample.plugin.CurlTask;
import io.github.manhnt217.task.sample.plugin.LogTask;
import io.github.manhnt217.task.sample.plugin.SqlTask;
import io.github.manhnt217.task.core.activity.DefaultActivityLogger;
import io.github.manhnt217.task.core.activity.ExecutionLog;
import io.github.manhnt217.task.core.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;

import java.io.IOException;
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

    public static void main(String[] args) throws IOException, ConfigurationException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = ActivityBuilder
                .task("task1")
                .taskName(CurlTask.class.getName())
                .inputMapping(ActivityContext.FROM_PROPS)
                .build();

        TaskBasedActivity task2 = ActivityBuilder
                .task("task2")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\", \"message\": \"Status code is \" + .task1.statusCode}")
                .build();

        TaskBasedActivity task3 = ActivityBuilder
                .task("task3")
                .taskName(SqlTask.class.getName())
                .inputMapping("{\"sql\":\"" + SQL + "\"} + " + ActivityContext.FROM_PROPS)
                .build();


        LinearCompositeTask task = new LinearCompositeTask("doesntMatterNow", Lists.newArrayList(task1, task2, task3));

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

        String executionId = UUID.randomUUID().toString();
        try {
            TestUtil.executeTask(task, TestUtil.OM.valueToTree(props), null, logHandler, executionId);
        } catch (TaskException e) {
            logHandler.error(executionId, "doesntmatter", "Failed to execute activity", e);
        }

        List<ExecutionLog> logs = logHandler.getLogs();
        ExecutionLog lastLog = logs.get(logs.size() - 1);
        System.out.println("Last log");
        System.out.println(lastLog.getContent());
    }
}
