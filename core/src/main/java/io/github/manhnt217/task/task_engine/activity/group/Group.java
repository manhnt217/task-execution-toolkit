package io.github.manhnt217.task.task_engine.activity.group;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.activity.InboundMessage;
import io.github.manhnt217.task.task_engine.activity.OutboundMessage;
import io.github.manhnt217.task.task_engine.activity.SimpleInboundMessage;
import io.github.manhnt217.task.task_engine.activity.simple.EndActivity;
import io.github.manhnt217.task.task_engine.activity.simple.StartActivity;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.exception.inner.ContextException;
import io.github.manhnt217.task.task_engine.exception.inner.TransformException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class Group implements LinkedActivityGroup<JsonNode, JsonNode> {

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
    protected Map<String, Activity> activities;

    public Group() {
        this.activities = new HashMap<>();
        this.links = new HashMap<>();
    }

    private static void saveOutput(ActivityContext context, Activity currentActivity, OutboundMessage out) throws GroupException {
        try {
            context.saveOutput(currentActivity, out);
        } catch (ContextException e) {
            throw new GroupException("Error while saving output for activity '" + currentActivity.getName() + "'", e);
        }
    }

    @Override
    public boolean containsActivity(Activity activity) {
        if (activity instanceof StartActivity) {
            return this.activities.values().stream().anyMatch(a -> a instanceof StartActivity);
        }
        if (activity instanceof EndActivity) {
            return this.activities.values().stream().anyMatch(a -> a instanceof EndActivity);
        }
        return this.activities.containsKey(activity.getName());
    }

    @Override
    public void addActivity(Activity activity) throws ConfigurationException {
        if (containsActivity(activity)) {
            if (activity instanceof StartActivity) {
                throw new ConfigurationException("StartActivity has already been added. Allow only one StartActivity");
            }
            if (activity instanceof EndActivity) {
                throw new ConfigurationException("EndActivity has already been added. Allow only one EndActivity");
            }
            throw new ConfigurationException("Activity '" + activity.getName() + "' has already been added");
        }
        if (activity instanceof StartActivity) {
            this.startActivity = (StartActivity) activity;
        } else if (activity instanceof EndActivity) {
            this.endActivity = (EndActivity) activity;
        }
        if (activity.getParent() != null) {
            throw new ConfigurationException("Activity '" + activity.getName() + "' has already belonged to another group. Please remove first");
        }
        activities.put(activity.getName(), activity);
        activity.setParent(this);
    }

    @Override
    public void removeActivity(Activity activity) throws ConfigurationException {
        Activity removed = activities.remove(activity.getName());
        if (removed != null) {
            removed.setParent(null);
        } else {
            throw new ConfigurationException("Activity '" + activity.getName() + "' does not belong to this group");
        }
    }

    @Override
    public JsonNode execute(JsonNode input, ActivityLogger activityLogger, ActivityContext context) throws GroupException, ActivityException {
        startActivity.setOutput(input);
        Activity currentActivity = startActivity;

        while (true) {
            OutboundMessage out = executeActivity(currentActivity, context, activityLogger);
            saveOutput(context, currentActivity, out);
            Activity nextActivity = getNextActivity(currentActivity, context);
            if (nextActivity == null) {
                // end the process execution
                return out.getContent();
            }
            currentActivity = nextActivity;
        }
    }

    @Override
    public void linkActivities(Activity from, Activity to, String guardExp) throws ConfigurationException {
        if (!containsActivity(from)) {
            addActivity(from);
        }
        if (!containsActivity(to)) {
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

    public final void linkToEnd(Activity activity, String guard) throws ConfigurationException {
        linkActivities(activity, endActivity, guard);
    }

    public final void linkStartToEnd(String guard) throws ConfigurationException {
        linkActivities(startActivity, endActivity, guard);
    }

    protected final OutboundMessage executeActivity(Activity activity, ActivityContext context, ActivityLogger activityLogger) throws ActivityException {
        InboundMessage inboundMessage;
        JsonNode taskInput;
        try {
            taskInput = context.transformInput(activity);
        } catch (TransformException e) {
            throw new ActivityException(activity, "Cannot transform the input for activity", e);
        }
        inboundMessage = SimpleInboundMessage.of(taskInput);
        return activity.process(inboundMessage, activityLogger, context);
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
                .sorted(Group::compareGuard).collect(Collectors.toList());
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

    /**
     * {@link #BLANK_GUARD_EXP} should always be checked first
     * {@link #OTHERWISE_GUARD_EXP} should always be checked last
     *
     * @return
     */
    private static int compareGuard(String g1, String g2) {
        if (g1.equals(BLANK_GUARD_EXP)) return -1;
        if (g2.equals(BLANK_GUARD_EXP)) return 1;
        if (g1.equals(OTHERWISE_GUARD_EXP)) return 1;
        if (g2.equals(OTHERWISE_GUARD_EXP)) return -1;
        else return 0;
    }

    private boolean isTrueGuard(ActivityContext context, String guard) throws TransformException {
        if (BLANK_GUARD_EXP.equals(guard) || OTHERWISE_GUARD_EXP.equals(guard)) {
            return true;
        }
        return context.evaluate(guard);
    }
}
