package io.github.manhnt217.task.task_executor.process.builtin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.manhnt217.task.task_executor.executor.TaskExecutor;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.TemplateLogHandler;
import kong.unirest.HttpMethod;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CurlTemplate extends Template<CurlTemplate.Input, CurlTemplate.Output> {

	@Override
	protected Class<? extends Input> getInputClass() {
		return Input.class;
	}

	@Override
	public Output exec(Input input, TemplateLogHandler logHandler) throws Exception {
		return doRequest(input.getUrl(), input.getMethod(), input.getHeaders(), input.getQueryParams(), input.getBody());
	}

	private Output doRequest(String requestURL, String method, Map<String, String> headers, Map<String, Object> queryParams, Object payload) throws Exception {

		HashMap<String, Object> params = processQueryParams(queryParams);

		HttpRequestWithBody req = Unirest.request(method, requestURL)
				.headers(headers)
				.queryString(params);


		HttpResponse<String> res;
		if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
			res = req.asString();
		} else {
			res = req.body(payload).asString();
		}

		if (res.isSuccess()) {
			return new Output(res.getStatus(), res.getBody());
		} else {
			return new Output(res.getStatus(),
					"Got an exception while making the request to portainer" +
							". URL = " + requestURL +
							". Code = " + res.getStatus() +
							". Message = " + res.getBody() +
							". Stacktrace = " + res.getParsingError().map(ExceptionUtils::getRootCauseMessage).orElse("")
					);
		}
	}

	private static HashMap<String, Object> processQueryParams(Map<String, Object> queryParams) throws JsonProcessingException {
		HashMap<String, Object> params = queryParams == null ? null : new HashMap<>(queryParams.size());
		if (queryParams != null) {
			for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
				Object v = entry.getValue();
				String key = entry.getKey();
				if (v instanceof String ||
						v instanceof Boolean ||
						v instanceof Long ||
						v instanceof Integer ||
						v instanceof Double ||
						v instanceof BigDecimal
				) {
					params.put(key, v);
				} else {
					params.put(key, TaskExecutor.om.writeValueAsString(v));
				}
			}
		}
		return params;
	}

	@Getter
	@Setter
	public static class Input {
		private String url;
		private String method;
		private Object body;
		private Map<String, Object> queryParams;
		private Map<String, String> headers;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public static class Output {
		private int statusCode;
		private String responseText;
	}
}
