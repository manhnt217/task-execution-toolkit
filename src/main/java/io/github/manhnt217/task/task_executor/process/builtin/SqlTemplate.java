package io.github.manhnt217.task.task_executor.process.builtin;

import io.github.manhnt217.task.task_executor.process.LogHandler;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.builtin.sql.DataSource;
import io.github.manhnt217.task.task_executor.process.builtin.sql.DataSourceConnector;
import io.github.manhnt217.task.task_executor.process.builtin.sql.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SqlTemplate extends Template<String, Object> {
    @Override
    protected Class<? extends String> getInputClass() {
        return String.class;
    }

    @Override
    public Object exec(String sql, LogHandler logHanlder) {

        DataSourceConnector dataSourceConnector = null;
        try {
            DataSource dataSource = getDataSource();
            dataSourceConnector = new DataSourceConnector(dataSource);
            dataSourceConnector.executeTransation(session -> this.execute(session, sql));
        } catch (Exception e) {
            log.error("Got an exception while executing query: " + sql, e);
        } finally {
            if (dataSourceConnector != null) {
                dataSourceConnector.close();
            }
        }

        return new Object();
    }

    private void execute(Session session, String sql) {
        NativeQuery query = session.createNativeQuery(sql);
        List resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            System.out.println("Got result: " + resultList.get(0));
        }
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
        configs.put("hibernate.connection.username", new DataSourceConfig(dataSource, "hibernate.connection.username", "asnmanager"));
        configs.put("hibernate.connection.password", new DataSourceConfig(dataSource, "hibernate.connection.password", "asnmanager"));
        configs.put("hibernate.hikari.minimumIdle", new DataSourceConfig(dataSource, "hibernate.hikari.minimumIdle", "2"));
        configs.put("hibernate.hikari.maximumPoolSize", new DataSourceConfig(dataSource, "hibernate.hikari.maximumPoolSize", "5"));
        dataSource.setConfigs(configs);
        return dataSource;
    }
}
