package io.github.manhnt217.task.persistence.service;

import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.activity.loop.ForEachActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.persistence.builder.ForEachActivityBuilder;
import io.github.manhnt217.task.persistence.builder.GroupActivityBuilder;
import io.github.manhnt217.task.persistence.builder.LinkedActivityGroupBuilder;
import io.github.manhnt217.task.persistence.model.ActivityDto;
import io.github.manhnt217.task.persistence.model.ActivityGroupDto;
import io.github.manhnt217.task.persistence.model.ActivityLinkDto;

import java.util.HashMap;
import java.util.Map;

import static io.github.manhnt217.task.core.task.CompositeTask.*;

/**
 * @author manhnguyen
 */
public class ActivityService {

    private static final ActivityService INSTANCE = new ActivityService();

    private ActivityService() {

    }

    public static ActivityService instance() {
        return INSTANCE;
    }

    public Activity buildActivity(ActivityDto activityDto) throws ConfigurationException {
        switch (activityDto.getType()) {
            case FOREACH:
                return forEachActivity(activityDto);
            case GROUP:
                return buildGroupActivity(activityDto);
            case TASK:
                return buildTaskActivity(activityDto);
            default:
                throw new IllegalArgumentException("Invalid activity type: " + activityDto.getType());
        }
    }

    private Activity buildTaskActivity(ActivityDto activityDto) {
        return ActivityBuilder
                .task(activityDto.getName())
                .taskName(activityDto.getTask())
                .inputMapping(activityDto.getInputMapping())
                .build();
    }

    private ForEachActivity forEachActivity(ActivityDto activityDto) throws ConfigurationException {
        ForEachActivityBuilder forEachActivityBuilder = ActivityBuilder
                .forEach()
                .name(activityDto.getName())
                .inputMapping(activityDto.getInputMapping())
                .outputMapping(activityDto.getOutputMapping())
                .start(activityDto.getStartName())
                .end(activityDto.getEndName());
        buildGroup(forEachActivityBuilder, activityDto.getGroup());
        return forEachActivityBuilder.build();
    }

    private GroupActivity buildGroupActivity(ActivityDto activityDto) throws ConfigurationException {
        GroupActivityBuilder groupActivityBuilder = ActivityBuilder
                .group()
                .name(activityDto.getName())
                .inputMapping(activityDto.getInputMapping())
                .outputMapping(activityDto.getOutputMapping())
                .start(activityDto.getStartName())
                .end(activityDto.getEndName());
        buildGroup(groupActivityBuilder, activityDto.getGroup());
        return groupActivityBuilder.build();
    }

    public void buildGroup(LinkedActivityGroupBuilder compositeTaskBuilder, ActivityGroupDto groupDto) throws ConfigurationException {

        Map<String, Activity> activityMap = new HashMap<>();
        for (ActivityDto activityDto : groupDto.getActivities()) {
            if (activityMap.put(activityDto.getName(), buildActivity(activityDto)) != null) {
                throw new IllegalStateException("Duplicated activity. Name = '" + activityDto.getName() + "'");
            }
        }
        for (ActivityLinkDto link : groupDto.getLinks()) {
            link(compositeTaskBuilder, link, activityMap);
        }
    }

    private void link(LinkedActivityGroupBuilder groupBuilder, ActivityLinkDto link, Map<String, Activity> activityMap) {
        String from = link.getFrom();
        String to = link.getTo();
        String guard = link.getGuard();

        if (END_ACTIVITY_NAME.equals(from) || START_ACTIVITY_NAME.equals(to)) {
            throw new IllegalArgumentException("Cannot link from EndActivity or to StartActivity");
        }

        if (START_ACTIVITY_NAME.equals(from) && END_ACTIVITY_NAME.equals(to)) {
            groupBuilder.linkStartToEnd(guard);
        } else if (START_ACTIVITY_NAME.equals(from)) {
            Activity activity = activityMap.get(to);
            if (activity == null) {
                throw new IllegalArgumentException("Activity '" + to + "' is not found");
            }
            groupBuilder.linkFromStart(activity, guard);
        } else if (END_ACTIVITY_NAME.equals(to)) {
            Activity activity = activityMap.get(from);
            if (activity == null) {
                throw new IllegalArgumentException("Activity '" + to + "' is not found");
            }
            groupBuilder.linkToEnd(activity, guard);
        } else {
            Activity fromActivity = activityMap.get(from);
            if (fromActivity == null) {
                throw new IllegalArgumentException("Activity '" + to + "' is not found");
            }
            Activity toActivity = activityMap.get(to);
            if (toActivity == null) {
                throw new IllegalArgumentException("Activity '" + to + "' is not found");
            }
            groupBuilder.link(fromActivity, toActivity, guard);
        }
    }
}
