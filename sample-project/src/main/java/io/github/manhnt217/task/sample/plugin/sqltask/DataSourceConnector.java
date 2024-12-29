package io.github.manhnt217.task.sample.plugin.sqltask;

import io.github.manhnt217.task.sample.ConsoleColors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.util.function.Consumer;

/**
 * @author manhnguyen
 */
@Slf4j
public class DataSourceConnector implements AutoCloseable {

    private final DataSource dataSource;
    private SessionFactory sessionFactory;

    public DataSourceConnector(DataSource dataSource) {
        this.dataSource = dataSource;
        try {
            configure();
        } catch (Exception e) {
            throw e;
        }
    }

    private void configure() {

        Configuration configuration = new Configuration();
        // Set default value
        configuration.setProperty(Environment.ORDER_INSERTS, "true");
        configuration.setProperty(Environment.ORDER_UPDATES, "true");
        configuration.setProperty(Environment.SHOW_SQL, "false");
        configuration.setProperty(Environment.BATCH_VERSIONED_DATA, "true");
        configuration.setProperty(Environment.STATEMENT_BATCH_SIZE, "30");
        configuration.setProperty(Environment.DEFAULT_BATCH_FETCH_SIZE, "32");
        configuration.setProperty(Environment.IMPLICIT_NAMING_STRATEGY, "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");

        dataSource.getConfigs().values().forEach(
                cfg -> configuration.setProperty(cfg.getConfigKey(), cfg.getConfigValue())
        );

        if (StringUtils.isBlank(configuration.getProperty(Environment.DRIVER))) {
            throw new RuntimeException("Missing configuration: " + Environment.DRIVER);
        }
        if (StringUtils.isBlank(configuration.getProperty(Environment.DIALECT))) {
            throw new RuntimeException("Missing configuration: " + Environment.DIALECT);
        }
        if (StringUtils.isBlank(configuration.getProperty("hibernate.hikari.connectionTimeout"))) {
            throw new RuntimeException("Missing configuration for HikariCP...");
        }

//        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
    }

    public void executeTransation(Consumer<Session> tx) {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();
            session.beginTransaction();
            log.info("Start transaction");
            tx.accept(session);
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null
                    && session.getTransaction().getStatus() == TransactionStatus.ACTIVE) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                if (session.getTransaction() != null
                        && session.getTransaction().getStatus() == TransactionStatus.ACTIVE) {
                    session.getTransaction().commit();
                }
                log.info("Finish transaction");
                session.close();
            }
        }
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}