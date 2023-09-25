package io.github.manhnt217.task.task_executor.executor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyResolver {

    private final List<DependentItem> items;

    public DependencyResolver(List<DependentItem> items) {
        this.items = new ArrayList<>(items);
    }

    // TODO: Very naive implementation

    /**
     * Retrieve and remove the next item without any dependency left
     *
     * @return next item without no dependency remaining
     */
    public DependentItem next() throws UnresolvableDependencyException {
        if (items.isEmpty()) {
            return null;
        }
        Iterator<DependentItem> iterator = items.iterator();
        for (DependentItem item; (item = iterator.next()) != null; ){
            if (item.getDependencies().isEmpty()) {
                iterator.remove();
                for (DependentItem remainItem : items) {
                    remainItem.getDependencies().remove(item.getName());
                }
                return item;
            }
        }
        throw new UnresolvableDependencyException(items.stream().map(DependentItem::getName).collect(Collectors.toSet()));
    }
}
