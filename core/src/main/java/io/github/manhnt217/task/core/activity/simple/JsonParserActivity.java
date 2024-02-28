package io.github.manhnt217.task.core.activity.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manhnguyen
 */
public class JsonParserActivity extends AbstractActivity {
    public JsonParserActivity(String name) {
        super(name);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        if (in.isEmpty()) {
            throw new JsonParserActivityException(context.getCurrentTaskName(), this.getName(), "Input is empty");
        }
        if (!in.getContent().isTextual()) {
            throw new JsonParserActivityException(context.getCurrentTaskName(), this.getName(), "Input is not a String");
        }
        String inText = in.getContent().textValue();

        try {
            JsonNode json = JSONUtil.MAPPER.readTree(inText);
            return SimpleOutboundMessage.of(json);
        } catch (JsonProcessingException e) {
            throw new JsonParserActivityException(context.getCurrentTaskName(), this.getName(), null, e);
        }

    }
}
