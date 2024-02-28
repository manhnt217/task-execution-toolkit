package io.github.manhnt217.task.plugin.source.http;

import io.github.manhnt217.task.core.container.EventSource;
import io.github.manhnt217.task.core.container.exception.ContainerException;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.task.TaskException;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.io.Receiver;
import io.undertow.server.BlockingHttpExchange;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;

/**
 * @author manhnguyen
 */
public class HttpEventSource extends EventSource<HttpEventSourceConfig, HttpEventSourceRequest, HttpEventsourceResponse> {

    private Undertow undertow;

    @Override
    protected Class<? extends HttpEventSourceConfig> getPropsType() {
        return HttpEventSourceConfig.class;
    }

    @Override
    public Class<? extends HttpEventSourceRequest> getDispatcherEventType() {
        return HttpEventSourceRequest.class;
    }

    @Override
    public Class<? extends HttpEventsourceResponse> getDispatcherReturnType() {
        return HttpEventsourceResponse.class;
    }

    @Override
    protected void startInternal(HttpEventSourceConfig props) throws Exception {
        Undertow.Builder builder = Undertow.builder();
        undertow = builder.addHttpListener(props.getPort(), props.getHost())
                .setHandler(this::handle)
                .build();
        undertow.start();
    }

    private void handle(HttpServerExchange exchange) throws TaskException, ContainerException, ActivityException, IOException {
        Map<String, Deque<String>> pathParameters = exchange.getPathParameters();
        Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();

        exchange.getRequestReceiver().receiveFullString((ex, message) -> {
            HttpEventSourceRequest event = new HttpEventSourceRequest();
            event.setBody(message);
            event.setPathParams(pathParameters);
            event.setQueryParams(queryParameters);
            HttpEventsourceResponse res;
            try {
                res = HttpEventSource.this.dispatch(event);
            } catch (ContainerException e) {
                throw new RuntimeException(e);
            } catch (TaskException e) {
                throw new RuntimeException(e);
            } catch (ActivityException e) {
                throw new RuntimeException(e);
            }
            ex.getResponseSender().send(res.getBody());
            ex.getResponseHeaders().put(Headers.CONTENT_TYPE, res.getContentType());
            ex.endExchange();
        });

    }

    @Override
    protected void shutdownInternal() throws Exception {
        undertow.stop();
    }
}
