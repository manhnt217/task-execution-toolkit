package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.activity.Activity;

/**
 * @author manhnguyen
 */
public abstract class ContainerActivityBuilder<A extends Activity, B extends ContainerActivityBuilder<A, B>> extends AbstractActivityBuilder<A, B> {

    protected GroupBuilder groupBuilder;

    ContainerActivityBuilder() {
        groupBuilder = new GroupBuilder();
    }

    public B start(String name) {
        this.groupBuilder.start(name);
        return (B) this;
    }

    public B end(String name) {
        this.groupBuilder.end(name);
        return (B) this;
    }

    public B linkFromStart(Activity a, String guard) {
        this.groupBuilder.linkFromStart(a, guard);
        return (B) this;
    }

    public B linkFromStart(Activity a) {
        this.groupBuilder.linkFromStart(a);
        return (B) this;
    }

    public B linkToEnd(Activity a, String guard) {
        this.groupBuilder.linkToEnd(a, guard);
        return (B) this;
    }

    public B linkToEnd(Activity a) {
        this.groupBuilder.linkToEnd(a);
        return (B) this;
    }

    public B link(Activity a, Activity b, String guard) {
        this.groupBuilder.link(a, b, guard);
        return (B) this;
    }

    public B link(Activity a, Activity b) {
        this.groupBuilder.link(a, b);
        return (B) this;
    }

    public B outputMapping(String outputMapping) {
        this.groupBuilder.outputMapping(outputMapping);
        return (B) this;
    }

    @Override
    protected void validate() {
        super.validate();
        this.groupBuilder.validate();
    }
}
