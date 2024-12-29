package io.github.manhnt217.task.core.activity.group;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.*;
import io.github.manhnt217.task.core.activity.group.exception.ActivityTransitionException;
import io.github.manhnt217.task.core.activity.simple.EndActivity;
import io.github.manhnt217.task.core.activity.simple.StartActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.ActivityInputException;
import io.github.manhnt217.task.core.exception.ActivityOutputException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.exception.inner.ContextException;
import io.github.manhnt217.task.core.exception.inner.TransformException;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.*;
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
 * @author manh nguyen
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
    @Getter
    protected StartActivity startActivity;
    protected EndActivity endActivity;
    protected Map<String, Activity> activities;

    public Group() {
        this.activities = new HashMap<>();
        this.links = new HashMap<>();
    }

    private static OutboundMessage executeActivity(ActivityContext context, Activity currentActivity) throws ActivityException {
        InboundMessage inboundMessage;
        JsonNode taskInput;
        if (currentActivity instanceof ExecutionAwareActivity) {
            ((ExecutionAwareActivity) currentActivity).withExecutionContext(context);
        }
        ActivityInfo activityInfo = ActivityInfo.from(currentActivity);
        activityInfo.setStartTime(OffsetDateTime.now());
        try {
            taskInput = context.transformInput(currentActivity);
        } catch (TransformException e) {
            throw new ActivityInputException(context.getCurrentTaskName(), currentActivity.getName(), e);
        }
        inboundMessage = SimpleInboundMessage.of(taskInput);
        OutboundMessage out = currentActivity.process(inboundMessage, context);
        activityInfo.setEndTime(OffsetDateTime.now());
        try {
            context.saveOutput(activityInfo, out);
        } catch (ContextException e) {
            throw new ActivityOutputException(context.getCurrentTaskName(), currentActivity.getName(), e);
        }
        return out;
    }

    /**
     * {@link #BLANK_GUARD_EXP} should always be checked first
     * {@link #OTHERWISE_GUARD_EXP} should always be checked last
     */
    private static int compareGuard(String g1, String g2) {
        if (g1.equals(BLANK_GUARD_EXP)) return -1;
        if (g2.equals(BLANK_GUARD_EXP)) return 1;
        if (g1.equals(OTHERWISE_GUARD_EXP)) return 1;
        if (g2.equals(OTHERWISE_GUARD_EXP)) return -1;
        else return 0;
    }

    private void validateBeforeAdding(Activity activity) throws ConfigurationException {
        if (activity instanceof StartActivity && startActivity != null) {
            throw new ConfigurationException("StartActivity has already been added. Allow only one StartActivity");
        }
        if (activity instanceof EndActivity && endActivity != null) {
            throw new ConfigurationException("EndActivity has already been added. Allow only one EndActivity");
        }
        @SuppressWarnings("unchecked")
        Collection<String> commonNames = CollectionUtils.intersection(this.getContainedActivityNames(), activity.getContainedActivityNames());
        if (!commonNames.isEmpty()) {
            throw new ConfigurationException("There are some names appearing in both current group and the incoming activity." +
                    " Names = [" + String.join(",", commonNames) + "]");
        }
    }

    @Override
    public Set<String> getContainedActivityNames() {
        HashSet<String> activityNames = new HashSet<>();
        for (Activity childActivity : activities.values()) {
            activityNames.addAll(childActivity.getContainedActivityNames());
        }
        return activityNames;
    }

    @Override
    public void addActivity(Activity activity) throws ConfigurationException {
        validateBeforeAdding(activity);
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
    public JsonNode execute(JsonNode input, ActivityContext context) throws ActivityException {
        startActivity.setOutput(input);
        Activity currentActivity = startActivity;

        while (true) {
            OutboundMessage out = executeActivity(context, currentActivity);
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
        if (!this.activities.containsKey(from.getName())) {
            addActivity(from);
        }
        if (!this.activities.containsKey(to.getName())) {
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

    protected final Activity getNextActivity(Activity activity, ActivityContext context) throws ActivityException {
        if (activity == endActivity) {
            return null;
        }
        Map<String, Activity> guardToActivityMap = getConnectedLinks(activity);
        if (guardToActivityMap == null) {
            throw new ActivityTransitionException(context.getCurrentTaskName(), activity.getName(), "Activity '" + activity.getName() + "' link to no where. Process stops");
        }
        List<String> guards = guardToActivityMap.keySet().stream()
                .sorted(Group::compareGuard).collect(Collectors.toList());
        for (String guard : guards) {
            try {
                if (isTrueGuard(context, guard)) {
                    return guardToActivityMap.get(guard);
                }
            } catch (TransformException e) {
                throw new ActivityTransitionException(context.getCurrentTaskName(), activity.getName(), "Cannot evaluate guard '" + guard + "' due to an exception was thrown", e);
            }
        }
        throw new ActivityTransitionException(context.getCurrentTaskName(),
                activity.getName(), "Cannot find the next activity because all links from current activity '" + activity.getName() + "' evaluate to FALSE");
    }

    protected final Map<String, Activity> getConnectedLinks(Activity fromActivity) {
        return links.get(fromActivity);
    }

    private boolean isTrueGuard(ActivityContext context, String guard) throws TransformException {
        if (BLANK_GUARD_EXP.equals(guard) || OTHERWISE_GUARD_EXP.equals(guard)) {
            return true;
        }
        return context.evaluate(guard);
    }
}
