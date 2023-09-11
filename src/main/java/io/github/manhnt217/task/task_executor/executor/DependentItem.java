package io.github.manhnt217.task.task_executor.executor;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = {"name"})
@RequiredArgsConstructor
public class DependentItem {
    private final String name;
    private final Set<String> dependencies;

    @Getter(AccessLevel.NONE)
    Set<String> dependants = new HashSet<>();

    public void addDependant(DependentItem item) {
        dependants.add(item.getName());
    }
}
