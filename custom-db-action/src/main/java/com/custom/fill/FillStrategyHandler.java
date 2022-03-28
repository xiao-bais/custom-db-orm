//package com.custom.fill;
//
//import com.custom.comm.JudgeUtilsAx;
//import com.custom.dbconfig.DbCustomStrategy;
//import com.custom.dbconfig.SymbolConst;
//import com.custom.sqlparser.TableInfoCache;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.ObjectUtils;
//
//import java.util.List;
//
///**
// * @Author Xiao-Bai
// * @Date 2022/3/21 15:58
// * @Desc：sql字段自动填充处理
// * 若在容器中发现了自动填充的实现类，则装配，否则自动填充无效，忽略自动填充配置
// **/
//@Slf4j
////@Component
//public class FillStrategyHandler implements  ApplicationContextAware {
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//
//        AutoFillColumnHandler autoFillColumnHandler = null;
//        try {
//            autoFillColumnHandler = applicationContext.getBean(AutoFillColumnHandler.class);
//        }catch (NoSuchBeanDefinitionException e) {
//            log.error(e.getMessage(), e);
//            log.warn("Invalid autofill configuration...");
//        }
//        if(ObjectUtils.isEmpty(autoFillColumnHandler)) return;
//
//        List<TableFillObject> tableAutoUpdateObjects = autoFillColumnHandler.fillStrategy();
//        if (CollectionUtils.isEmpty(tableAutoUpdateObjects)) return;
//
//        for (TableFillObject autoUpdateObject : tableAutoUpdateObjects) {
//            if(!ObjectUtils.isEmpty(autoUpdateObject) && !ObjectUtils.isEmpty(autoUpdateObject.getTableFillMapper())) {
//                if(ObjectUtils.isEmpty(autoUpdateObject.getEntityClass())) {
//                    TableInfoCache.setTableFill(SymbolConst.NORMAL, autoUpdateObject);
//                }else {
//                    TableInfoCache.setTableFill(autoUpdateObject.getEntityClass().getName(), autoUpdateObject);
//                }
//            }
//        }
//    }
//}
