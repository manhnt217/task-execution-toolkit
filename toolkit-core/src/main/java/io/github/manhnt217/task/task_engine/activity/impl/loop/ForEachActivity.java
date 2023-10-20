package io.github.manhnt217.task.task_engine.activity.impl.loop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_engine.activity.*;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;
import io.github.manhnt217.task.task_engine.activity.impl.LinkBasedActivityGroup;
import io.github.manhnt217.task.task_engine.activity.impl.SimpleOutboundMessage;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.context.ForEachContext;
import lombok.Getter;
import lombok.Setter;

import static io.github.manhnt217.task.task_engine.context.ActivityContext.ALL_SUBTASKS_JSLT;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class ForEachActivity extends LinkBasedActivityGroup implements Activity {

    public static final String KEY_ITEM = "item";
    public static final String KEY_INDEX = "index";
    private final String name;

    private String inputMapping;

    public ForEachActivity(String name, String startActivityName, String endActivityName, String outputMapping) {
        super(startActivityName, endActivityName, outputMapping);
        this.name = name;
    }

    public ForEachActivity(String name, String startActivityName, String endActivityName) {
        this(name, startActivityName, endActivityName, ALL_SUBTASKS_JSLT);
    }

    @Override
    public boolean registerOutput() {
        return true;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        JsonNode input = in.getContent();
        if (!(input instanceof ArrayNode)) {
            throw new IllegalArgumentException("Input must be an array");
        }
        ArrayNode inputArr = (ArrayNode) input;
        ArrayNode outputArr = context.createArrayNode();

        int index = 0;
        for (JsonNode item : inputArr) {
            ForEachContext loopContext = new ForEachContext(context);
            ObjectNode loopInput = context.createObjectNode();
            loopInput.set(KEY_ITEM, item);
            loopInput.set(KEY_INDEX, new IntNode(index));
            try {
                JsonNode output = this.execute(loopInput, activityLogger, loopContext);
                outputArr.add(output);
            } catch (GroupException e) {
                throw new ActivityException(this, "", e);
            }
            index++;
        }

        return SimpleOutboundMessage.of(outputArr);
    }
}
