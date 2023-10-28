package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;
import io.github.manhnt217.task.task_engine.exception.inner.ContextException;
import io.github.manhnt217.task.task_engine.exception.inner.TransformException;
import io.github.manhnt217.task.task_engine.type.ObjectRef;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;

/**
 * @author manhnguyen
 */
public class JSONUtil {

    private static final ThreadLocal<OM> OBJECT_MAPPER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new JSR310Module());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        SimpleModule objectRefModule = new SimpleModule();
        ObjectRefDeserializer deserializer = new ObjectRefDeserializer();
        ObjectRefSerializer serializer = new ObjectRefSerializer();
        objectRefModule.addSerializer(ObjectRef.class, serializer);
        objectRefModule.addDeserializer(ObjectRef.class, deserializer);
        objectMapper.registerModule(objectRefModule);

        return new OM(objectMapper, serializer, deserializer);
    });

    private static final String REF_ID = "refId";

    private static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER_THREAD_LOCAL.get().getObjectMapper();
    }

    private static void setActivityContext(ActivityContext context) {
        OBJECT_MAPPER_THREAD_LOCAL.get().getDeserializer().setActivityContext(context);
        OBJECT_MAPPER_THREAD_LOCAL.get().getSerializer().setActivityContext(context);
    }

    private static void clearActivityContext() {
        OBJECT_MAPPER_THREAD_LOCAL.get().getDeserializer().setActivityContext(null);
        OBJECT_MAPPER_THREAD_LOCAL.get().getSerializer().setActivityContext(null);
    }

    /* We may need this in the future
    public static final void clear() {
        OBJECT_MAPPER_THREAD_LOCAL.remove();
    }
    */

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
        return getObjectMapper().createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return getObjectMapper().createObjectNode();
    }

    public static <T> T treeToValue(TreeNode n, Class<T> valueType, ActivityContext activityContext) throws IllegalArgumentException, JsonProcessingException {

        setActivityContext(activityContext);
        T value = getObjectMapper().treeToValue(n, valueType);
        clearActivityContext();
        return  value;
    }

    public static <T extends JsonNode> T valueToTree(Object fromValue, ActivityContext activityContext) throws IllegalArgumentException {
        setActivityContext(activityContext);
        T value = getObjectMapper().valueToTree(fromValue);
        clearActivityContext();
        return value;

    }

    @Getter
    @RequiredArgsConstructor
    private static class OM {
        private final ObjectMapper objectMapper;
        private final ObjectRefSerializer serializer;
        private final ObjectRefDeserializer deserializer;
    }

    @Setter
    public static class ObjectRefDeserializer extends StdDeserializer<ObjectRef> {

        private ActivityContext activityContext;

        protected ObjectRefDeserializer() {
            super((Class<?>) null);
        }

        @Override
        public ObjectRef deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            TreeNode treeNode = p.getCodec().readTree(p);
            TreeNode refIdNode = treeNode.get(REF_ID);
            if (refIdNode == null || !(refIdNode instanceof TextNode)) {
                throw new JsonParseException(p, "refId not found for ObjectRef");
            }
            if (activityContext == null) {
                throw new JsonParseException(p, "No activity context to resolve object ref. Call setActivityContext on the deserializer first");
            }
            try {
                return activityContext.resolveRef(((TextNode) refIdNode).textValue());
            } catch (ContextException e) {
                throw new JsonParseException(p, e.getMessage());
            }
        }
    }

    @Setter
    public static class ObjectRefSerializer extends StdSerializer<ObjectRef> {

        private ActivityContext activityContext;

        protected ObjectRefSerializer() {
            super((Class<ObjectRef>) null);
        }

        @Override
        public void serialize(ObjectRef value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (activityContext == null) {
                throw new IOException("No activity context to create object ref. Call setActivityContext on the serializer first");
            }
            String refId = activityContext.createRef(value.get());
            gen.writeObject(Collections.singletonMap(REF_ID, refId));
        }
    }
}
