package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.Activity;

/**
 * @author manh nguyen
 */
public abstract class ContainerActivityBuilder<A extends Activity, B extends ContainerActivityBuilder<A, B>> extends AbstractActivityBuilder<A, B> implements LinkedActivityGroupBuilder<B> {

    protected GroupBuilder groupBuilder;

    ContainerActivityBuilder(String name) {
        super(name);
        groupBuilder = new GroupBuilder();
    }

    public B start(String name) {
        this.getGroupBuilder().start(name);
        return (B) this;
    }

    public B end(String name) {
        this.getGroupBuilder().end(name);
        return (B) this;
    }

    @Override
    public GroupBuilder getGroupBuilder() {
        return groupBuilder;
    }

    @Override
    protected void validate() {
        super.validate();
        this.getGroupBuilder().validate();
    }
}
