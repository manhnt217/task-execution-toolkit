package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.activity.InboundMessage;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;
import io.github.manhnt217.task.task_executor.activity.impl.SimpleOutboundMessage;
import io.github.manhnt217.task.task_executor.process.Logger;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Set;

@Getter @Setter
public abstract class Task implements Activity {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static {
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		OBJECT_MAPPER.registerModule(new JSR310Module());
		OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
	}

	public Task(String name) {
		this.name = name;
	}

	@Override
	public OutboundMessage process(InboundMessage in, String executionId, Logger logger, ExecContext context) throws ActivityException {

		log(getStartLogExpression(), context, executionId, logger);

		SimpleOutboundMessage out = SimpleOutboundMessage.of(this.execute(in.getContent(), executionId, logger));

		log(getEndLogExpression(), context, executionId, logger);

		return out;
	}

	private void log(String logExp, ExecContext ctx, String executionId, Logger logger) {
		if (StringUtils.isBlank(logExp)) {
			return;
		}
		try {
			JsonNode jsonNode = ctx.transform(logExp);
			logger.info(executionId, getName(), jsonNode.isContainerNode() ? "" : jsonNode.asText());
		} catch (Exception e) {
			logger.warn(executionId, getName(), "Error while applying log expression to the context. Expression = " + logExp, e);
		}
	}

	public abstract JsonNode execute(JsonNode input, String executionId, Logger logger) throws TaskExecutionException;

	/**
	 * <ul>
	 * 	   <li> Contains ALPHANUMERICS ONLY </li>
	 * </ul>
	 */
	protected String name;

	protected String inputMappingExpression;

	protected String startLogExpression;

	protected String endLogExpression;

	protected Set<String> dependencies = Collections.emptySet();

	/**
	 * Output of a task is recorded by default
	 * @return
	 */
	@Override
	public boolean registerOutput() {
		return true;
	}
}