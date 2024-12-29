package io.github.manhnt217.task.core.task.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.GroupException;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.task.TaskContext;
import lombok.Getter;

/**
 * @author manh nguyen
 */
public class Function<P, R> implements io.github.manhnt217.task.core.task.Task {

    public static final String START_ACTIVITY_NAME = "START";
    public static final String END_ACTIVITY_NAME = "END";
    public static final String START_INPUT_MAPPING = "." + START_ACTIVITY_NAME;
    @Getter
    protected final String name;
    protected final Group activityGroup;
    @Getter
    protected final Class<? extends P> inputType;
    @Getter
    protected final Class<? extends R> outputType;

    public Function(String name, Group activityGroup, Class<? extends P> inputType, Class<? extends R> outputType) {
        this.name = name;
        this.activityGroup = activityGroup;
        this.inputType = inputType;
        this.outputType = outputType;
    }
    // TODO:
    //  Note: Converting input/output (JsonNode) from/to desired type (R/P) is for 2 purpose
    //   * 1 - (main) Transfer ObjectRef between parent and child ActivityContext. So that we know which object need to be kept.
    //         (Previously we kept all the object ref in RootActivityContext which might cause out of memory after a long run)
    //   * 2 - Parameterize a Callable make it easier to predict the input/output. It will help the UI to build the inputmapping easier
    //   (because we know what the target input types, output types look like)
    //  If there is any performance issue on converting input/output (JsonNode) from/to desired type. We should consider following approach:
    //   - Remove the conversion
    //   - Check for the appearance of ObjectRef in the return (of a Callable) by travese through the output (JsonNode), check for any key
    //     named refId, and the node type is TextNode, and the text value equals to one of the available object refs. Then we copy those refs to the parent context.
    //   - All the Object Refs in the parent context will be available in the child context.
    public R exec(P in, TaskContext context) throws TaskException {
        JsonNode input = inputType == null ? null : JSONUtil.valueToTree(in, context);
        context.setTaskName(this.name);
        JsonNode groupOutput;
        try {
            groupOutput = activityGroup.execute(input, context);
        } catch (GroupException | ActivityException e) {
            throw new TaskException(getName(), input, e);
        }
        if (outputType == null) {
            return null;
        }
        try {
            return JSONUtil.treeToValue(groupOutput, outputType, context);
        } catch (JsonProcessingException e) {
            throw new TaskException(getName(), input, "Cannot convert output to desired type. Output = " + groupOutput + ". Desired type = " + outputType, e);
        }
    }
}
