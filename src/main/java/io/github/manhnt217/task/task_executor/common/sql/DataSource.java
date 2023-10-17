package io.github.manhnt217.task.task_executor.common.sql;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class DataSource {

    private String name;

    private Map<String, DataSourceConfig> configs;
}
