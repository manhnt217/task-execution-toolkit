package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;
import io.github.manhnt217.task.task_engine.exception.inner.TransformException;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

/**
 * @author manhnguyen
 */
public class JSONUtil {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.registerModule(new JSR310Module());
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    public static JsonNode applyTransform(String jsltExp, JsonNode input) throws TransformException {
        try {
            if (StringUtils.isBlank(jsltExp)) {
                return NullNode.getInstance();
            }

            // TODO: Cache the result of compilation if it is time-consuming process (need to monitor)
            Expression jslt = Parser.compileString(jsltExp);
            return jslt.apply(input);
        } catch (Exception e) {
            throw new TransformException(e);
        }
    }

    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    public static <T> T treeToValue(TreeNode n, Class<T> valueType) throws IllegalArgumentException, JsonProcessingException {
        return OBJECT_MAPPER.treeToValue(n, valueType);
    }

    public static <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
        return OBJECT_MAPPER.valueToTree(fromValue);
    }
}
