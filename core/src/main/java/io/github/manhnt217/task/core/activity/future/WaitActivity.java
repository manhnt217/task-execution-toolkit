package io.github.manhnt217.task.core.activity.future;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.activity.future.exception.WaitActivityException;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.activity.future.exception.WaitCancelledException;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.activity.future.exception.WaitRuntimeException;
import io.github.manhnt217.task.core.activity.future.exception.WaitTimeoutException;
import io.github.manhnt217.task.core.exception.CancelException;
import io.github.manhnt217.task.core.exception.TimeoutException;
import io.github.manhnt217.task.core.type.Future;
import io.github.manhnt217.task.core.type.ObjectRef;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
// TODO: Implement WaitMultipleActivity so that we can wait on multiple future at once.
//  A properties that it might have: atLeast (number) tell the activity to stop waiting when at least a number of future has been completed
public class WaitActivity extends AbstractActivity {
    public WaitActivity(String name) {
        super(name);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws WaitActivityException {
        Input input;
        Future<JsonNode> future;
        Long timeout;
        boolean silentTimeout;
        boolean silentCancel;
        try {
            input = JSONUtil.treeToValue(in.getContent(), Input.class, context);
            future = input.getFuture().get();
            timeout = input.getTimeout();
            silentTimeout = input.isSilentTimeout();
            silentCancel = input.isSilentCancel();
        } catch (JsonProcessingException e) {
            throw new WaitActivityException(context.getCurrentTaskName(), this.getName(), "Cannot deserialize input", e);
        } catch (ClassCastException e) {
            throw new WaitActivityException(context.getCurrentTaskName(), this.getName(), "Given input is not an instance of " + Future.class.getName());
        }

        try {
            JsonNode result = wait(future, timeout);
            return SimpleOutboundMessage.of(result);
        } catch (TimeoutException e) {
            if (silentTimeout) {
                return SimpleOutboundMessage.of(NullNode.getInstance());
            } else {
                throw new WaitTimeoutException(this, context.getCurrentTaskName());
            }
        } catch (CancelException e) {
            if (silentCancel) {
                return SimpleOutboundMessage.of(NullNode.getInstance());
            } else {
                throw new WaitCancelledException(this, context.getCurrentTaskName());
            }
        } catch (Exception e) {
            throw new WaitRuntimeException(this, context.getCurrentTaskName(), e);
        }
    }

    private JsonNode wait(Future<JsonNode> future, Long timeout) throws TimeoutException, CancelException {
        if (timeout == null) {
            return future.get();
        } else {
            return future.get(timeout);
        }
    }

    @Getter
    @Setter
    public static class Input {
        private ObjectRef<Future<JsonNode>> future;
        private Long timeout;
        private boolean silentTimeout;
        private boolean silentCancel;
    }
}
