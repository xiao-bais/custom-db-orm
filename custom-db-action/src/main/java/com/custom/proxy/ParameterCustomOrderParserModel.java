package com.custom.proxy;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.handler.DbAnnotationsParserHandler;
import com.custom.handler.DbParserFieldHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/15 11:06
 * @Desc：sql参数化解析
 **/
@Slf4j
public class ParameterCustomOrderParserModel {


    /**
    * order=false的预编译sql
    */
    public void prepareDisorderParams() throws Exception {
        // 参数化-直接编译 ${name} 替换为name的值
        replaceSqlSymbol();
        // 参数化-前预编译 #{name} 替换为 @name@
        prepareSqlParamsSymbol();
        // 参数化-后预编译 @name@ 替换为?
        prepareAfterSqlParamsSymbol();
    }


    /**
    * order=true的预编译sql
    */
    public void prepareOrderParams() {
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Object param = params[i];

            if (CustomUtil.isBasicType(type)) {
                paramResList.add(param);
            } else throw new IllegalArgumentException(String.format("Illegal parameter method: %s.%s(), only basic type parameters are allowed when order = true", method.getDeclaringClass().getName(), method.getName()));

        }

    }



    /**
     * 参数预编译化（后步骤）
     */
    private void prepareAfterSqlParamsSymbol() throws Exception {
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            String paramName = methodParameters[i].getName();
            Object paramValue = params[i];
            if(JudgeUtilsAx.isEmpty(paramValue))
                throw new CustomCheckException("params '" + paramName + "' is Empty");
            JudgeParamsType(type, paramName, paramValue);
        }
    }


    @SuppressWarnings("unchecked")
    private void JudgeParamsType(Class<?> type, String paramName, Object paramValue) throws Exception {
        String signName = String.format(sign, paramName);
        if (CustomUtil.isBasicType(type)) {
            paramResList.add(paramValue);

        } else if (type.equals(List.class)) {
            List<Object> paramsForList = (List<Object>) paramValue;
            paramsForList = paramsForList.stream().filter(CustomUtil::isBasicType).collect(Collectors.toList());
            StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
            paramsForList.forEach(x -> symbol.add(SymbolConst.QUEST));
            paramResList.addAll(paramsForList);

        } else if (type.isArray()) {
            int length = Array.getLength(paramValue);
            StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
            for (int j = 0; j < length; j++) {
                symbol.add(SymbolConst.QUEST);
                paramResList.add(Array.get(paramValue, j));
            }

        } else if (type.equals(Set.class)) {
            Set<Object> paramsSet = (Set<Object>) paramValue;
            paramsSet = paramsSet.stream().filter(CustomUtil::isBasicType).collect(Collectors.toSet());
            StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
            paramsSet.forEach(x -> symbol.add(SymbolConst.QUEST));
            paramResList.addAll(paramsSet);
        }
        // 最终只剩下自定义的实体类
        else {
            Field[] fields = CustomUtil.getFields(type);
            String[] fieldNames = (String[]) Arrays.stream(fields).map(Field::getName).toArray();
            List<Object> fieldVales = new DbParserFieldHandler().getFieldsVal(paramValue, Arrays.copyOf(fieldNames, fieldNames.length, String[].class));
            for (int j = 0; j < fieldNames.length; j++) {
                String fieldName = fieldNames[j];
                Object fieldValue = fieldVales.get(j);
                JudgeParamsType(fieldValue.getClass(), String.format("%s.%s", paramName, fieldName), fieldValue);
            }
        }
        prepareSql = prepareSql.replace(signName, SymbolConst.QUEST);
    }


    /**
    * 自定义sql参数提取（前步骤）
    */
    private void prepareSqlParamsSymbol() throws Exception {
        handleParams();
        int index = 0;
        String sql = prepareSql;
        while (true) {
            int[] indexes = CustomUtil.replaceSqlRex(prepareSql, SymbolConst.PREPARE_BEGIN_REX_1, SymbolConst.PREPARE_END_REX, index);
            if (indexes == null) break;

            String prepareName = prepareSql.substring(indexes[0] + 2, indexes[1]);
            if (JudgeUtilsAx.isBlank(prepareName))
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_NAME, prepareName, sql));

            if(prepareName.indexOf(SymbolConst.POINT) > 0) {
                prepareName = prepareName.substring(0, prepareName.indexOf(SymbolConst.POINT));
            }
            if (JudgeUtilsAx.isEmpty(paramsMap.get(prepareName))) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_VALUE, prepareName, sql));
            }
            String param = prepareSql.substring(indexes[0], indexes[1] + 1);
            String signParam = String.format(sign, prepareName);
            prepareSql = prepareSql.replace(param, signParam);
            index = indexes[2] - prepareName.length() - 2;
        }
    }


    //Class<?> cls, String name, Object value
    void handleParams() throws Exception {
        for (int i = 0; i < parameterTypes.length; i++) {
            String paramName = methodParameters[i].getName();
            Object paramValue = params[i];
            paramsMap.put(paramName, paramValue);
//            if(CustomUtil.isBasicType(paramValue)) {
//
//            }else if(paramValue instanceof List) {
//                paramsMap.put(paramName, paramValue);
//            }else if(paramValue instanceof Set) {
//                paramsMap.put(paramName, paramValue);
//            }else if(paramValue instanceof Map) {
//                paramsMap.putAll((Map<String,Object>)paramValue);
//            }else {
//                Field[] fields = CustomUtil.getFields(paramValue.getClass());
//                String[] fieldNames = (String[]) Arrays.stream(fields).map(Field::getName).toArray();
//                List<Object> fieldVales = new DbParserFieldHandler().getFieldsVal(paramValue, Arrays.copyOf(fieldNames, fieldNames.length, String[].class));
//                for (String fieldName : fieldNames) {
//                    handleParams()
//                }
//            }
        }
    }

    /**
     * 将${name} 替换为 `name`的值
     */
    private void replaceSqlSymbol() {

        if(!prepareSql.contains(SymbolConst.PREPARE_BEGIN_REX_2)) {
            return;
        }
        int index = 0;
        while (true){
            int[] indexes = CustomUtil.replaceSqlRex(prepareSql, SymbolConst.PREPARE_BEGIN_REX_2, SymbolConst.PREPARE_END_REX, index);
            if (indexes == null) break;
            String text = prepareSql.substring(indexes[0] + 2, indexes[1]);
            Object param = paramsMap.get(text);
            if(JudgeUtilsAx.isEmpty(param)) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_NAME, text, prepareSql));
            }
            prepareSql = prepareSql.replace(prepareSql.substring(indexes[0], indexes[1] + 1), param.toString());
        }
    }


    private Method method;

    private String prepareSql;

    private List<Object> paramResList;

    private Map<String, Object> paramsMap;

    private Class<?>[] parameterTypes;

    private Parameter[] methodParameters;

    private Object[] params;

    private final String sign = "@%s@";

    public ParameterCustomOrderParserModel(String prepareSql, Method method, Object[] params){
        this.method = method;
        this.prepareSql = prepareSql;
        this.methodParameters = method.getParameters();
        this.parameterTypes = method.getParameterTypes();
        this.params = params;
        this.paramResList = new ArrayList<>();
        this.paramsMap = new HashMap<>();
    }

    public String getPrepareSql() {
        return prepareSql;
    }

    public List<Object> getParamResList() {
        return paramResList;
    }
}
