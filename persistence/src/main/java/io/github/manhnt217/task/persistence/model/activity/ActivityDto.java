package io.github.manhnt217.task.persistence.model.activity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.manhnt217.task.persistence.model.ActivityGroupDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TrialActivityDto.class, name = ActivityDto.Type.TRY)
})
public class ActivityDto {

    private String name;
    private String inputMapping;

    private ActivityGroupDto group;

    public static class Type {
        public static final String GROUP = "GROUP";
        public static final String FOREACH = "FOREACH";
        public static final String TRY = "TRY";
        public static final String FUNC = "FUNC";
        public static final String PLUGIN = "PLUGIN";
        public static final String SOURCE = "SOURCE";
    }
}
