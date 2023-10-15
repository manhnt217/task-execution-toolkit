package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.*;
import io.github.manhnt217.task.task_executor.activity.impl.SimpleInboundMessage;
import io.github.manhnt217.task.task_executor.activity.impl.SimpleOutboundMessage;
import io.github.manhnt217.task.task_executor.process.Logger;
import io.github.manhnt217.task.task_executor.task.context.ExecContext;
import io.github.manhnt217.task.task_executor.task.context.ParamContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class CompoundTask extends Task implements ContainerActivity {

    private List<Activity> activities;

    public static final String ALL_SUBTASKS_JSLT = "{" +
            "\"" + ParamContext.KEY_PROPS + "\": null," +
            "\"" + StartActivity.NAME + "\": null," +
            "\"" + EndActivity.NAME + "\": null," +
            " * : . " +
            "}";

    private String outputMapping;
    private StartActivity startActivity;
    private EndActivity endActivity;

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
    private Map<Activity, Map<String, Activity>> links;

    public CompoundTask() {
        this("COMPOUNDTASK-" + UUID.randomUUID(), Collections.emptyList());
    }

    public CompoundTask(String name, List<Task> subTasks) {
        super(name);
        this.activities = new ArrayList<>();
        this.links = new HashMap<>();

        startActivity = new StartActivity();
        endActivity = new EndActivity();

        this.addActivity(startActivity);
        this.addActivity(endActivity);

        if (CollectionUtils.isEmpty(subTasks)) {
            return;
        }
        this.addActivity(subTasks.get(0));
        this.linkActivities(startActivity, subTasks.get(0), null);
        for (int i = 0; i < subTasks.size() - 1; i++) {
            linkTasks(subTasks.get(i), subTasks.get(i + 1));
        }
        linkActivities(subTasks.get(subTasks.size() - 1), endActivity, null);
    }

    /**
     * {@link #BLANK_GUARD_EXP} should always be checked first
     * {@link #OTHERWISE_GUARD_EXP} should always be checked last
     * @return
     */
    private static int compareGuard(String g1, String g2) {
        if (g1.equals(BLANK_GUARD_EXP)) return -1;
        if (g2.equals(BLANK_GUARD_EXP)) return 1;
        if (g1.equals(OTHERWISE_GUARD_EXP)) return 1;
        if (g2.equals(OTHERWISE_GUARD_EXP)) return -1;
        else return -1;
    }

    private void linkTasks(Task from, Task to) {
        this.addActivity(to);
        this.linkActivities(from, to, null);
    }

    @Override
    public JsonNode execute(JsonNode input, String executionId, Logger logger, JsonNode props) throws TaskExecutionException {

        ExecContext context = new ParamContext(props);

        try {

            Activity currentActivity = startActivity;
            startActivity.setOutput(input);

            while (true) {
                OutboundMessage out = executeActivity(currentActivity, context, executionId, logger);
                context.saveOutput(currentActivity, out);
                Activity nextActivity = getNextActivity(currentActivity, context);
                if (nextActivity instanceof EndActivity) {
                    break;
                }
                currentActivity = nextActivity;
            }

            try {
                return getOutput(context);
            } catch (Exception e) {
                throw new RuntimeException("Exception while transform the output");
            }
        } catch (Exception e) {
            throw new TaskExecutionException("Unexpected exception occurred", this, e);
        }
    }

    private JsonNode getOutput(ExecContext context) {
        if (StringUtils.isBlank(outputMapping)) {
            return null;
        }
        return context.transform(outputMapping);
    }

    private OutboundMessage executeActivity(Activity activity, ExecContext context, String executionId, Logger logger) throws ActivityException {
        InboundMessage inboundMessage;
        if (activity instanceof Task) {
            JsonNode taskInput = context.transformInput((Task) activity);
            inboundMessage = SimpleInboundMessage.of(taskInput);
        } else {
            inboundMessage = SimpleInboundMessage.empty();
        }
        return activity.process(inboundMessage, executionId, logger, context);
    }

    private Activity getNextActivity(Activity activity, ExecContext context) {
        Map<String, Activity> guardToActivityMap = links.get(activity);
        if (guardToActivityMap == null) {
            throw new IllegalStateException("Activity link to no where. Process stops");
        }
        List<String> guards = guardToActivityMap.keySet().stream()
                .sorted(CompoundTask::compareGuard).collect(Collectors.toList());
        for (String guard : guards) {
            if (isTrueGuard(context, guard)) {
                return guardToActivityMap.get(guard);
            }
        }
        throw new IllegalStateException("Cannot find the next activity because all links from current activity evaluate to FALSE");
    }

    private boolean isTrueGuard(ExecContext context, String guard) {
        if (BLANK_GUARD_EXP.equals(guard) || OTHERWISE_GUARD_EXP.equals(guard)) {
            return true;
        }
        JsonNode evaluationResult = context.transform(guard);
        return evaluationResult.asBoolean();
    }

    @Override
    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    @Override
    public void linkActivities(Activity from, Activity to, String guardExp) {
        if (!this.activities.contains(from) || !this.activities.contains(to)) {
            throw new IllegalStateException("Both activity must be added before linking");
        }
        String guard = StringUtils.defaultIfBlank(guardExp, BLANK_GUARD_EXP);
        Map<String, Activity> guardToActivityMap = links.computeIfAbsent(from, a -> new HashMap<>());
        if (guardToActivityMap.containsKey(guardExp)) {
            throw new IllegalArgumentException("Guard '" + guard + "' already been added for activity '" + from.getName() + "'");
        }
        guardToActivityMap.put(guard, to);
    }

    public void linkFromStart(Activity activity, String guard) {
        linkActivities(startActivity, activity, guard);
    }

    public void linkToEnd(Activity activity) {
        linkActivities(activity, endActivity, null);
    }

    public static class StartActivity implements Activity {

        public static final OutboundMessage START_ACTIVITY_OUTBOUND_MSG = () -> OBJECT_MAPPER.createObjectNode();
        public static final String NAME = "_START_";
        private JsonNode output;

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public boolean registerOutput() {
            return true;
        }

        public void setOutput(JsonNode output) {
            this.output = output;
        }

        @Override
        public OutboundMessage process(InboundMessage in, String executionId, Logger logger, ExecContext context) throws ActivityException {
            if (output == null) {
                return START_ACTIVITY_OUTBOUND_MSG;
            } else {
                return SimpleOutboundMessage.of(output);
            }
        }
    }

    public static class EndActivity implements Activity {

        public static final String NAME = "_END_";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public boolean registerOutput() {
            return false;
        }

        @Override
        public OutboundMessage process(InboundMessage in, String executionId, Logger logger, ExecContext context) throws ActivityException {
            return null;
        }
    }
}
