package io.github.manhnt217.task.task_engine.context.sub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_engine.context.AbstractActivityContext;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.context.ObjectRef;
import io.github.manhnt217.task.task_engine.exception.inner.ContextException;
import io.github.manhnt217.task.task_engine.task.Task;

import java.util.Iterator;
import java.util.Map;

/**
 * @author manhnguyen
 */
public abstract class AbstractSubActivityContext extends AbstractActivityContext {

    protected final ActivityContext parent;

    protected AbstractSubActivityContext(ActivityContext parent, boolean copyTaskOutput) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent context is null");
        }
        this.parent = parent;
        copy(parent.toMap(), contextParams, copyTaskOutput);
    }


    private void copy(Map<String, JsonNode> from, ObjectNode to, boolean copyTaskOutput) {
        if (copyTaskOutput) {
            Iterator<Map.Entry<String, JsonNode>> fields = from.entrySet().iterator();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                to.set(entry.getKey(), entry.getValue());
            }
        } else {
            to.set(KEY_PROPS, from.get(KEY_PROPS));
        }
    }

    @Override
    public Task resolveTask(String name) {
        return parent.resolveTask(name);
    }

    @Override
    public String createRef(Object object) {
        return parent.createRef(object);
    }

    @Override
    public String getExecutionId() {
        return parent.getExecutionId();
    }

    @Override
    public void clearRef(String refId) {
        parent.clearRef(refId);
    }

    @Override
    public ObjectRef resolveRef(String refId) throws ContextException {
        return parent.resolveRef(refId);
    }
}
