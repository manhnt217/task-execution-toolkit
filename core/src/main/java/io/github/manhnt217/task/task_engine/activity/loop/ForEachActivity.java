package io.github.manhnt217.task.task_engine.activity.loop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.activity.InboundMessage;
import io.github.manhnt217.task.task_engine.activity.OutboundMessage;
import io.github.manhnt217.task.task_engine.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.task_engine.activity.group.Group;
import io.github.manhnt217.task.task_engine.activity.AbstractActivity;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.context.JSONUtil;
import io.github.manhnt217.task.task_engine.context.sub.ForEachContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;

/**
 * @author manhnguyen
 */
public class ForEachActivity extends AbstractActivity implements Activity {

    public static final String KEY_ITEM = "item";
    public static final String KEY_INDEX = "index";

    private final Group activityGroup;

    public ForEachActivity(String name, Group activityGroup) {
        super(name);
        this.activityGroup = activityGroup;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        JsonNode input = in.getContent();
        if (!(input instanceof ArrayNode)) {
            throw new IllegalArgumentException("Input must be an array");
        }
        ArrayNode inputArr = (ArrayNode) input;
        ArrayNode outputArr = JSONUtil.createArrayNode();

        int index = 0;
        for (JsonNode item : inputArr) {
            ForEachContext loopContext = new ForEachContext(context);
            ObjectNode loopInput = JSONUtil.createObjectNode();
            loopInput.set(KEY_ITEM, item);
            loopInput.set(KEY_INDEX, new IntNode(index));
            try {
                JsonNode output = activityGroup.execute(loopInput, activityLogger, loopContext);
                outputArr.add(output);
            } catch (GroupException e) {
                throw new ActivityException(this, "", e);
            }
            index++;
        }

        return SimpleOutboundMessage.of(outputArr);
    }
}
