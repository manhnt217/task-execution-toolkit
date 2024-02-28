package io.github.manhnt217.task.plugin.source.http;

import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.Map;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class HttpEventSourceRequest {
    private Map<String, Deque<String>> pathParams;
    private Map<String, Deque<String>> queryParams;
    private String body;
}
