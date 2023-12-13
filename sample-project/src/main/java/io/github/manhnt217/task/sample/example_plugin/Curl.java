package io.github.manhnt217.task.sample.example_plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.core.task.plugin.PluginLogger;
import io.github.manhnt217.task.sample.Util;
import kong.unirest.HttpMethod;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author manh nguyen
 */
public class Curl extends Plugin<Curl.Input, Curl.Output> {

    // Uncomment this to disable redirect globally
    //	static {
    //		Unirest.config()
    //				.followRedirects(false);
    //	}

    @Override
    public Class<? extends Input> getInputType() {
        return Input.class;
    }

    @Override
    public Output exec(Input input, PluginLogger logger) throws Exception {
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
            return new Output(res.getStatus(), "Got an exception while making the request");
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
                    params.put(key, Util.OM.writeValueAsString(v));
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
