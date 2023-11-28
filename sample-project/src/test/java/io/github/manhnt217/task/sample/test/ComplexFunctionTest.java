package io.github.manhnt217.task.sample.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.activity.ExecutionLog;
import io.github.manhnt217.task.core.activity.func.FunctionCallActivity;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.LinearFunction;
import io.github.manhnt217.task.sample.SimpleEngineRepository;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.Curl;
import io.github.manhnt217.task.sample.plugin.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author manh nguyen
 */
public class ComplexFunctionTest {

    public static final String SQL = "DECLARE " +
            "    p varchar2(10);" +
            "    c number \\\\:= 12111; " +
            "BEGIN " +
            "    select DUMMY into p from dual; " +
            "    insert into TESTPLSQL(RECORD_NAME, CREATED) values ('Hell o112 PL/SQL ' || c, systimestamp);" +
            "    DBMS_OUTPUT.PUT_LINE('Got a result: ================ ' || p || chr(10) || ' ABC' ); " +
            "END;";

    @Test
    public void testComplex1() throws IOException, ConfigurationException, TaskException {
        DefaultTaskLogger logHandler = new DefaultTaskLogger();

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Curl.class.getSimpleName())
                .inputMapping(ActivityContext.FROM_PROPS)
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\", \"message\": \"Status code is \" + .p1.statusCode}")
                .build();

        LinearFunction func = new LinearFunction("t1", Lists.newArrayList(p1, p2));

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
        JsonNode output = TestUtil.executeFunc(func, TestUtil.OM.valueToTree(props), null, logHandler, UUID.randomUUID().toString());
        Map<String, Object> out = TestUtil.OM.treeToValue(output, Map.class);
        assertThat(out.size(), is(2));
        assertThat(out, hasKey("p1"));
        assertThat(out, hasKey("p2"));

        assertThat((Map<String, Object>) out.get("p1"), hasKey("statusCode"));
        assertThat(((Map) out.get("p2")).size(), is(0));

        assertThat(logHandler.getLogs().size(), greaterThanOrEqualTo(1));
        Optional<ExecutionLog> optionalExecutionLog = logHandler.getLogs().stream().filter(logLine -> "p2".equals(logLine.getActivityName())).findAny();
        assertThat(optionalExecutionLog.isPresent(), is(true));
        assertThat(optionalExecutionLog.get().getContent(), is("Status code is 200"));
    }

    @Test
    public void testComplex2_PassingInputFromParent() throws IOException, ConfigurationException, TaskException {
        DefaultTaskLogger logHandler = new DefaultTaskLogger();

        PluginActivity act1 = ActivityBuilder
                .plugin("act1", Curl.class.getSimpleName())
                .inputMapping("{\"url\": ." + Function.START_ACTIVITY_NAME + ".url, \"method\": \"GET\"}")
                .build();

        LinearFunction func = new LinearFunction("c1", Lists.newArrayList(act1));

        Map<String, Object> input = ImmutableMap.of("url", "https://example.com");
        JsonNode output = TestUtil.executeFunc(func, null, TestUtil.OM.valueToTree(input), logHandler, UUID.randomUUID().toString());
        Map<String, Object> out = TestUtil.OM.treeToValue(output, Map.class);
        assertThat(out.size(), is(2));
        assertThat(out, hasKey(Function.START_ACTIVITY_NAME));
        assertThat(out, hasKey("act1"));

        assertThat((Map<String, Object>) out.get("act1"), hasKey("statusCode"));
    }

    @Test
    public void testRecursive() throws ConfigurationException, TaskException, JsonProcessingException {
        // Calculate the factorial
        String taskName = "r1";
        FunctionCallActivity callr1Activity = ActivityBuilder
                .funcCall("callr1")
                .funcName(taskName) // recursive call
                .inputMapping(".START | {\"n\": .n - 1, \"acc\": .acc * .n}")
                .build();
        Function r1 = ActivityBuilder
                .function(taskName)
                .linkStartToEnd(".START.n == 1")
                .linkFromStart(callr1Activity, Group.OTHERWISE_GUARD_EXP)
                .linkToEnd(callr1Activity)
                .outputMapping("if (.callr1) .callr1 else .START.acc")
                .build();

        SimpleEngineRepository simpleTaskRepository = new SimpleEngineRepository();
        simpleTaskRepository.registerFunction(r1);

        int n = 7;

        Map<String, Object> input = ImmutableMap.of(
                "n", n,
                "acc", 1);

        JsonNode output = TestUtil.executeFunc(
                taskName,
                null,
                TestUtil.OM.valueToTree(input),
                new DefaultTaskLogger(),
                UUID.randomUUID().toString(),
                simpleTaskRepository);

        Long result = TestUtil.OM.treeToValue(output, Long.class);
        assertThat(result, is(factorial(n)));

    }

    private long factorial(int n) {
        long f = 1;
        for (int i = 1; i <= n; i++) {
            f = f * i;
        }
        return f;
    }
}