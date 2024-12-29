package io.github.manhnt217.task.sample.plugin;

import io.github.manhnt217.task.sample.plugin.sqltask.DataSource;
import io.github.manhnt217.task.sample.plugin.sqltask.DataSourceConfig;
import io.github.manhnt217.task.sample.plugin.sqltask.DataSourceConnector;
import io.github.manhnt217.task.task_engine.task.PluginTask;
import io.github.manhnt217.task.task_engine.task.TaskLogger;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author manhnguyen
 */
@Slf4j
public class SqlTask extends PluginTask<SqlTask.Input, Object> {

    public SqlTask(String name) {
        super(name);
    }

    @Override
    protected Class<? extends Input> getInputClass() {
        return Input.class;
    }

    @Override
    public Object exec(Input input, TaskLogger taskLogger) throws Exception {

        DataSourceConnector dataSourceConnector = null;
        try {
            DataSource dataSource = getDataSource(input);
            dataSourceConnector = new DataSourceConnector(dataSource);
            dataSourceConnector.executeTransation(session -> this.execute(session, input.getSql(), taskLogger));
        } finally {
            if (dataSourceConnector != null) {
                dataSourceConnector.close();
            }
        }

        return new Object();
    }

    private void execute(Session session, String sql, TaskLogger log) {
        try {
            enabledDBMSOutput(session);

            NativeQuery query = session.createNativeQuery(sql);
            query.executeUpdate();

            session.doWork((Connection connection) -> logOutput(log, connection));
        } finally {
            disableDBMSOutput(session);
        }
    }

    private static void logOutput(TaskLogger log, Connection connection) {
        try (CallableStatement call = connection.prepareCall(
                "declare "
                        + " num integer := 1000;"
                        + " begin "
                        + "   dbms_output.get_lines(?, num);"
                        + " end;"
        )) {
            call.registerOutParameter(1, Types.ARRAY,
                    "DBMSOUTPUT_LINESARRAY");
            call.execute();

            Array array = null;
            try {
                array = call.getArray(1);
                Stream.of((Object[]) array.getArray())
                        .filter(Objects::nonNull)
                        .forEach(o -> log.info(String.valueOf(o)));
            } finally {
                if (array != null)
                    array.free();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void disableDBMSOutput(Session session) {
        NativeQuery disableDMBSOutputQuery = session.createNativeQuery("begin dbms_output.disable(); end;");
        disableDMBSOutputQuery.executeUpdate();
    }

    private static void enabledDBMSOutput(Session session) {
        NativeQuery enableDMBSOutputQuery = session.createNativeQuery("begin dbms_output.enable(); end;");
        enableDMBSOutputQuery.executeUpdate();
    }

    private DataSource getDataSource(Input input) {
        DataSource dataSource = new DataSource();
        dataSource.setName(input.getDataSource());
        dataSource.setConfigs(buildDSConfigs(input));

        return dataSource;
    }

    private static Map<String, DataSourceConfig> buildDSConfigs(Input input) {
        Map<String, String> dataSourceProperties = input.getDataSourceProperties();
        Map<String, DataSourceConfig> configs = new HashMap<>(dataSourceProperties.size());
        for (Map.Entry<String, String> entry : dataSourceProperties.entrySet()) {
            configs.computeIfAbsent(entry.getKey(), k -> {
                DataSourceConfig hkDataSourceConfig = new DataSourceConfig();
                hkDataSourceConfig.setConfigKey(entry.getKey());
                hkDataSourceConfig.setConfigValue(entry.getValue());
                return hkDataSourceConfig;
            });
        }
        return configs;
    }

    @Getter
    @Setter
    public static class Input {
        private String sql;
        private String dataSource;
        private Map<String, String> dataSourceProperties;
    }
}
