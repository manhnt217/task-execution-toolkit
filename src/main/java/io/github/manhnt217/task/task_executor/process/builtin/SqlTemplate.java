package io.github.manhnt217.task.task_executor.process.builtin;

import io.github.manhnt217.task.task_executor.process.LogHandler;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.builtin.sql.DataSource;
import io.github.manhnt217.task.task_executor.process.builtin.sql.DataSourceConfig;
import io.github.manhnt217.task.task_executor.process.builtin.sql.DataSourceConnector;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.jdbc.AbstractWork;
import org.hibernate.query.NativeQuery;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class SqlTemplate extends Template<SqlTemplate.Input, Object> {
    @Override
    protected Class<? extends Input> getInputClass() {
        return Input.class;
    }

    @Override
    public Object exec(Input input, LogHandler logHanlder) {

        DataSourceConnector dataSourceConnector = null;
        try {
            DataSource dataSource = getDataSource();
            dataSourceConnector = new DataSourceConnector(dataSource);
            dataSourceConnector.executeTransation(session -> this.execute(session, input.getSql()));
        } catch (Exception e) {
            log.error("Got an exception while executing query: " + input.getSql(), e);
        } finally {
            if (dataSourceConnector != null) {
                dataSourceConnector.close();
            }
        }

        return new Object();
    }

    private void execute(Session session, String sql) {

        NativeQuery enableDMBSOutputQuery = session.createNativeQuery("begin dbms_output.enable(); end;");
        enableDMBSOutputQuery.executeUpdate();

        NativeQuery query = session.createNativeQuery(sql);
        query.executeUpdate();

        session.doWork(new AbstractWork() {
            @Override
            public void execute(Connection connection) throws SQLException {
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
                                .forEach(System.out::println);
                    } finally {
                        if (array != null)
                            array.free();
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        NativeQuery disableDMBSOutputQuery = session.createNativeQuery("begin dbms_output.disable(); end;");
        disableDMBSOutputQuery.executeUpdate();
    }

    // Hardcode data
    private static DataSource getDataSource() {
        DataSource dataSource = new DataSource();
        Map<String, DataSourceConfig> configs = new HashMap<>();
        configs.put("hibernate.connection.driver_class", new DataSourceConfig(dataSource, "hibernate.connection.driver_class", "oracle.jdbc.OracleDriver"));
        configs.put("hibernate.dialect", new DataSourceConfig(dataSource, "hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect"));
        configs.put("hibernate.hikari.connectionTimeout", new DataSourceConfig(dataSource, "hibernate.hikari.connectionTimeout", "20000"));
        configs.put("hibernate.hikari.idleTimeout", new DataSourceConfig(dataSource, "hibernate.hikari.idleTimeout", "30000"));
        configs.put("hibernate.connection.url", new DataSourceConfig(dataSource, "hibernate.connection.url", "jdbc:oracle:thin:@dbserver:1521/FORTNAWCS"));
        configs.put("hibernate.connection.username", new DataSourceConfig(dataSource, "hibernate.connection.username", "housekeeping"));
        configs.put("hibernate.connection.password", new DataSourceConfig(dataSource, "hibernate.connection.password", "housekeeping"));
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
