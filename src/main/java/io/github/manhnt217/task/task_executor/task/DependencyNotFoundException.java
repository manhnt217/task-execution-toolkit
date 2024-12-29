package io.github.manhnt217.task.task_executor.task;

import lombok.Getter;

@Getter
public class DependencyNotFoundException extends Exception {
    private final String name;

    public DependencyNotFoundException(String name) {
        super("Dependency '" + name + "' is not found");
        this.name = name;
    }
}
