package io.github.manhnt217.task.task_executor.activity.impl;

import io.github.manhnt217.task.task_executor.activity.Activity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ActivityLink {
    private final Activity from;
    private final Activity to;
    private final String guardExp;
}
