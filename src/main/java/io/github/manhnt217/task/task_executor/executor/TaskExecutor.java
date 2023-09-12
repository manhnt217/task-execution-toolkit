package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TemplateTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskExecutor {
    public static final ObjectMapper om = new ObjectMapper();

    static {
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.registerModule(new JSR310Module());
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }


    protected final List<ExecutionLog> logs;

    public static TaskExecutor getTaskExecutor(Task task) {
        if (task instanceof TemplateTask) {
            return new TemplateTaskExecutor();
        } else {
            return new CompoundTaskExecutor();
        }
    }

    protected TaskExecutor() {
        this.logs = new ArrayList<>(0);
    }

    public abstract JsonNode execute(Task task, JsonNode input);

    public List<ExecutionLog> getLogs() {
        return this.logs;
    }
}
