package io.github.manhnt217.task.task_engine.activity.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.*;
import io.github.manhnt217.task.task_engine.activity.impl.simple.EndActivity;
import io.github.manhnt217.task.task_engine.activity.impl.simple.StartActivity;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.exception.inner.ContextException;
import io.github.manhnt217.task.task_engine.exception.inner.TransformException;
import io.github.manhnt217.task.task_engine.type.EngineType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Activity group based on links. It uses links to connect activities.<br>
 * It must contain only one {@link StartActivity} and only one {@link EndActivity}<br>
 * Execution will begin with <code>StartActivity</code> and will end when it reaches its <code>EndActivity</code><br>
 * <p>
 * The input of the activity group will be the output of the StartActivity <br>
 * The input of the EndActivity be the output of the activity group <br>
 *
 * @author manhnguyen
 */
public abstract class LinkBasedActivityGroup implements ActivityGroup<JsonNode, EngineType> {

    public static final String BLANK_GUARD_EXP = "<blank>";
    public static final String OTHERWISE_GUARD_EXP = "<otherwise>";
    /**
     * <table>
     *     <tr>
     *         <td>from1</td>
     *         <td>
     *             <ul>
     *                  <li>guardExp1 -> to1</li>
     *                  <li>guardExp2 -> to2</li>
     *              </ul>
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>from2</td>
     *         <td>
     *             <ul>
     *                  <li>guardExp3 -> to3</li>
     *              </ul>
     *         </td>
     *     </tr>
     * </table>
     */
    protected final Map<Activity, Map<String, Activity>> links;
    protected StartActivity startActivity;
    protected EndActivity endActivity;
    protected List<Activity<EngineType, EngineType>> activities;

    public LinkBasedActivityGroup(String startActivityName, String endActivityName, String outputMapping) throws ConfigurationException {
        this.activities = new ArrayList<>();
        this.links = new HashMap<>();

        startActivity = new StartActivity(startActivityName);
        endActivity = new EndActivity(endActivityName);
        endActivity.setInputMapping(outputMapping);

        this.addActivity(startActivity);
        this.addActivity(endActivity);
    }

    /**
     * {@link #BLANK_GUARD_EXP} should always be checked first
     * {@link #OTHERWISE_GUARD_EXP} should always be checked last
     *
     * @return
     */
    protected final static int compareGuard(String g1, String g2) {
        if (g1.equals(BLANK_GUARD_EXP)) return -1;
        if (g2.equals(BLANK_GUARD_EXP)) return 1;
        if (g1.equals(OTHERWISE_GUARD_EXP)) return 1;
        if (g2.equals(OTHERWISE_GUARD_EXP)) return -1;
        else return -1;
    }

    private static <O extends EngineType> void saveOutput(ActivityContext context, Activity currentActivity, O out) throws GroupException {
        try {
            context.saveOutput(currentActivity, out);
        } catch (ContextException e) {
            throw new GroupException("Error while saving output for activity '" + currentActivity.getName() + "'", e);
        }
    }

    @Override
    public void addActivity(Activity activity) throws ConfigurationException {
        if (activity.getParent() != null) {
            throw new ConfigurationException("Activity '" + activity.getName() + "' has already belonged to another group. Please remove first");
        }
        activities.add(activity);
        activity.setParent(this);
    }

    @Override
    public void removeActivity(Activity activity) throws ConfigurationException {
        boolean removed = activities.remove(activity);
        if (removed) {
            activity.setParent(null);
        } else {
            throw new ConfigurationException("Activity '" + activity.getName() + "' does not belong to this group");
        }
    }

    @Override
    public EngineType execute(JsonNode input, ActivityLogger activityLogger, ActivityContext context) throws GroupException, ActivityException {
        startActivity.setOutput(input);
        Activity currentActivity = startActivity;

        while (true) {
            EngineType out = executeActivity(currentActivity, context, activityLogger);
            saveOutput(context, currentActivity, out);
            Activity nextActivity = getNextActivity(currentActivity, context);
            if (nextActivity == null) {
                // end the process execution
                return out;
            }
            currentActivity = nextActivity;
        }
    }

    public void linkActivities(Activity from, Activity to, String guardExp) throws ConfigurationException {
        if (!activities.contains(from)) {
            addActivity(from);
        }
        if (!activities.contains(to)) {
            addActivity(to);
        }
        String guard = StringUtils.defaultIfBlank(guardExp, BLANK_GUARD_EXP);
        Map<String, Activity> guardToActivityMap = links.computeIfAbsent(from, a -> new HashMap<>());
        if (guardToActivityMap.containsKey(guardExp)) {
            throw new ConfigurationException("Guard '" + guard + "' already been added for activity '" + from.getName() + "'");
        }
        guardToActivityMap.put(guard, to);
    }

    public final void linkFromStart(Activity activity, String guard) throws ConfigurationException {
        linkActivities(startActivity, activity, guard);
    }

    public final void linkToEnd(Activity activity) throws ConfigurationException {
        linkActivities(activity, endActivity, null);
    }

    protected final <I extends EngineType, O extends EngineType> EngineType executeActivity(Activity<I, O> activity, ActivityContext context, ActivityLogger activityLogger) throws ActivityException {
        I taskInput;
        try {
            taskInput = context.transformInput(activity);
        } catch (TransformException e) {
            throw new ActivityException(activity, "Cannot transform the input for activity", e);
        }
        return activity.process(taskInput, activityLogger, context);
    }

    protected final Activity getNextActivity(Activity activity, ActivityContext context) throws GroupException {
        if (activity == endActivity) {
            return null;
        }
        Map<String, Activity> guardToActivityMap = getConnectedLinks(activity);
        if (guardToActivityMap == null) {
            throw new GroupException("Activity '" + activity.getName() + "' link to no where. Process stops");
        }
        List<String> guards = guardToActivityMap.keySet().stream()
                .sorted(LinkBasedActivityGroup::compareGuard).collect(Collectors.toList());
        for (String guard : guards) {
            try {
                if (isTrueGuard(context, guard)) {
                    return guardToActivityMap.get(guard);
                }
            } catch (TransformException e) {
                throw new GroupException("Cannot evaluate guard due to an exception was thrown", e);
            }
        }
        throw new GroupException("Cannot find the next activity because all links from current activity (name = '" + activity.getName() + "') evaluate to FALSE");
    }

    protected final Map<String, Activity> getConnectedLinks(Activity fromActivity) {
        return links.get(fromActivity);
    }

    protected final boolean isTrueGuard(ActivityContext context, String guard) throws TransformException {
        if (BLANK_GUARD_EXP.equals(guard) || OTHERWISE_GUARD_EXP.equals(guard)) {
            return true;
        }
        return context.evaluate(guard);
    }
}
