package io.github.manhnt217.task.task_executor.activity.impl;

import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.activity.InboundMessage;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;
import io.github.manhnt217.task.task_executor.process.Logger;

import java.util.UUID;

public class EndActivity implements Activity {

    private static final String NAME_PREFIX = "EndActivity-";

    private final String name;

    public EndActivity() {
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
        return null;
    }
}
