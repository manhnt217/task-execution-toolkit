package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.Activity;

/**
 * @author manhnguyen
 */
public interface LinkedActivityGroupBuilder<B> {
    GroupBuilder getGroupBuilder();

    default B linkFromStart(Activity a, String guard) {
        this.getGroupBuilder().linkFromStart(a, guard);
        return (B) this;
    }

    default B linkFromStart(Activity a) {
        this.getGroupBuilder().linkFromStart(a);
        return (B) this;
    }

    default B linkToEnd(Activity a, String guard) {
        this.getGroupBuilder().linkToEnd(a, guard);
        return (B) this;
    }

    default B linkToEnd(Activity a) {
        this.getGroupBuilder().linkToEnd(a);
        return (B) this;
    }

    default B link(Activity a, Activity b, String guard) {
        this.getGroupBuilder().link(a, b, guard);
        return (B) this;
    }

    default B link(Activity a, Activity b) {
        this.getGroupBuilder().link(a, b);
        return (B) this;
    }

    default B linkStartToEnd(String guard) {
        this.getGroupBuilder().linkStartToEnd(guard);
        return (B) this;
    }

    default B outputMapping(String outputMapping) {
        this.getGroupBuilder().outputMapping(outputMapping);
        return (B) this;
    }
}
