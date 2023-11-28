package io.github.manhnt217.task.core.activity.loop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.AbstractGroupActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.GroupException;

/**
 * @author manh nguyen
 */
public class ForEachActivity extends AbstractGroupActivity {

    public static final String KEY_ITEM = "item";
    public static final String KEY_INDEX = "index";

    public ForEachActivity(String name, Group activityGroup) {
        super(name, activityGroup);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
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
                JsonNode output = activityGroup.execute(loopInput, loopContext);
                outputArr.add(output);
            } catch (GroupException e) {
                throw new ActivityException(this, "", e);
            }
            index++;
        }

        return SimpleOutboundMessage.of(outputArr);
    }
}
