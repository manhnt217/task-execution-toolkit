package io.github.manhnt217.task.task_engine.activity.group;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;

/**
 * @author manhnguyen
 */
public interface LinkedActivityGroup<P, Q> extends ActivityGroup<P, Q>{

    void linkActivities(Activity from, Activity to, String guardExp) throws ConfigurationException;
}
