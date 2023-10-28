package io.github.manhnt217.task.task_engine.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author manhnguyen
 */
@RequiredArgsConstructor
@Getter
public final class Mapping<T extends EngineType> implements EngineType {
    private final  Map<String, T> value;
}
