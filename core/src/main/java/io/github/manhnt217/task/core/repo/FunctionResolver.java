package io.github.manhnt217.task.core.repo;

import io.github.manhnt217.task.core.task.function.Function;

/**
 * @author manh nguyen
 */
public interface FunctionResolver {
    Function getFunction(String name);
}
