package io.github.manhnt217.task.task_executor.activity;

import io.github.manhnt217.task.task_executor.process.Logger;

public interface Activity {

    String getName();

    /**
     * Specify whether the output of this activity will be recorded for later use
     * @return
     */
    boolean registerOutput();

    /**
     * @param in
     * @param executionId
     * @param logger
     * @return <code>null</code> means the activity has done processing the input,
     * but hasn't returned any output yet due to some certain criteria are not met
     */
    OutboundMessage process(InboundMessage in, String executionId, Logger logger) throws ActivityException;
}