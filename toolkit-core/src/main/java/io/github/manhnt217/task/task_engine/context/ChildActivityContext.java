package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

/**
 * @author manhnguyen
 */
public abstract class ChildActivityContext extends BasicActivityContext {
    public ChildActivityContext(ActivityContext context, boolean copyPropsOnly) {
        super(context.getExecutionId());
        copy(context.toMap(), this.contextParams, copyPropsOnly);
    }

    private static void copy(Map<String, JsonNode> from, ObjectNode to, boolean copyPropsOnly) {
        if (copyPropsOnly) {
            to.set(KEY_PROPS, from.get(KEY_PROPS));
        } else {
            Iterator<Map.Entry<String, JsonNode>> fields = from.entrySet().iterator();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                to.set(entry.getKey(), entry.getValue());
            }
        }
    }
}
