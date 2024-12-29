package io.github.manhnt217.task.core.context.component;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.exception.inner.TransformException;

/**
 * @author manhnguyen
 */
public interface ActivityTransformer {
	JsonNode transformInput(Activity activity) throws TransformException;

	boolean evaluate(String jslt) throws TransformException;

	String getExecutionId();
}
