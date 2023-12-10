package io.github.manhnt217.task.core.activity.future;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.TimeOutException;
import io.github.manhnt217.task.core.type.Future;
import io.github.manhnt217.task.core.type.ObjectRef;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
public class WaitActivity extends AbstractActivity {
    public WaitActivity(String name) {
        super(name);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        Input input;
        Future<JsonNode> future;
        Long timeout;
        boolean silentTimeout;
        try {
            input = JSONUtil.treeToValue(in.getContent(), Input.class, context);
            future = input.getFuture().get();
            timeout = input.getTimeout();
            silentTimeout = input.isSilentTimeout();
        } catch (JsonProcessingException e) {
            throw new ActivityException(this, "Cannot deserialize input", e);
        } catch (ClassCastException e) {
            throw new ActivityException(this, "Given input is not an instance of " + Future.class.getName(), e);
        }

        try {
            JsonNode result = wait(future, timeout, silentTimeout);
            return SimpleOutboundMessage.of(result);
        } catch (Exception e) {
            throw new ActivityException(this, "Exception while waiting for the result", e);
        }
    }

    private JsonNode wait(Future<JsonNode> future, Long timeout, boolean silentTimeout) throws TimeOutException {
        try {
            if (timeout == null) {
                return future.get();
            } else {
                return future.get(timeout);
            }
        } catch (TimeOutException e) {
            if (silentTimeout) {
                return NullNode.getInstance();
            } else {
                throw e;
            }
        }
    }

    @Getter @Setter
    public static class Input {
        private ObjectRef<Future<JsonNode>> future;
        private Long timeout;
        private boolean silentTimeout;
    }
}
