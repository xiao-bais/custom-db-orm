package com.custom.jdbc.handler;

import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author  Xiao-Bai
 * @since  2022/11/15 23:16
 * 映射目标缓存
 */
public class MappedTargetCache<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<FieldCache> fieldSet;

    public MappedTargetCache(Class<T> targetClass) {
        this.fieldSet = new HashSet<>();

        try {
            List<PropertyDescriptor> descriptorList = ReflectUtil.getProperties(targetClass);
            for (PropertyDescriptor descriptor : descriptorList) {
                FieldCache fieldCache = new FieldCache(descriptor);
                this.fieldSet.add(fieldCache);
            }
        } catch (IntrospectionException e) {
            logger.error(e.toString(), e);
        }

    }

    public FieldCache findForName(String fieldName) {
        return fieldSet.stream().filter(op -> fieldName.equals(op.fieldName)).findFirst().orElse(null);
    }



    /**
     * 内部子类，只用于缓存字段信息
     */
    static final class FieldCache {

        private final String fieldName;
        private final boolean baseType;
        private final PropertyDescriptor descriptor;
        private final TypeHandler<?> typeHandler;

        public FieldCache(PropertyDescriptor descriptor) {
            this.descriptor = descriptor;
            this.fieldName = descriptor.getName();
            Class<?> fieldType = descriptor.getPropertyType();
            this.baseType = CustomUtil.isBasicClass(fieldType);
            this.typeHandler = ResultSetTypeMappedHandler.getTargetTypeHandler(fieldType).getClone();
        }


        public String getFieldName() {
            return fieldName;
        }

        public boolean isBaseType() {
            return baseType;
        }

        public PropertyDescriptor getDescriptor() {
            return descriptor;
        }

        public TypeHandler<?> getTypeHandler() {
            return typeHandler;
        }
    }



}
