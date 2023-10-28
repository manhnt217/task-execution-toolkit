package io.github.manhnt217.task.task_engine.type.simple;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author manhnguyen
 */
@RequiredArgsConstructor
@Getter
public final class Int implements EngineSimpleType {
    private int value;
}
