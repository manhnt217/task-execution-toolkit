package io.github.manhnt217.task.task_executor.common.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataSourceConfig {

    private DataSource dataSource;

    private String configKey;

    private String configValue;
}
