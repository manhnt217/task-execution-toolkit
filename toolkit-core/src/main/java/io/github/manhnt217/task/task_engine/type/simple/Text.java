package io.github.manhnt217.task.task_engine.type.simple;

import io.github.manhnt217.task.task_engine.type.EngineType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author manhnguyen
 */
@RequiredArgsConstructor
@Getter
public final class Text implements EngineSimpleType {
    private final String value;
}
