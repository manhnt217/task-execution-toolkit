package io.github.manhnt217.task.persistence.model.activity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.manhnt217.task.persistence.model.activity.simple.JsonParserActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.MapperActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.PluginActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.SourceActivityDto;
import io.github.manhnt217.task.persistence.model.activity.simple.WaitActivityDto;
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
        @JsonSubTypes.Type(value = TrialActivityDto.class, name = ActivityDto.Type.TRY),
        @JsonSubTypes.Type(value = GroupActivityDto.class, name = ActivityDto.Type.GROUP),
        @JsonSubTypes.Type(value = ForeachActivityDto.class, name = ActivityDto.Type.FOREACH),
        @JsonSubTypes.Type(value = FunctionActivityDto.class, name = ActivityDto.Type.FUNC),
        @JsonSubTypes.Type(value = PluginActivityDto.class, name = ActivityDto.Type.PLUGIN),
        @JsonSubTypes.Type(value = SourceActivityDto.class, name = ActivityDto.Type.SOURCE),
        @JsonSubTypes.Type(value = FunctionActivityDto.class, name = ActivityDto.Type.FUTURE),
        @JsonSubTypes.Type(value = WaitActivityDto.class, name = ActivityDto.Type.WAIT),
        @JsonSubTypes.Type(value = JsonParserActivityDto.class, name = ActivityDto.Type.JSON_PARSER),
        @JsonSubTypes.Type(value = MapperActivityDto.class, name = ActivityDto.Type.MAPPER)
})
public class ActivityDto {

    private String name;
    private String inputMapping;

    public static class Type {
        public static final String GROUP = "GROUP";
        public static final String FOREACH = "FOREACH";
        public static final String TRY = "TRY";
        public static final String FUNC = "FUNC";
        public static final String PLUGIN = "PLUGIN";
        public static final String SOURCE = "SOURCE";
        public static final String FUTURE = "FUTURE";
        public static final String WAIT = "WAIT";
        public static final String JSON_PARSER = "JSON_PARSER";
        public static final String MAPPER = "MAPPER";
    }
}
