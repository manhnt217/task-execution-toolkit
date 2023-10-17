package io.github.manhnt217.task.task_executor.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author manhnguyen
 */
public class CommonUtil {
    public static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        OM.registerModule(new JSR310Module());
        OM.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    public static String getErrorSummary(Throwable err) {
        return ExceptionUtils.getThrowableList(err).stream()
                .map(t -> getShortClassName(t.getClass()) + ": " + StringUtils.defaultIfBlank(t.getMessage(), ""))
                .collect(Collectors.joining(" Caused by: "));
    }

    private static String getShortClassName(Class<? extends Throwable> clazz) {
        return Arrays.stream(clazz.getPackage().getName().split("\\."))
                .map(t -> t.substring(0, 1)).collect(Collectors.joining(".")) + '.' + clazz.getSimpleName();
    }

    public static Date toDate(OffsetDateTime offsetDateTime) {
        return Date.from(offsetDateTime.toInstant());
    }
}
