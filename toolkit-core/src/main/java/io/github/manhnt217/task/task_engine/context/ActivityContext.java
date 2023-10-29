package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.exception.inner.ContextException;
import io.github.manhnt217.task.task_engine.exception.inner.TransformException;
import io.github.manhnt217.task.task_engine.type.EngineType;
import io.github.manhnt217.task.task_engine.type.simple.ObjectRef;

import java.util.Map;

/**
 * @author manhnguyen
 */
public interface ActivityContext {

    String KEY_PROPS = "_PROPS_";
    String ALL_SUBTASKS_JSLT = "{\""+KEY_PROPS+"\": null, * : . }";
    String FROM_PROPS = "." + KEY_PROPS;

    <I extends EngineType, O extends EngineType> void saveOutput(Activity<I, O> activity, O output) throws ContextException;

    Map<String, JsonNode> toMap();

    <I extends EngineType, O extends EngineType> I transformInput(Activity<I, O> activity) throws TransformException;

    boolean evaluate(String jslt) throws TransformException;

    String getExecutionId();

    String createRef(Object object);

    ObjectRef resolveRef(String refId) throws ContextException;

    void clearRef(String refId);
}
