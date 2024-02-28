package io.github.manhnt217.task.plugin.source.http;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter @Setter
public class HttpEventSourceConfig {

    private String host;
    private int port;
}
