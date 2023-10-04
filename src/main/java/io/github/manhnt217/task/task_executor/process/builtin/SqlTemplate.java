package io.github.manhnt217.task.task_executor.process.builtin;

import io.github.manhnt217.task.task_executor.common.sql.DataSource;
import io.github.manhnt217.task.task_executor.common.sql.DataSourceConfig;
import io.github.manhnt217.task.task_executor.common.sql.DataSourceConnector;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.TemplateLogger;
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

@Slf4j
public class SqlTemplate extends Template<SqlTemplate.Input, Object> {

    public SqlTemplate() {
        super();
    }

    @Override
    protected Class<? extends Input> getInputClass() {
        return Input.class;
    }

    @Override
    public Object exec(Input input, TemplateLogger logger) throws Exception {

        DataSourceConnector dataSourceConnector = null;
        try {
            DataSource dataSource = getDataSource(input.getDataSource());
            dataSourceConnector = new DataSourceConnector(dataSource);
            dataSourceConnector.executeTransation(session -> this.execute(session, input.getSql(), logger));
        } catch (Exception e) {
            throw new Exception("Cannot execute following SQL script: [" + input.getSql() + "]", e);
        } finally {
            if (dataSourceConnector != null) {
                dataSourceConnector.close();
            }
        }

        return new Object();
    }

    private void execute(Session session, String sql, TemplateLogger log) {
        try {
            enabledDBMSOutput(session);

            NativeQuery query = session.createNativeQuery(sql);
            query.executeUpdate();

            session.doWork((Connection connection) -> logOutput(log, connection));
        } finally {
            disableDBMSOutput(session);
        }
    }

    private static void logOutput(TemplateLogger log, Connection connection) {
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
        }
        catch (Exception e) {
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

    private DataSource getDataSource(String dataSourceName) {
        DataSource dataSource = new DataSource();
        dataSource.setName("foo");
        Map<String, DataSourceConfig> configs = new HashMap<>();
        configs.put("hibernate.connection.driver_class", new DataSourceConfig(dataSource, "hibernate.connection.driver_class", "oracle.jdbc.OracleDriver"));
        configs.put("hibernate.dialect", new DataSourceConfig(dataSource, "hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect"));
        configs.put("hibernate.hikari.connectionTimeout", new DataSourceConfig(dataSource, "hibernate.hikari.connectionTimeout", "20000"));
        configs.put("hibernate.hikari.idleTimeout", new DataSourceConfig(dataSource, "hibernate.hikari.idleTimeout", "30000"));
        configs.put("hibernate.connection.url", new DataSourceConfig(dataSource, "hibernate.connection.url", "jdbc:oracle:thin:@dbserver:1521/FORTNAWCS"));
        configs.put("hibernate.connection.username", new DataSourceConfig(dataSource, "hibernate.connection.username", "foo"));
        configs.put("hibernate.connection.password", new DataSourceConfig(dataSource, "hibernate.connection.password", "foo"));
        configs.put("hibernate.hikari.minimumIdle", new DataSourceConfig(dataSource, "hibernate.hikari.minimumIdle", "2"));
        configs.put("hibernate.hikari.maximumPoolSize", new DataSourceConfig(dataSource, "hibernate.hikari.maximumPoolSize", "5"));
        dataSource.setConfigs(configs);
        return dataSource;
    }

    @Getter
    @Setter
    public static class Input {
        private String sql;
        private String dataSource;
    }
}
