package io.github.manhnt217.task.core.task;

/**
 * @author manhnguyen
 */
public interface TaskResolver {
    Task resolve(String name);

    void register(Task task);
}
