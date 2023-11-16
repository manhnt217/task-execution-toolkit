package io.github.manhnt217.task.sample;

import io.github.manhnt217.task.core.task.Task;
import io.github.manhnt217.task.core.task.TaskResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author manhnguyen
 */
public class SimpleTaskResolver implements TaskResolver {
    protected final Map<String, Task> tasks;

    public SimpleTaskResolver() {
        tasks = new HashMap<>();
    }

    @Override
    public Task resolve(String name) {
        return tasks.get(name);
    }

    @Override
    public void register(Task task) {
        tasks.put(task.getName(), task);
    }
}
