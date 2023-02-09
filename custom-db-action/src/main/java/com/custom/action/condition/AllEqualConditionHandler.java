package com.custom.action.condition;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 全等条件解析处理
 * @author   Xiao-Bai
 * @since  2022/7/21 0021 14:44
 */
public class AllEqualConditionHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(AllEqualConditionHandler.class);

    private final Object entity;
    private final Map<String, String> fieldMapper;
    private final DefaultConditionWrapper<T> conditionWrapper;

    public AllEqualConditionHandler(T entity, DefaultConditionWrapper<T> conditionWrapper) {
        this.entity = entity;
        this.conditionWrapper = conditionWrapper;
        this.fieldMapper = conditionWrapper.getTableSupport().fieldMap();
    }

    /**
     * 条件拼接
     */
    public void allExecEqCondition() {
        try {
            Map<String, Object> parseMap = ReflectUtil.beanToMap(entity);
            parseMap.forEach((key, value) -> {
                if (fieldMapper.containsKey(key) && Objects.nonNull(value)) {
                    conditionWrapper.eq(fieldMapper.get(key), value);
                }
            });
        } catch (IntrospectionException e) {
            logger.error("There is a problem with the current entity, please check");
            throw new CustomCheckException(e.getMessage());
        }
    }




}
