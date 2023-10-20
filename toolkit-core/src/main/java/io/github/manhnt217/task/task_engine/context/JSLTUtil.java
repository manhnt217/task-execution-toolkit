package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;
import io.github.manhnt217.task.task_engine.exception.inner.TransformException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manhnguyen
 */
public class JSLTUtil {

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
}
