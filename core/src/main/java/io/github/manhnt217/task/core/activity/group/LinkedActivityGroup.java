package io.github.manhnt217.task.core.activity.group;

import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;

/**
 * @author manhnguyen
 */
public interface LinkedActivityGroup<P, Q> extends ActivityGroup<P, Q>{

    void linkActivities(Activity from, Activity to, String guardExp) throws ConfigurationException;
}
