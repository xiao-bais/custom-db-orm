package com.custom.action.wrapper;

import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/21 0021 14:44
 * @Desc 全等条件解析处理
 */
public class AllEqualConditionHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(AllEqualConditionHandler.class);

    private final Object entity;
    private final Map<String, String> fieldMapper;
    private final DefaultConditionWrapper<T> conditionWrapper;

    public AllEqualConditionHandler(Object entity, Map<String, String> fieldMapper, DefaultConditionWrapper<T> conditionWrapper) {
        this.entity = entity;
        this.fieldMapper = fieldMapper;
        this.conditionWrapper = conditionWrapper;
    }

    public void allEqCondition() {
        Map<String, Object> parseMap = new HashMap<>();
        try {
            parseMap = CustomUtil.beanToMap(entity);
        } catch (IntrospectionException e) {
            logger.error("There is a problem with the current entity, please check");
            ExThrowsUtil.toCustom(e.getMessage());
        }
        parseMap.forEach((key, value) -> {
            if (fieldMapper.containsKey(key) && Objects.nonNull(value)) {
                conditionWrapper.eq(fieldMapper.get(key), value);
            }
        });
    }




}
