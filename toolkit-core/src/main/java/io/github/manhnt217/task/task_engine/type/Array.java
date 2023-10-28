package io.github.manhnt217.task.task_engine.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author manhnguyen
 */
@RequiredArgsConstructor
@Getter
public final class Array<T extends EngineType> implements EngineType {
    private final List<T> value;
}
