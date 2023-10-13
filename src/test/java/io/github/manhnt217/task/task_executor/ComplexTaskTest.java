package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.manhnt217.task.task_executor.process.DefaultLogger;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TaskExecutionException;
import io.github.manhnt217.task.task_executor.task.TemplateTask;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.manhnt217.task.task_executor.common.CommonUtil.OM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
    public void testComplex1() throws TaskExecutionException, JsonProcessingException {
        DefaultLogger logHandler = new DefaultLogger();

        TemplateTask task1 = new TemplateTask("task1");
        task1.setInputMappingExpression("._PARENT_");
        task1.setTemplateName("CurlTemplate");
        task1.setEndLogExpression("\"Finish task 1\"");

        TemplateTask task2 = new TemplateTask("task2");
        task2.setTemplateName("LogTemplate");
        task2.setInputMappingExpression("{\"severity\": \"INFO\", \"message\": \"Status code is \" + .task1.statusCode}");

        TemplateTask task3 = new TemplateTask("task3");
        task3.setTemplateName("SqlTemplate");
        task3.setInputMappingExpression("{\"sql\":\"" + SQL + "\"} + ._PARENT_");

        task2.setDependencies(Sets.newHashSet(task1.getName()));
        task3.setDependencies(Sets.newHashSet(task2.getName()));

        CompoundTask compoundTask1 = new CompoundTask("c1", Lists.newArrayList(task1, task2, task3));
        compoundTask1.setInputMappingExpression("._PARENT_");

        Map<String, Object> input = ImmutableMap.of(
                "url", "https://example.com",
                "method", "GET",
                "dataSource", "foo",
                "dataSourceProperties", new HashMap<String, String>() {{
                    put("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver");
                    put("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
                    put("hibernate.hikari.connectionTimeout", "20000");
                    put("hibernate.hikari.idleTimeout", "30000");
                    put("hibernate.connection.url", "jdbc:oracle:thin:@dbserver:1521/FORTNAWCS");
                    put("hibernate.connection.username", "foo");
                    put("hibernate.connection.password", "foo");
                    put("hibernate.hikari.minimumIdle", "2");
                    put("hibernate.hikari.maximumPoolSize", "5");
                }}
        );
        JsonNode output = TestUtil.executeTask(compoundTask1, Task.OBJECT_MAPPER.valueToTree(input), logHandler, UUID.randomUUID().toString());
        Map<String, Object> out = OM.treeToValue(output, Map.class);
        assertThat(out.size(), is(3));
        assertThat(out, hasKey("task1"));
        assertThat(out, hasKey("task2"));
        assertThat(out, hasKey("task3"));

        assertThat((Map<String, Object>) out.get("task1"), hasKey("statusCode"));
        assertThat(((Map) out.get("task2")).size(), is(0));

        assertThat(logHandler.getLogs().size(), greaterThanOrEqualTo(2));
        assertThat(logHandler.getLogs().get(0).getContent(), is("Finish task 1"));
        assertThat(logHandler.getLogs().get(1).getContent(), is("Status code is 200"));
    }
}
