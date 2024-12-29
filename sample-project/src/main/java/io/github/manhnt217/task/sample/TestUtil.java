package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author manh nguyen
 */
public class TestUtil {

    public static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
}