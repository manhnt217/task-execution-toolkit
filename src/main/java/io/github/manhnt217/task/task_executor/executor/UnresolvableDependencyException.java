package io.github.manhnt217.task.task_executor.executor;

import lombok.Getter;

import java.util.Set;

@Getter
public class UnresolvableDependencyException extends Exception {

    private final Set<String> itemNames;

    public UnresolvableDependencyException(Set<String> itemNames) {
        super("Dependencies of these items: [" + String.join(", ", itemNames) + "] cannot be resolved");
        this.itemNames = itemNames;
    }
}
