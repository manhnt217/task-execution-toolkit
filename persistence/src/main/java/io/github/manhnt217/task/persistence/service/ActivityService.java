package io.github.manhnt217.task.persistence.service;

import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.activity.loop.ForEachActivity;
import io.github.manhnt217.task.core.activity.simple.FromLastActivity;
import io.github.manhnt217.task.core.activity.simple.MapperActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.persistence.builder.ForEachActivityBuilder;
import io.github.manhnt217.task.persistence.builder.GroupActivityBuilder;
import io.github.manhnt217.task.persistence.builder.LinkedActivityGroupBuilder;
import io.github.manhnt217.task.persistence.model.ActivityGroupDto;
import io.github.manhnt217.task.persistence.model.ActivityLinkDto;
import io.github.manhnt217.task.persistence.model.activity.*;

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
        } else if (activityDto instanceof PluginActivityDto) {
            return buildPluginActivity((PluginActivityDto) activityDto);
        } else if (activityDto instanceof FunctionActivityDto) {
            return buildFuncCallActivity((FunctionActivityDto) activityDto);
        } else if (activityDto instanceof SourceActivityDto) {
            return buildSourceActivity((SourceActivityDto) activityDto);
        } else if (activityDto instanceof TrialActivityDto) {
            return buildTrialActivity((TrialActivityDto) activityDto);
        } else if (activityDto instanceof FromLastActivityDto) {
            return buildFromLastActivity((FromLastActivityDto) activityDto);
        } else if (activityDto instanceof MapperActivityDto) {
            return buildMapperActivity((MapperActivityDto) activityDto);
        } else {
            throw new IllegalArgumentException("Invalid activity type: " + activityDto.getClass().getName());
        }
    }

    private Activity buildTrialActivity(TrialActivityDto activityDto) {
        // TODO: implement this method
        return null;
    }

    private Activity buildFromLastActivity(FromLastActivityDto activityDto) {
        return new FromLastActivity(activityDto.getName());
    }

    private Activity buildMapperActivity(MapperActivityDto activityDto) {
        MapperActivity mapperActivity = new MapperActivity(activityDto.getName());
        mapperActivity.setInputMapping(activityDto.getInputMapping());
        return mapperActivity;
    }

    private Activity buildSourceActivity(SourceActivityDto activityDto) {
        return ActivityBuilder
                .fromSource(activityDto.getName(), activityDto.getSourceName())
                .build();
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
