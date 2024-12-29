package io.github.manhnt217.task.core.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.container.FutureProcessor;
import io.github.manhnt217.task.core.exception.inner.ContextException;
import io.github.manhnt217.task.core.exception.inner.TransformException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.type.ObjectRef;

import java.util.Iterator;
import java.util.Map;

/**
 * @author manh nguyen
 */
public interface ActivityContext {

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

    void saveOutput(Activity activity, OutboundMessage output) throws ContextException;

    Iterator<Map.Entry<String, JsonNode>> iterator();

    //<editor-fold desc="Transformer">
    JsonNode transformInput(Activity activity) throws TransformException;

    boolean evaluate(String jslt) throws TransformException;
    //</editor-fold>

    String getExecutionId();

    //<editor-fold desc="Repository">
    EngineRepository getRepo();
    //</editor-fold>

    //<editor-fold desc="RefManager">
    String createRef(Object object);

    ObjectRef resolveRef(String refId) throws ContextException;

    void clearRef(String refId);
    //</editor-fold>

    TaskLogger getLogger();

    //<editor-fold desc="StackManager">
    Callstack getCallStack();

    default String getCurrentTaskName() {
        return getCallStack().getTop();
    }
    //</editor-fold>

    FutureProcessor getFutureProcessor();
}
