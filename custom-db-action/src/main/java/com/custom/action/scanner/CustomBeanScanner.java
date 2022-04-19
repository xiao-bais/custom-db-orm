package com.custom.action.scanner;

import com.custom.comm.BasicDao;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConstant;
import com.custom.comm.annotations.DbTable;
import com.custom.comm.annotations.mapper.SqlMapper;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExceptionConst;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/22 14:16
 * @Desc：将指定路径下的类扫描出来
 **/
@Slf4j
public class CustomBeanScanner {

    /**
     * 所有需要注册的bean集合
     */
    private final Set<Class<?>> beanRegisterSet = new HashSet<>();

    /**
     * 类加载器
     */
    private final ClassLoader classLoader = CustomBeanScanner.class.getClassLoader();

    /**
     * 扫描的包
     */
    private String packageScan = SymbolConstant.EMPTY;

    /**
     * 资源路径
     */
    private URL url;

    public CustomBeanScanner(String... packageScans) {
        for (String scan : packageScans) {
            packageScan = scan;
            if (JudgeUtilsAx.isEmpty(packageScan)) {
                continue;
            }
            this.scannerPackage();
        }
    }

    public Set<Class<?>> getBeanRegisterList() {
        return this.beanRegisterSet;
    }


    /**
     * 扫描包
     */
    private void scannerPackage() {
        try {
            url = classLoader.getResource(packageScan.replace(SymbolConstant.POINT, SymbolConstant.SLASH));
            if (url == null) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_URL, packageScan));
            }
            String protocol = url.getProtocol();
            if (SymbolConstant.FILE.equals(protocol)) {
                addLocalClass(packageScan);
            } else if (SymbolConstant.JAVA.equals(protocol)) {
                addJarClass(packageScan);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


    /**
     * 加载本地类
     */
    @SuppressWarnings("unchecked")
    private void addLocalClass(final String packageName) throws URISyntaxException {

        try {
            url = classLoader.getResource(packageName.replace(SymbolConstant.POINT, SymbolConstant.SLASH));
            if (url == null) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_URL, packageScan));
            }
            URI uri = url.toURI();
            File classFile = new File(uri);
            classFile.listFiles(pathName -> {
                if (pathName.isDirectory()) {
                    try {
                        addLocalClass(packageName + SymbolConstant.POINT + pathName.getName());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                if (pathName.getName().endsWith(SymbolConstant.CLASS)) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(packageName + SymbolConstant.POINT + pathName.getName().replace(SymbolConstant.CLASS, SymbolConstant.EMPTY));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (clazz != null && (
                            BasicDao.class.isAssignableFrom(clazz)
                            || clazz.isAnnotationPresent(SqlMapper.class)
                            || clazz.isAnnotationPresent(DbTable.class))
                    ) beanRegisterSet.add(clazz);

                    return true;
                }
                return false;
            });
        } catch (URISyntaxException e) {
            log.error(String.format(ExceptionConst.EX_NOT_FOUND_URL, packageScan));
            throw e;
        }
    }

    /**
     * 加载jar包中的类
     */
    @SuppressWarnings("unchecked")
    private void addJarClass(final String packageName) throws IOException {
        if (JudgeUtilsAx.isEmpty(packageName)) return;
        String pathName = packageName.replace(SymbolConstant.POINT, SymbolConstant.SLASH);
        JarFile jarFile = null;

        url = classLoader.getResource(packageName);
        if (url == null) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_URL, packageScan));
        }
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        while (jarEntryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = jarEntryEnumeration.nextElement();
            String jarEntryName = jarEntry.getName();

            if (jarEntryName.contains(pathName) && !jarEntryName.equals(pathName + SymbolConstant.SLASH)) {
                if (jarEntry.isDirectory()) {
                    String beanClassName = jarEntry.getName().replace(SymbolConstant.SLASH, SymbolConstant.POINT);
                    int endIndex = beanClassName.lastIndexOf(SymbolConstant.POINT);
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = beanClassName.substring(0, endIndex);
                    }
                    addJarClass(prefix);
                    if (jarEntry.getName().endsWith(SymbolConstant.CLASS)) {
                        Class<?> beanClass = null;

                        try {
                            beanClass = classLoader.loadClass(jarEntry.getName().replace(SymbolConstant.SLASH, SymbolConstant.POINT).replace(SymbolConstant.CLASS, SymbolConstant.EMPTY));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (beanClass != null && (
                                BasicDao.class.isAssignableFrom(beanClass)
                                        || beanClass.isAnnotationPresent(SqlMapper.class)
                                        || beanClass.isAnnotationPresent(DbTable.class))
                        ) beanRegisterSet.add(beanClass);

                    }
                }
            }
        }

    }

}
