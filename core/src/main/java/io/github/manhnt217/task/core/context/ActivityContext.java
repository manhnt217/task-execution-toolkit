package io.github.manhnt217.task.core.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.exception.inner.ContextException;
import io.github.manhnt217.task.core.exception.inner.TransformException;
import io.github.manhnt217.task.core.task.Task;

import java.util.Map;

/**
 * @author manhnguyen
 */
public interface ActivityContext {

    String KEY_PROPS = "_PROPS_";
    String ALL_SUBTASKS_JSLT = "{\""+KEY_PROPS+"\": null, * : . }";
    String FROM_PROPS = "." + KEY_PROPS;

    void saveOutput(Activity activity, OutboundMessage output) throws ContextException;

    Map<String, JsonNode> toMap();

    JsonNode transformInput(Activity activity) throws TransformException;

    boolean evaluate(String jslt) throws TransformException;

    String getExecutionId();

    Task resolveTask(String name);

    String createRef(Object object);

    ObjectRef resolveRef(String refId) throws ContextException;

    void clearRef(String refId);
}
