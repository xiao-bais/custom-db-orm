package com.custom.generator;

import com.custom.comm.SymbolConstant;
import com.custom.generator.model.ServiceStructModel;
import com.custom.generator.model.TableStructModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/20 10:14
 * @Desc：
 **/
public class FreemarkerUtil {

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerUtil.class);

    private final static String TEMPLATE_PATH = "src/main/java/com/custom/generator/templates";
    private final static String CLASS_PATH = "src/main/java/com/home/shop";

    /**
     * 开始创建实体类
     */
    public static void buildEntity(TableStructModel tableStructModel) {

        // 创建配置实例
        Configuration configuration = new Configuration();
        Writer writer = null;
        String basePath = System.getProperty("user.dir");

        try {
            // 配置模板路径
            configuration.setDirectoryForTemplateLoading(new File(basePath +  "\\custom-db-generator" + SymbolConstant.FILE_SEPARATOR + TEMPLATE_PATH));
            Template template = configuration.getTemplate("EntityTemplate.ftl");

            File parentPackage = new File(basePath + "\\custom-test\\" + CLASS_PATH + SymbolConstant.FILE_SEPARATOR + tableStructModel.getEntityPackage());
            if(!parentPackage.exists()) {
                parentPackage.mkdirs();
            }
            File javaFile = new File(parentPackage.getPath() + SymbolConstant.FILE_SEPARATOR + tableStructModel.getEntityName() + SymbolConstant.DOT_JAVA);
            if(javaFile.exists() && tableStructModel.getOverrideEnable()) {
                javaFile.delete();
            }

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(javaFile)));
            template.process(tableStructModel, writer);

            logger.info("生成实体：{}.{}", tableStructModel.getSourcePackage(), tableStructModel.getEntityName());

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }finally {
            if(writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    /**
     * 开始创建实体类
     */
    public static void buildService(ServiceStructModel serviceStructModel) {

        // 创建配置实例
        Configuration configuration = new Configuration();
        Writer writer = null;
        String basePath = System.getProperty("user.dir");

        try {
            // 配置模板路径
            configuration.setDirectoryForTemplateLoading(new File(basePath +  "\\custom-db-generator" + SymbolConstant.FILE_SEPARATOR + TEMPLATE_PATH));
            Template template = configuration.getTemplate("ServiceTemplate.ftl");

            File parentPackage = new File(basePath + "\\custom-test\\" + CLASS_PATH + SymbolConstant.FILE_SEPARATOR + serviceStructModel.getServicePackage());
            if(!parentPackage.exists()) {
                parentPackage.mkdirs();
            }
            File javaFile = new File(parentPackage.getPath() + SymbolConstant.FILE_SEPARATOR + serviceStructModel.getClassName() + SymbolConstant.DOT_JAVA);
            if(javaFile.exists() && tableStructModel.getOverrideEnable()) {
                javaFile.delete();
            }

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(javaFile)));
            template.process(tableStructModel, writer);

            logger.info("生成实体：{}.{}", serviceStructModel.getSourcePackage(), serviceStructModel.getClassName());

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }finally {
            if(writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
