package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.trial.TrialActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;

/**
 * @author manh nguyen
 */
public class TrialActivityBuilder extends ContainerActivityBuilder<TrialActivity, TrialActivityBuilder> {

    private Class<? extends Throwable> ex;
    private boolean catchRootCause;

    TrialActivityBuilder(String name, Class<? extends Throwable> ex, boolean catchRootCause) {
        super(name);
        this.ex = ex;
        this.catchRootCause = catchRootCause;
    }

    @Override
    public TrialActivity build() throws ConfigurationException {
        validate();
        Group group = this.groupBuilder.buildGroup();
        TrialActivity trialActivity = new TrialActivity(name, group, ex, catchRootCause);
        trialActivity.setInputMapping(inputMapping);
        return trialActivity;
    }
}
