package io.github.manhnt217.task.task_engine.type;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author manhnguyen
 */
public class TypeDescriptor {

    public static Object getTypeDescription(Class<? extends EngineType> clazz) {
        if (TextType.class.isAssignableFrom(clazz)) {
            return TextType.class.getSimpleName();
        } if (ObjectRef.class.isAssignableFrom(clazz)) {
            return ObjectRef.class.getSimpleName();
        } else { // ObjectType
            return getObjectTypeDescription(clazz);
        }
    }

    private static Map<String, Object> getObjectTypeDescription(Class<? extends EngineType> clazz) {
        Map<String, Object> result = new HashMap<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                Class<? extends EngineType> fieldType = getEngineType(descriptor);
                if (fieldType == null) {
                    continue;
                }
                result.put(descriptor.getName(), getTypeDescription(fieldType));
            }
            return result;
        } catch (IntrospectionException e) {
            throw new RuntimeException("Cannot get type description for type: " + clazz.getName(), e);
        }
    }

    private static Class<? extends EngineType> getEngineType(PropertyDescriptor descriptor) {
        Method readMethod = descriptor.getReadMethod();
        Method writeMethod = descriptor.getWriteMethod();
        Class<?> propertyType = descriptor.getPropertyType();
        if (readMethod == null || writeMethod == null || !EngineType.class.isAssignableFrom(propertyType)) {
            return null;
        } else {
            return (Class<? extends EngineType>) propertyType;
        }
    }
}
