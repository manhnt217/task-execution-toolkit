package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.future.FutureActivity;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;

/**
 * @author manh nguyen
 */
public class FutureActivityBuilder extends ContainerActivityBuilder<FutureActivity, FutureActivityBuilder> {

    FutureActivityBuilder(String name) {
        super(name);
    }

    @Override
    public FutureActivity build() throws ConfigurationException {
        validate();
        Group group = this.groupBuilder.buildGroup();
        FutureActivity forEachActivity = new FutureActivity(this.name, group);
        forEachActivity.setInputMapping(inputMapping);
        return forEachActivity;
    }
}
