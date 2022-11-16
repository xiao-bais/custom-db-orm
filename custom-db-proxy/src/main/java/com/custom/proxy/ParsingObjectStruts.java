package com.custom.proxy;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @date 2022/5/8 19:46
 * @desc:sql参数结构解析
 */
@SuppressWarnings("unchecked")
public class ParsingObjectStruts {

    private final static Logger logger = LoggerFactory.getLogger(ParsingObjectStruts.class);

    private final Map<String, Object> paramsMap = new HashMap<>();

    public void parser(String name, Object value) {
        if (Objects.isNull(value)) {
            paramsMap.put(name, null);
            return;
        }
        if (CustomUtil.isBasicType(value)) {
            paramsMap.put(name, value);
        }
        else if (value.getClass().isArray()) {
            parseArray(name, value);
        }
        else if (value instanceof Collection) {
            if (value instanceof List) {
                parseList(name, value);
            } else if (value instanceof Set) {
                parseSet(name, value);
            } else {
                throw new CustomCheckException("Unsupported data type: " + value.getClass());
            }
        }else if (value instanceof Map) {
            parseMap(name, value);
        }else {
            parseObject(name, value);
        }
    }


    private void parseObject(String name, Object value) {
        if (Objects.isNull(value)) {
            paramsMap.put(name, null);
            return;
        }


        List<Field> fieldList = ReflectUtil.loadFields(value.getClass(), false);
        if (fieldList.isEmpty()) throw new CustomCheckException("In %s, no available attributes were resolved", value.getClass());

        for (Field field : fieldList) {
            String fieldName = formatMapperKey(name, field.getName());
            Object fieldValue = null;

            try {
                fieldValue = ReflectUtil.readFieldValue(value, field.getName());
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }

            if (Objects.isNull(fieldValue)) {
                paramsMap.put(fieldName, null);
                continue;
            }

            Class<?> fieldType = field.getType();
            if (CustomUtil.isBasicClass(fieldType)) {
                paramsMap.put(fieldName, fieldValue);
            }

            else if (Collection.class.isAssignableFrom(fieldType)) {

                if (List.class.isAssignableFrom(fieldType)) {
                    parseList(fieldName, fieldValue);
                }
                else if (Set.class.isAssignableFrom(fieldType)) {
                    parseSet(fieldName, fieldValue);
                }
                else {
                    throw new CustomCheckException("Unsupported data type: " + fieldValue.getClass());
                }
            }

            else if (Map.class.isAssignableFrom(fieldType)) {
                parseMap(fieldName, fieldValue);
            }
        }
    }

    private void parseSet(String name, Object value) {
        if (Objects.isNull(value)) {
            paramsMap.put(name, null);
            return;
        }
        Set<Object> valueSet = (Set<Object>) value;
        parseList(name, new ArrayList<>(valueSet));
    }


    private void parseList(String name, Object value) {
        if (Objects.isNull(value)) {
            paramsMap.put(name, null);
            return;
        }
        // 筛选出只有基础类型(包括包装类型)的值
        List<Object> list = ((List<Object>) value).stream().filter(CustomUtil::isBasicType).collect(Collectors.toList());
        paramsMap.put(name, list);
    }


    private void parseMap(String name, Object value) {
        if (Objects.isNull(value)) {
            paramsMap.put(name, null);
            return;
        }
        Map<Object, Object> paramMap = (Map<Object, Object>) value;
        paramMap.forEach((key, val) -> {
            String tempName = formatMapperKey(name, key);
            if (CustomUtil.isBasicType(val)) {
                paramsMap.put(tempName, val);
            }
            else if (val instanceof Collection) {

                if (val instanceof Map) {
                    parseMap(tempName, val);
                }
                else if (val instanceof List) {
                    parseList(tempName, val);
                }
                else if (val instanceof Set) {
                    parseSet(tempName, val);
                }
                else {
                    throw new CustomCheckException("Unsupported data type: " + val.getClass());
                }
            } else {
                parseObject(tempName, val);
            }
        });
    }

    private String formatMapperKey(String name, Object key) {
        return String.format("%s.%s", name, key);
    }

    private void parseArray(String name, Object value) {
        if (Objects.isNull(value)) {
            return;
        }

        List<Object> list = new ArrayList<>();
        int length = Array.getLength(value);

        for (int i = 0; i < length; i++) {
            list.add(Array.get(value, i));
        }
        parseList(name, list);
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }
}
