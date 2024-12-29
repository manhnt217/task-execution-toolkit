package io.github.manhnt217.task.task_executor.task;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static io.github.manhnt217.task.task_executor.executor.ParamContext.WITHOUT_PARENT_JSLT;

@Getter
@Setter
public class CompoundTask extends Task {

    private List<Task> subTasks;

    private String outputMapping;

    public CompoundTask() {
        this.subTasks = new ArrayList<>(0);
    }

    public CompoundTask(List<Task> subTasks) {
        this.subTasks = new ArrayList<>(subTasks);
    }

    public void setOutputMapping(String outputMapping) {
        if (StringUtils.isBlank(outputMapping)) {
            this.outputMapping = WITHOUT_PARENT_JSLT;
        } else {
            this.outputMapping = outputMapping;
        }
    }
}
