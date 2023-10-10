package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.*;
import io.github.manhnt217.task.task_executor.activity.impl.EndActivity;
import io.github.manhnt217.task.task_executor.activity.impl.SimpleInboundMessage;
import io.github.manhnt217.task.task_executor.activity.impl.StartActivity;
import io.github.manhnt217.task.task_executor.process.Logger;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static io.github.manhnt217.task.task_executor.task.ParamContext.WITHOUT_PARENT_JSLT;

@Getter
@Setter
public class CompoundTask extends Task implements ContainerActivity {

    private List<Activity> activities;

    private String outputMapping = WITHOUT_PARENT_JSLT;
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
        this(Collections.emptyList());
    }

    public CompoundTask(List<Task> subTasks) {
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

    private void linkTasks(Task from, Task to) {
        this.addActivity(to);
        this.linkActivities(from, to, null);
    }

    @Override
    public JsonNode execute(JsonNode input, String executionId, Logger logger) throws TaskExecutionException {

        ParamContext context = new ParamContext();

        context.setParentInput(input);

        try {

            Activity currentActivity = startActivity;

            while (true) {
                OutboundMessage out = executeActivity(currentActivity, context, executionId, logger);
                saveOutput(currentActivity, out, context);
                Activity nextActivity = getNextActivity(currentActivity, context);
                if (nextActivity instanceof EndActivity) {
                    break;
                }
                currentActivity = nextActivity;
            }

            try {
                return context.transform(this.getOutputMapping());
            } catch (Exception e) {
                throw new RuntimeException("Exception while transform the output");
            }
        } catch (Exception e) {
            throw new TaskExecutionException("Unexpected exception occurred", this, e);
        }
    }

    private OutboundMessage executeActivity(Activity activity, ParamContext context, String executionId, Logger logger) throws ActivityException {
        InboundMessage inboundMessage;
        if (activity instanceof Task) {
            JsonNode taskInput = context.transformInput((Task) activity);
            inboundMessage = SimpleInboundMessage.of(taskInput);
        } else {
            inboundMessage = SimpleInboundMessage.empty();
        }
        return activity.process(inboundMessage, executionId, logger);
    }

    private void saveOutput(Activity activity, OutboundMessage out, ParamContext context) {
        if (activity.registerOutput()) {
            context.saveOutput(activity, out.getContent());
        }
    }

    private Activity getNextActivity(Activity activity, ParamContext context) {
        Map<String, Activity> guardToActivityMap = links.get(activity);
        if (guardToActivityMap == null) {
            throw new IllegalStateException("Activity link to no where. Process stops");
        }
        String trueGuard = guardToActivityMap.keySet().stream()
                .filter(guard -> isTrueGuard(context, guard))
                .findAny().orElseThrow(() -> new IllegalStateException("Cannot find the next activity because all links from current activity evaluate to FALSE"));
        Activity nextActivity = guardToActivityMap.get(trueGuard);
        return nextActivity;
    }

    private boolean isTrueGuard(ParamContext context, String guard) {
        if (BLANK_GUARD_EXP.equals(guard)) {
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

    public void setOutputMapping(String outputMapping) {
        if (StringUtils.isBlank(outputMapping)) {
            this.outputMapping = WITHOUT_PARENT_JSLT;
        } else {
            this.outputMapping = outputMapping;
        }
    }
}
