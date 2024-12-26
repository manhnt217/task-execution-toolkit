package io.github.manhnt217.task.core.context.component;

import io.github.manhnt217.task.core.exception.inner.ContextException;
import io.github.manhnt217.task.core.type.ObjectRef;

/**
 * @author manhnguyen
 */
public interface ObjectRefContext {
	String createRef(Object object);

	ObjectRef resolveRef(String refId) throws ContextException;

	void clearRef(String refId);
}
