package io.github.manhnt217.task.persistence.model.activity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.activity.source.FromSourceActivity;
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
        @JsonSubTypes.Type(value = TrialActivityDto.class, name = ActivityDto.Type.TRY),
        @JsonSubTypes.Type(value = GroupActivityDto.class, name = ActivityDto.Type.GROUP),
        @JsonSubTypes.Type(value = ForeachActivityDto.class, name = ActivityDto.Type.FOREACH),
        @JsonSubTypes.Type(value = FunctionActivityDto.class, name = ActivityDto.Type.FUNC),
        @JsonSubTypes.Type(value = PluginActivityDto.class, name = ActivityDto.Type.PLUGIN),
        @JsonSubTypes.Type(value = SourceActivityDto.class, name = ActivityDto.Type.SOURCE)
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
