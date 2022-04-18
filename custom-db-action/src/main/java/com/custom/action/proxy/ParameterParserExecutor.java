package com.custom.action.proxy;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConst;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExceptionConst;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/15 11:06
 * @Desc：sql自定义参数化解析
 **/
@Slf4j
@SuppressWarnings("unchecked")
public class ParameterParserExecutor {


    /**
    * order=false的预编译sql
    */
    public void prepareDisorderParams() throws Exception {

        // 参数化-前期-预编译 #{name} 替换为 {@name@}
        prepareSqlParamsSymbol();
        // 参数化-直接替换 ${name} 替换为name的值
        replaceSqlSymbol();
        // 参数化-后期-预编译 {@name@} 替换为?
        prepareAfterSqlParamsSymbol();
    }


    /**
    * order=true的预编译sql
     * 只允许基本类型的参数值
    */
    public void prepareOrderParams() {
        for (Object param : params) {
            Class<?> type = param.getClass();
            if (CustomUtil.isBasicType(type)) {
                paramResList.add(param);
            } else
                throw new IllegalArgumentException(String.format("Illegal parameter method: %s.%s(), only basic type parameters are allowed when order = true", method.getDeclaringClass().getName(), method.getName()));
        }

    }



    /**
     * 参数预编译化（后步骤）
     */
    private void prepareAfterSqlParamsSymbol() {
        basicParamsMap.forEach((String k, Object v) ->{
            if(JudgeUtilsAx.isEmpty(v))
                throw new CustomCheckException("Parameter '" + k + "' cannot be empty");
            try {
                JudgeTypeAndSetterSymbolParams(k, v);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }


    /**
    * 判断参数类型，并开始将参数的位置以'?'代替
    */
    // todo... 待优化 将{@name@} 的格式，以正则表达式去判断，现在这种方式比较死板
    private void JudgeTypeAndSetterSymbolParams(String paramName, Object paramValue) throws Exception {

        if(!prepareSql.contains("{@")) return;
//        String regex = ".*@*@*";
//        Matcher matcher = Pattern.compile(regex).matcher(prepareSql);
//        if(!matcher.find()) return;

        String signName = String.format(sign, paramName);
        StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        if (CustomUtil.isBasicType(paramValue.getClass())) {
            paramResList.add(paramValue);
            symbol.add(SymbolConst.QUEST);

        } else if (paramValue instanceof List) {
            List<Object> paramsForList = (List<Object>) paramValue;
            paramsForList = paramsForList.stream().filter(x -> CustomUtil.isBasicType(x.getClass())).collect(Collectors.toList());
            paramsForList.forEach(x -> symbol.add(SymbolConst.QUEST));
            paramResList.addAll(paramsForList);

        } else if (paramValue instanceof Map) {
            Map<?,?> paramsForMap = (Map<?,?>) paramValue;
            paramsForMap.forEach((k, v) -> {
                try {
                    JudgeTypeAndSetterSymbolParams(String.format("%s.%s", paramName, k), v);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });

        }else if (paramValue.getClass().isArray()) {
            int length = Array.getLength(paramValue);
            for (int j = 0; j < length; j++) {
                symbol.add(SymbolConst.QUEST);
                paramResList.add(Array.get(paramValue, j));
            }

        }
        else if (paramValue instanceof Set) {
            Set<Object> paramsForSet = (Set<Object>) paramValue;
            paramsForSet = paramsForSet.stream().filter(x -> CustomUtil.isBasicType(x.getClass())).collect(Collectors.toSet());
            paramsForSet.forEach(x -> symbol.add(SymbolConst.QUEST));
            paramResList.addAll(paramsForSet);
        }
        // 最终只剩下自定义的实体类
        else {
            Field[] fields = CustomUtil.getFields(paramValue.getClass());
            List<String> fieldNames = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
//            List<Object> fieldVales = new DbParserFieldHandler().getFieldsVal(paramValue, fieldNames.toArray(new String[0]));
            for (int j = 0; j < fieldNames.size(); j++) {
                String fieldName = fieldNames.get(j);
//                Object fieldValue = fieldVales.get(j);
//                JudgeTypeAndSetterSymbolParams(String.format("%s.%s", paramName, fieldName), fieldValue);
            }
        }
        prepareSql = prepareSql.replace(signName, symbol.toString());
    }


    /**
    * 自定义sql参数提取（前步骤）
    */
    private void prepareSqlParamsSymbol() throws Exception {
        for (int i = 0; i < methodParameters.length; i++) {
            String paramName = methodParameters[i].getName();
            Object paramValue = params[i];
            handleParamMaps(paramName, paramValue);
        }
        int index = 0;
        // todo... 待优化 以正则表达式的格式来匹配参数的位置，以便替换
        while (true) {
            int[] indexes = CustomUtil.replaceSqlRex(prepareSql, SymbolConst.PREPARE_BEGIN_REX_1, SymbolConst.PREPARE_END_REX, index);
            if (indexes == null) break;

            String prepareName = prepareSql.substring(indexes[0] + 2, indexes[1]);
            if (JudgeUtilsAx.isBlank(prepareName))
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_NAME, prepareName, sql));

            if (JudgeUtilsAx.isEmpty(paramsMap.get(String.format(sign, prepareName)))) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_VALUE, prepareName, sql));
            }

            if(basicParamsMap.get(prepareName) == null) {
                basicParamsMap.put(prepareName, paramsMap.get(String.format(sign, prepareName)));
            }

            String param = prepareSql.substring(indexes[0], indexes[1] + 1);
            String signParam = String.format(sign, prepareName);
            prepareSql = prepareSql.replace(param, signParam);
            index = indexes[2] - prepareName.length() - 2;
        }
    }



    /**
    * 处理参数映射
    */
    private void handleParamMaps(String paramName, Object paramValue) throws Exception {

        if(JudgeUtilsAx.isEmpty(paramValue)) throw new CustomCheckException(String.format("Parameter '%s' cannot be empty", paramName));
        if(CustomUtil.isBasicType(paramValue.getClass())
            || paramValue instanceof List || paramValue instanceof Set || paramValue.getClass().isArray()) {
            paramsMap.put(String.format(sign, paramName), paramValue);

        } else if(paramValue instanceof Map) {
            Map<String, Object> objectMap = (Map<String, Object>) paramValue;
            objectMap.forEach((k, v) ->{
                try {
                    handleParamMaps(String.format("%s.%s", paramName, k), v);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });

        }else {
            Field[] fields = CustomUtil.getFields(paramValue.getClass());
            List<String> fieldNames = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
//            List<Object> fieldVales = new DbParserFieldHandler().getFieldsVal(paramValue, fieldNames.toArray(new String[0]));
            for (int j = 0; j < fieldNames.size(); j++) {
                String fieldName = fieldNames.get(j);
//                Object fieldValue = fieldVales.get(j);
//                handleParamMaps(String.format("%s.%s", paramName, fieldName), fieldValue);
            }
        }
    }

    /**
     * 将${name} 替换为 `name`的值
     */
    private void replaceSqlSymbol() {

        if(!prepareSql.contains(SymbolConst.PREPARE_BEGIN_REX_2)) {
            return;
        }
        // todo... 待优化 以正则表达式的格式来匹配参数的位置，以便替换
        int index = 0;
        while (true){
            int[] indexes = CustomUtil.replaceSqlRex(prepareSql, SymbolConst.PREPARE_BEGIN_REX_2, SymbolConst.PREPARE_END_REX, index);
            if (indexes == null) break;
            String name = prepareSql.substring(indexes[0] + 2, indexes[1]);
            Object paramName = paramsMap.get(String.format(sign, name));
            if(JudgeUtilsAx.isEmpty(paramName)) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_PARAMS_NAME, name, sql));
            }
            prepareSql = prepareSql.replace(prepareSql.substring(indexes[0], indexes[1] + 1), paramName.toString());
        }
    }


    private Method method;

    private String prepareSql;

    private String sql;

    private List<Object> paramResList;

    private Map<String, Object> paramsMap;

    private Map<String, Object> basicParamsMap;

    private Parameter[] methodParameters;

    private Object[] params;

    private final String sign = "{@%s@}";

    public ParameterParserExecutor(String prepareSql, Method method, Object[] params){
        this.method = method;
        this.prepareSql = prepareSql;
        this.sql = prepareSql;
        this.methodParameters = method.getParameters();
        this.params = params;
        this.paramResList = new ArrayList<>();
        this.paramsMap = new HashMap<>();
        this.basicParamsMap = new LinkedHashMap<>();
    }

    public String getPrepareSql() {
        return prepareSql;
    }

    public List<Object> getParamResList() {
        return paramResList;
    }
}
