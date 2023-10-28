package io.github.manhnt217.task.sample.test.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.reflect.TypeToken;
import io.github.manhnt217.task.task_engine.type.Array;
import io.github.manhnt217.task.task_engine.type.EngineObject;
import io.github.manhnt217.task.task_engine.type.Mapping;
import io.github.manhnt217.task.task_engine.type.TypeDescriptor;
import io.github.manhnt217.task.task_engine.type.simple.Bool;
import io.github.manhnt217.task.task_engine.type.simple.Decimal;
import io.github.manhnt217.task.task_engine.type.simple.Int;
import io.github.manhnt217.task.task_engine.type.simple.ObjectRef;
import io.github.manhnt217.task.task_engine.type.simple.Text;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manhnguyen
 */
@Execution(ExecutionMode.CONCURRENT)
public class TypeDescriptorTest {

    @Test
    public void testObjectType() throws JsonProcessingException {
        TypeDescriptor typeDescriptor = TypeDescriptor.getTypeDescriptor(Foo.class);

        assertThat(typeDescriptor.getTypeName(), is(Foo.class.getName()));

        Map<String, TypeDescriptor> m = typeDescriptor.getDetail();

        assertThat(m, hasKey("bar1"));
        assertThat(m, hasKey("bar3"));
        assertThat(m, hasKey("bar4"));
        assertThat(m, hasKey("bar5"));
        assertThat(m, hasKey("bar6"));
        assertThat(m, hasKey("bar7"));
        assertThat(m, hasKey("bar8"));

        assertThat(m.get("bar1").getTypeName(), is("Text"));
        assertThat(m.get("bar3").getTypeName(), is("ObjectRef"));
        assertThat(m.get("bar4").getTypeName(), is("Int"));
        assertThat(m.get("bar5").getTypeName(), is("Decimal"));
        assertThat(m.get("bar6").getTypeName(), is("Bool"));
        assertThat(m.get("bar7").getTypeName(), is("Array"));
        assertThat(m.get("bar8").getTypeName(), is("Mapping"));
    }

    @Getter
    @Setter
    private static class Foo extends EngineObject {
        private Text bar1;
        private String bar2;
        private ObjectRef bar3;
        private Int bar4;
        private Decimal bar5;
        private Bool bar6;
        private Array<Int> bar7;
        private Mapping<Text> bar8;
    }

    @Getter
    @Setter
    private static class Fizz extends Foo {
        private Int buzz;
    }

    @Test
    public void testInheritance() {
        TypeDescriptor typeDescriptor = TypeDescriptor.getTypeDescriptor(Fizz.class);
        assertThat(typeDescriptor.getTypeName(), is(Fizz.class.getName()));

        Map<String, TypeDescriptor> m = typeDescriptor.getDetail();

        assertThat(m, hasKey("bar1"));
        assertThat(m, hasKey("bar3"));
        assertThat(m, hasKey("bar4"));
        assertThat(m, hasKey("bar5"));
        assertThat(m, hasKey("bar6"));
        assertThat(m, hasKey("bar7"));
        assertThat(m, hasKey("bar8"));

        assertThat(m.get("bar1").getTypeName(), is("Text"));
        assertThat(m.get("bar3").getTypeName(), is("ObjectRef"));
        assertThat(m.get("bar4").getTypeName(), is("Int"));
        assertThat(m.get("bar5").getTypeName(), is("Decimal"));
        assertThat(m.get("bar6").getTypeName(), is("Bool"));
        assertThat(m.get("bar7").getTypeName(), is("Array"));
        assertThat(m.get("bar8").getTypeName(), is("Mapping"));

        assertThat(m, hasKey("buzz"));
        assertThat(m.get("buzz").getTypeName(), is("Int"));
    }

    @Getter @Setter
    public static class AA extends EngineObject {
        Array<Array<Text>> aa;
    }

    @Test
    public void testComplexArray() {
        Map<String, TypeDescriptor> detail1 = TypeDescriptor.getTypeDescriptor(AA.class).getDetail();

        assertThat(detail1, hasKey("aa"));

        assertThat(detail1.get("aa").getTypeName(), is("Array"));
        assertThat(detail1.get("aa").getSubType().getTypeName(), is("Array"));
        assertThat(detail1.get("aa").getSubType().getSubType().getTypeName(), is("Text"));
    }

    @Test
    public void testComplexArray2() {

        TypeToken<Array<Array<Text>>> t = new TypeToken<Array<Array<Text>>>(){};
        TypeDescriptor desc = TypeDescriptor.getTypeDescriptor(t.getType());

        assertThat(desc.getTypeName(), is("Array"));
        assertThat(desc.getSubType().getTypeName(), is("Array"));
        assertThat(desc.getSubType().getSubType().getTypeName(), is("Text"));
    }

    @Getter
    @Setter
    private static class A extends EngineObject {
        private B b;
    }

    @Getter
    @Setter
    private static class B extends EngineObject {
        private A a;
    }

    @Test
    public void testRecursiveType() {
        TypeDescriptor aDescriptor = TypeDescriptor.getTypeDescriptor(A.class);

        assertThat(aDescriptor.getTypeName(), is(A.class.getName()));

        Map<String, TypeDescriptor> aDetail = aDescriptor.getDetail();

        assertThat(aDetail, hasKey("b"));

        assertThat(aDetail.get("b").getTypeName(), is(B.class.getName()));

        Map<String, TypeDescriptor> bDetail = aDetail.get("b").getDetail();

        assertThat(bDetail, hasKey("a"));
        assertThat(bDetail.get("a").getTypeName(), is(A.class.getName()));
        assertNull(bDetail.get("a").getDetail());
        assertNull(bDetail.get("a").getSubType());
    }
}
