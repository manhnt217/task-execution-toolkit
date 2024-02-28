package io.github.manhnt217.task.plugin.source.http;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter @Setter
public class HttpEventsourceResponse {
    private String body;
    private String contentType;
}
