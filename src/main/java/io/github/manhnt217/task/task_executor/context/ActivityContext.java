package io.github.manhnt217.task.task_executor.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.ActivityContextException;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;
import io.github.manhnt217.task.task_executor.activity.impl.EndActivity;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

/**
 * @author manhnguyen
 */
public class ActivityContext {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.registerModule(new JSR310Module());
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    public static final String KEY_PROPS = "_PROPS_";
    public static final String ALL_SUBTASKS_JSLT = "{" +
            "\"" + KEY_PROPS + "\": null," +
            " * : . " +
            "}";

    /**
     * Capture all the inputs & outputs of each task
     */
    protected final ObjectNode contextParams; // root object to evaluate JSLT expression
    private final String executionId;
    protected JsonNode props;

    public ActivityContext(String executionId, JsonNode props) {
        this.executionId = executionId;
        this.contextParams = OBJECT_MAPPER.createObjectNode();
        if (props != null) {
            this.props = props;
            this.contextParams.set(KEY_PROPS, this.props);
        }
    }

    protected ActivityContext(String executionId) {
        this(executionId, null);
    }

    public void saveOutput(Activity activity, OutboundMessage output) throws ActivityContextException {
        if (activity.registerOutput() && output != null && !output.isEmpty()) {
            if (contextParams.get(activity.getName()) != null) {
                throw new ActivityContextException("Output of activity '" + activity.getName() + "' has already existed in the context");
            }
            contextParams.set(activity.getName(), output.getContent());
        }
    }

    public JsonNode transformInput(Activity activity) {
        return JSLTUtil.applyTransform(activity.getInputMapping(), contextParams);
    }

    public JsonNode evaluate(String jslt) {
        return JSLTUtil.applyTransform(jslt, contextParams);
    }

    public String getExecutionId() {
        return executionId;
    }

    public ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    public ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    public <T> T treeToValue(TreeNode n, Class<T> valueType) throws IllegalArgumentException, JsonProcessingException {
        return OBJECT_MAPPER.treeToValue(n, valueType);
    }

    public <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
        return OBJECT_MAPPER.valueToTree(fromValue);
    }
}
