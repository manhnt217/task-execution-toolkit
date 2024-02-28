package io.github.manhnt217.task.persistence.service;

import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.future.FutureActivity;
import io.github.manhnt217.task.core.activity.future.WaitActivity;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.activity.loop.ForEachActivity;
import io.github.manhnt217.task.core.activity.simple.JsonParserActivity;
import io.github.manhnt217.task.core.activity.simple.MapperActivity;
import io.github.manhnt217.task.core.activity.source.FromSourceActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.persistence.builder.ForEachActivityBuilder;
import io.github.manhnt217.task.persistence.builder.FutureActivityBuilder;
import io.github.manhnt217.task.persistence.builder.GroupActivityBuilder;
import io.github.manhnt217.task.persistence.builder.LinkedActivityGroupBuilder;
import io.github.manhnt217.task.persistence.model.ActivityGroupDto;
import io.github.manhnt217.task.persistence.model.ActivityLinkDto;
import io.github.manhnt217.task.persistence.model.activity.ActivityDto;
import io.github.manhnt217.task.persistence.model.activity.ForeachActivityDto;
import io.github.manhnt217.task.persistence.model.activity.FunctionActivityDto;
import io.github.manhnt217.task.persistence.model.activity.FutureActivityDto;
import io.github.manhnt217.task.persistence.model.activity.GroupActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.JsonParserActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.MapperActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.PluginActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.SourceActivityDto;
import io.github.manhnt217.task.persistence.model.activity.TrialActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.WaitActivityDto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author manh nguyen
 */
public class ActivityService {

    private static final ActivityService INSTANCE = new ActivityService();

    private ActivityService() {

    }

    public static ActivityService instance() {
        return INSTANCE;
    }

    public Activity buildActivity(ActivityDto activityDto) throws ConfigurationException {
        if (activityDto instanceof ForeachActivityDto) {
            return forEachActivity((ForeachActivityDto) activityDto);
        } else if (activityDto instanceof GroupActivityDto) {
            return buildGroupActivity((GroupActivityDto) activityDto);
        } else if (activityDto instanceof FutureActivityDto) {
            return buildFutureActivity((FutureActivityDto) activityDto);
        } else if (activityDto instanceof PluginActivityDto) {
            return buildPluginActivity((PluginActivityDto) activityDto);
        } else if (activityDto instanceof FunctionActivityDto) {
            return buildFuncCallActivity((FunctionActivityDto) activityDto);
        } else if (activityDto instanceof TrialActivityDto) {
            return buildTrialActivity((TrialActivityDto) activityDto);
        } else if (activityDto instanceof SourceActivityDto) {
            FromSourceActivity activity = new FromSourceActivity(activityDto.getName(), ((SourceActivityDto) activityDto).getSourceName());
            activity.setInputMapping(activityDto.getInputMapping());
            return activity;
        } else if (activityDto instanceof WaitActivityDto) {
            WaitActivity activity = new WaitActivity(activityDto.getName());
            activity.setInputMapping(activityDto.getInputMapping());
            return activity;
        } else if (activityDto instanceof JsonParserActivityDto) {
            JsonParserActivity activity = new JsonParserActivity(activityDto.getName());
            activity.setInputMapping(activityDto.getInputMapping());
            return activity;
        } else if (activityDto instanceof MapperActivityDto) {
            MapperActivity activity = new MapperActivity(activityDto.getName());
            activity.setInputMapping(activityDto.getInputMapping());
            return activity;
        } else {
            throw new IllegalArgumentException("Invalid activity type: " + activityDto.getClass().getName());
        }
    }

    private Activity buildTrialActivity(TrialActivityDto activityDto) throws ConfigurationException {
        try {
            Class<?> clazz = Class.forName(activityDto.getEx());
            if (!Throwable.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Class '" + clazz + "' is not Throwable");
            }
            return ActivityBuilder
                    .trial(activityDto.getName(), (Class<? extends Throwable>) clazz, activityDto.isCatchRootCause())
                    .inputMapping(activityDto.getInputMapping())
                    .build();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Activity buildPluginActivity(PluginActivityDto activityDto) {
        return ActivityBuilder
                .plugin(activityDto.getName(), activityDto.getPluginName())
                .inputMapping(activityDto.getInputMapping())
                .build();
    }

    private Activity buildFuncCallActivity(FunctionActivityDto activityDto) {
        return ActivityBuilder
                .funcCall(activityDto.getName())
                .funcName(activityDto.getFunctionName())
                .inputMapping(activityDto.getInputMapping())
                .build();
    }

    private ForEachActivity forEachActivity(ForeachActivityDto activityDto) throws ConfigurationException {
        ForEachActivityBuilder forEachActivityBuilder = ActivityBuilder
                .forEach(activityDto.getName())
                .inputMapping(activityDto.getInputMapping())
                .outputMapping(activityDto.getOutputMapping())
                .start(activityDto.getStartName())
                .end(activityDto.getEndName());
        buildGroupBuilder(forEachActivityBuilder, activityDto.getGroup());
        return forEachActivityBuilder.build();
    }

    private GroupActivity buildGroupActivity(GroupActivityDto activityDto) throws ConfigurationException {
        GroupActivityBuilder groupActivityBuilder = ActivityBuilder
                .group(activityDto.getName(), activityDto.isSyncGroup())
                .inputMapping(activityDto.getInputMapping())
                .outputMapping(activityDto.getOutputMapping())
                .start(activityDto.getStartName())
                .end(activityDto.getEndName());
        buildGroupBuilder(groupActivityBuilder, activityDto.getGroup());
        return groupActivityBuilder.build();
    }

    private FutureActivity buildFutureActivity(FutureActivityDto activityDto) throws ConfigurationException {
        FutureActivityBuilder builder = ActivityBuilder
                .future(activityDto.getName())
                .inputMapping(activityDto.getInputMapping())
                .outputMapping(activityDto.getOutputMapping())
                .start(activityDto.getStartName())
                .end(activityDto.getEndName());
        buildGroupBuilder(builder, activityDto.getGroup());
        return builder.build();
    }

    void buildGroupBuilder(LinkedActivityGroupBuilder<?> linkedActivityGroupBuilder, ActivityGroupDto groupDto) throws ConfigurationException {

        Map<String, Activity> activityMap = new HashMap<>();
        for (ActivityDto activityDto : groupDto.getActivities()) {
            if (activityMap.put(activityDto.getName(), buildActivity(activityDto)) != null) {
                throw new IllegalStateException("Duplicated activity. Name = '" + activityDto.getName() + "'");
            }
        }
        for (ActivityLinkDto link : groupDto.getLinks()) {
            link(linkedActivityGroupBuilder, link, activityMap);
        }
    }

    private void link(LinkedActivityGroupBuilder groupBuilder, ActivityLinkDto link, Map<String, Activity> activityMap) {
        String from = link.getFrom();
        String to = link.getTo();
        String guard = link.getGuard();

        if (groupBuilder.getEndName().equals(from) || groupBuilder.getStartName().equals(to)) {
            throw new IllegalArgumentException("Cannot link from EndActivity or to StartActivity");
        }

        if (groupBuilder.getStartName().equals(from) && groupBuilder.getEndName().equals(to)) {
            groupBuilder.linkStartToEnd(guard);
        } else if (groupBuilder.getStartName().equals(from)) {
            Activity activity = activityMap.get(to);
            if (activity == null) {
                throw new IllegalArgumentException("Activity '" + to + "' is not found");
            }
            groupBuilder.linkFromStart(activity, guard);
        } else if (groupBuilder.getEndName().equals(to)) {
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
