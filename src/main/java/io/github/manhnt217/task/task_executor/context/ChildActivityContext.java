package io.github.manhnt217.task.task_executor.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

/**
 * @author manhnguyen
 */
public abstract class ChildActivityContext extends ActivityContext {
    public ChildActivityContext(ActivityContext context, boolean copyPropsOnly) {
        super(context.getExecutionId());
        copy(context.contextParams, this.contextParams, copyPropsOnly);
    }

    private static void copy(ObjectNode from, ObjectNode to, boolean copyPropsOnly) {
        if (copyPropsOnly) {
            to.set(KEY_PROPS, from.get(KEY_PROPS));
        } else {
            Iterator<Map.Entry<String, JsonNode>> fields = from.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                to.set(entry.getKey(), entry.getValue());
            }
        }
    }
}
