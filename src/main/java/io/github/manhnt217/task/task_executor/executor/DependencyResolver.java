package io.github.manhnt217.task.task_executor.executor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DependencyResolver {

    private final Map<String, DependentItem> itemMap;

    public DependencyResolver(Set<DependentItem> items) {
        this.itemMap = items.stream().collect(Collectors.toMap(DependentItem::getName, Function.identity()));
    }

    private void buildDependencyGraph() throws DependencyNotFoundException {
        for (DependentItem item : itemMap.values()) {
            Set<String> dependencies = item.getDependencies();
            for (String dependency : dependencies) {
                DependentItem dependentItem = itemMap.get(dependency);
                if (dependentItem == null) {
                    throw new DependencyNotFoundException(dependency);
                }
                dependentItem.addDependant(item);
            }
        }
    }

    // TODO: Very dummy implementation

    /**
     * Retrieve and remove the next item without any dependency left
     *
     * @return next item without no dependency remaining
     */
    public DependentItem next() throws UnresolvableDependencyException {
        if (itemMap.isEmpty()) {
            return null;
        }
        for (DependentItem item : itemMap.values()) {
            if (item.getDependencies().isEmpty()) {
                itemMap.remove(item.getName());
                for (DependentItem remainItem : itemMap.values()) {
                    remainItem.getDependencies().remove(item.getName());
                }
                return item;
            }
        }
        throw new UnresolvableDependencyException(itemMap.keySet());
    }
}
