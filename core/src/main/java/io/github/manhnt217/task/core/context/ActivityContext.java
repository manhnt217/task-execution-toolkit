package io.github.manhnt217.task.core.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.ActivityInfo;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.container.FutureProcessor;
import io.github.manhnt217.task.core.context.component.*;
import io.github.manhnt217.task.core.exception.inner.ContextException;

import java.util.Iterator;
import java.util.Map;

/**
 * @author manh nguyen
 */
public interface ActivityContext
        extends ExecutionContext, ActivityTransformer, RepoContext, ObjectRefContext, LogContext {

    String KEY_PROPS = "_PROPS_";
    String ALL_SUBTASKS_JSLT = "{\""+KEY_PROPS+"\": null, * : . }";
    String FROM_PROPS = from(KEY_PROPS);

    static String from(String key) {
        return "." + key;
    }

    static String from(Activity activity) {
        return "." + activity.getName();
    }

    ObjectNode getProps();

    void saveOutput(ActivityInfo activityInfo, OutboundMessage output) throws ContextException;

    Iterator<Map.Entry<String, JsonNode>> iterator();

    FutureProcessor getFutureProcessor();
}
