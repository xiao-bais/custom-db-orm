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
public class FreemarkerTemplateStructs {

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerTemplateStructs.class);

    private final static String TEMPLATE_PATH = "src/main/resources/templates";
//    private final static String CLASS_PATH = "src/main/java/com/home/shop";

    /**
     * 开始创建实体类
     */
    public void buildEntity(TableStructModel tableStructModel) {

        // 创建配置实例
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        Writer writer = null;
        String basePath = System.getProperty("user.dir");

        try {
            // 配置模板路径
            configuration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
            configuration.setDirectoryForTemplateLoading(new File(basePath + "\\custom-db-generator\\" + TEMPLATE_PATH));
            Template template = configuration.getTemplate("EntityTemplate.ftl");

            File parentPackage = new File(basePath + SymbolConstant.FILE_SEPARATOR + tableStructModel.getEntityClassPath());
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
     * 开始创建业务类
     */
    public void buildService(ServiceStructModel serviceStructModel) {

        // 创建配置实例
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_20);
        Writer writer = null;
        String basePath = System.getProperty("user.dir");

        try {
            // 配置模板路径
            configuration.setDirectoryForTemplateLoading(new File(basePath +  "\\custom-db-generator" + SymbolConstant.FILE_SEPARATOR + TEMPLATE_PATH));
            Template template = configuration.getTemplate("ServiceTemplate.ftl");

            File parentPackage = new File(basePath + SymbolConstant.FILE_SEPARATOR + serviceStructModel.getServiceClassPath());
            if(!parentPackage.exists()) {
                parentPackage.mkdirs();
            }


            // service
            File serviceFile = new File(parentPackage.getPath() + SymbolConstant.FILE_SEPARATOR, serviceStructModel.getServiceName() + SymbolConstant.DOT_JAVA);
            if(serviceFile.exists() && serviceStructModel.getOverrideEnable()) {
                serviceFile.delete();
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceFile)));
            template.process(serviceStructModel, writer);

            logger.info("生成业务类：{}.{}", serviceStructModel.getSourcePackage(), serviceStructModel.getServiceName());

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
     * 开始创建业务实现类
     */
    public void buildServiceImpl(ServiceStructModel serviceStructModel) {

        // 创建配置实例
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_20);
        Writer writer = null;
        String basePath = System.getProperty("user.dir");

        try {
            // 配置模板路径
            configuration.setDirectoryForTemplateLoading(new File(basePath +  "\\custom-db-generator" + SymbolConstant.FILE_SEPARATOR + TEMPLATE_PATH));
            Template template = configuration.getTemplate("ServiceImplTemplate.ftl");

            File parentPackage = new File(basePath + SymbolConstant.FILE_SEPARATOR + serviceStructModel.getServiceImplClassPath());
            if(!parentPackage.exists()) {
                parentPackage.mkdirs();
            }

            // serviceImpl
            File serviceImplFile = new File(parentPackage.getPath(), serviceStructModel.getServiceImplName() + SymbolConstant.DOT_JAVA);
            if(serviceImplFile.exists() && serviceStructModel.getOverrideEnable()) {
                serviceImplFile.delete();
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceImplFile)));
            template.process(serviceStructModel, writer);

            logger.info("生成业务类：{}.{}", serviceStructModel.getSourcePackage(), serviceStructModel.getServiceImplName());

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
