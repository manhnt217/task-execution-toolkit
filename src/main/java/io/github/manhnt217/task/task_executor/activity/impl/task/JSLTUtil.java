package io.github.manhnt217.task.task_executor.activity.impl.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;
import org.apache.commons.lang3.StringUtils;

public class JSLTUtil {

	public static JsonNode applyTransform(String jsltExp, JsonNode input) {
		if (StringUtils.isBlank(jsltExp)) {
			return input;
		}

		// TODO: Cache the result of compilation if it is time-consuming process (need to monitor)
		Expression jslt = Parser.compileString(jsltExp);
		return jslt.apply(input);
	}
}
