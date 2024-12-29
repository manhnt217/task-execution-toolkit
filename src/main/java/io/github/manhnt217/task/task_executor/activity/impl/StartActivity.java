package io.github.manhnt217.task.task_executor.activity.impl;

import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.activity.InboundMessage;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;
import io.github.manhnt217.task.task_executor.process.Logger;
import io.github.manhnt217.task.task_executor.task.Task;

import java.util.UUID;

public class StartActivity implements Activity {

    public static final OutboundMessage START_ACTIVITY_OUTBOUND_MSG = () -> Task.OBJECT_MAPPER.createObjectNode();

    private static final String NAME_PREFIX = "StartActivity-";

    private final String name;

    public StartActivity() {
        name = NAME_PREFIX + UUID.randomUUID();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean registerOutput() {
        return false;
    }

    @Override
    public OutboundMessage process(InboundMessage in, String executionId, Logger logger) throws ActivityException {
        return START_ACTIVITY_OUTBOUND_MSG;
    }
}
