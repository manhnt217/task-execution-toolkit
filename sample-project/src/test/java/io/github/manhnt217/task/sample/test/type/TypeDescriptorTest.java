package io.github.manhnt217.task.sample.test.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.task_engine.type.ObjectRef;
import io.github.manhnt217.task.task_engine.type.ObjectType;
import io.github.manhnt217.task.task_engine.type.TextType;
import io.github.manhnt217.task.task_engine.type.TypeDescriptor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

/**
 * @author manhnguyen
 */
public class TypeDescriptorTest {

    @Test
    public void testObjectType() throws JsonProcessingException {
        Object typeDescription = TypeDescriptor.getTypeDescription(Foo.class);
        String rs = TestUtil.OM.writeValueAsString(typeDescription);
        System.out.println(rs);
    }

    @Getter @Setter
    private static class Foo extends ObjectType {
        private TextType bar1;
        private String bar2;
        private ObjectRef bar3;
    }
}
