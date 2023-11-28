package io.github.manhnt217.task.sample.test.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.source.FromSourceActivity;
import io.github.manhnt217.task.core.container.TaskContainer;
import io.github.manhnt217.task.core.event.source.EventSource;
import io.github.manhnt217.task.core.exception.MultipleHandlersException;
import io.github.manhnt217.task.core.exception.NoHandlerException;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.event.EventSourceConfig;
import io.github.manhnt217.task.core.task.handler.Handler;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventHandlerTest {

    @Captor
    private ArgumentCaptor<EventSource> eventSourceCaptor;

    @Captor
    private ArgumentCaptor<JsonNode> inputCaptor;

    @Test
    public void testSimpleHandler(@Mock EngineRepository repo) throws MultipleHandlersException, TaskException, NoHandlerException, ConfigurationException {
        String sourceName = "simpleEventSource";

        ObjectNode props = TestUtil.OM.createObjectNode();
        TaskContainer taskContainer = spy(new TaskContainer(props, repo));
        EventSourceConfig esc = spy(new EventSourceConfig());
        esc.setName(sourceName);
        esc.setAutoStart(true);
        esc.setAsync(false);
        esc.setPropsJSLT(null);
        esc.setPluginClassName(SimpleEventSource.class.getName());
        esc.loadClass();

        FromSourceActivity fromSourceActivity = ActivityBuilder.fromSource("from" + sourceName, sourceName).build();
        Handler handler = spy(ActivityBuilder
                .handler("h1")
                .from(fromSourceActivity)
                .linkToEnd(fromSourceActivity)
                .build());

        when(repo.findAllEventSources()).thenReturn(Collections.singletonList(esc));
        when(repo.findHandlerBySourceName(sourceName)).thenReturn(Collections.singletonList(handler));

        taskContainer.start();

        verify(taskContainer).dispatch(eventSourceCaptor.capture(), eq("Hello world"), eq(String.class));
        verify(handler).handle(inputCaptor.capture(), any());

        EventSource createdES = eventSourceCaptor.getValue();
        assertThat(createdES.getName(), is(sourceName));
        assertThat(createdES.isAsync(), is(false));
        assertThat(createdES.getDispatcherReturnType(), is((Object)String.class));
        assertThat(inputCaptor.getValue().textValue(), is("Hello world"));

        taskContainer.shutdown();
    }

    public static class SimpleEventSource extends EventSource<Object, String> {
        @Override
        protected Class<?> getPropsType() {
            return Object.class;
        }

        @Override
        public Class<? extends String> getDispatcherReturnType() {
            return String.class;
        }

        @Override
        protected void startInternal(Object props) throws Exception {
            dispatch("Hello world");
        }

        @Override
        protected void shutdownInternal() throws Exception {
            System.out.println("Shutdown");
        }
    }

}
