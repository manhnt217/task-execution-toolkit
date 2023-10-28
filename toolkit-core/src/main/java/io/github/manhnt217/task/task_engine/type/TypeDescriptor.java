package io.github.manhnt217.task.task_engine.type;

import io.github.manhnt217.task.task_engine.type.simple.EngineSimpleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author manhnguyen
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TypeDescriptor {

    private String typeName;
    private TypeDescriptor subType;
    private Map<String, TypeDescriptor> detail;

    public static TypeDescriptor getTypeDescriptor(Type t) {
        List<Class<? extends EngineObject>> visited = new ArrayList<>();
        return getTypeDescriptor0(t, visited);
    }

    private static TypeDescriptor getTypeDescriptor0(Type t, List<Class<? extends EngineObject>> visited) {

        if (t instanceof Class) {
            Class clazz = (Class) t;
            if (EngineObject.class.isAssignableFrom(clazz)) {
                if (visited.contains(clazz)) {
                    return new TypeDescriptor(clazz.getName(), null, null);
                } else {
                    visited.add((Class<? extends EngineObject>) t);
                    return new TypeDescriptor(clazz.getName(), null, getObjectTypeDescription(clazz, visited));
                }
            }

            if (EngineSimpleType.class.isAssignableFrom(clazz)){
                return new TypeDescriptor(clazz.getSimpleName(), null, null);
            }

            return null;
        }

        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            return getTypeDescriptorForArrayAndMapping(pt, visited);
        }

        return null;
    }

    private static TypeDescriptor getTypeDescriptorForArrayAndMapping(ParameterizedType pt, List<Class<? extends EngineObject>> visited) {
        Type rawType = pt.getRawType();
        if (
                rawType instanceof Class &&
                (
                        Array.class.isAssignableFrom((Class<?>) rawType)  ||
                        Mapping.class.isAssignableFrom((Class<?>) rawType)
                )
        ) {
            Type subType = pt.getActualTypeArguments()[0];
            return new TypeDescriptor(((Class<?>) rawType).getSimpleName(), getTypeDescriptor0(subType, visited), null);
        }
        return null;
    }

    private static Map<String, TypeDescriptor> getObjectTypeDescription(Class<? extends EngineType> clazz, List<Class<? extends EngineObject>> visited) {
        Map<String, TypeDescriptor> result = new HashMap<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                Type fieldType = getEngineType(descriptor);
                if (fieldType == null) {
                    continue;
                }
                result.put(descriptor.getName(), getTypeDescriptor0(fieldType, visited));
            }
            return result;
        } catch (IntrospectionException e) {
            throw new RuntimeException("Cannot get type description for type: " + clazz.getName(), e);
        }
    }

    private static Type getEngineType(PropertyDescriptor descriptor) {
        Method readMethod = descriptor.getReadMethod();
        Method writeMethod = descriptor.getWriteMethod();
        Class<?> propertyType = descriptor.getPropertyType();
        if (readMethod == null || writeMethod == null || !EngineType.class.isAssignableFrom(propertyType)) {
            return null;
        } else {
            return descriptor.getReadMethod().getGenericReturnType();
        }
    }
}
