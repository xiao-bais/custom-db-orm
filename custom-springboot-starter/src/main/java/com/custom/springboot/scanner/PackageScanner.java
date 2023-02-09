package com.custom.springboot.scanner;

import com.custom.comm.annotations.DbTable;
import com.custom.comm.annotations.mapper.SqlMapper;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.JudgeUtil;
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
 * @author  Xiao-Bai
 * @since  2021/11/22 14:16
 * @Desc：将指定路径下的类扫描出来
 **/
@Slf4j
public class PackageScanner {

    /**
     * 所有需要注册的bean集合
     */
    private final Set<Class<?>> beanRegisterSet = new HashSet<>();

    /**
     * 类加载器
     */
    private final ClassLoader classLoader = PackageScanner.class.getClassLoader();

    /**
     * 扫描的包
     */
    private String packageScan = Constants.EMPTY;

    /**
     * 资源路径
     */
    private URL url;

    public PackageScanner(String... packageScans) {
        for (String scan : packageScans) {
            packageScan = scan;
            if (JudgeUtil.isEmpty(packageScan)) {
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
            url = classLoader.getResource(packageScan.replace(Constants.POINT, Constants.FILE_SEPARATOR));
            if (url == null) {
                throw new CustomCheckException(String.format("The package url cannot be found：'%s'", packageScan));
            }
            String protocol = url.getProtocol();
            if (Constants.FILE.equals(protocol)) {
                addLocalClass(packageScan);
            } else if (Constants.JAR.equals(protocol)) {
                addJarClass(packageScan);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


    /**
     * 加载本地类
     */
    private void addLocalClass(final String packageName) throws URISyntaxException {

        try {
            url = classLoader.getResource(packageName.replace(Constants.POINT, Constants.FILE_SEPARATOR));
            if (url == null) {
                throw new CustomCheckException(String.format("The package url cannot be found：'%s'", packageScan));
            }
            URI uri = url.toURI();
            File classFile = new File(uri);
            classFile.listFiles(pathName -> {
                if (pathName.isDirectory()) {
                    try {
                        addLocalClass(packageName + Constants.POINT + pathName.getName());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                if (pathName.getName().endsWith(Constants.CLASS)) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(packageName + Constants.POINT + pathName.getName().replace(Constants.CLASS, Constants.EMPTY));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (clazz != null && (clazz.isAnnotationPresent(SqlMapper.class)
                            || clazz.isAnnotationPresent(DbTable.class))
                    ) beanRegisterSet.add(clazz);

                    return true;
                }
                return false;
            });
        } catch (URISyntaxException e) {
            log.error(String.format("The package url cannot be found：'%s'", packageScan));
            throw e;
        }
    }

    /**
     * 加载jar包中的类
     */
    private void addJarClass(final String packageName) throws IOException {
        if (JudgeUtil.isEmpty(packageName)) return;
        String pathName = packageName.replace(Constants.POINT, Constants.FILE_SEPARATOR);
        JarFile jarFile = null;

        url = classLoader.getResource(packageName);
        if (url == null) {
            throw new CustomCheckException(String.format("The package url cannot be found：'%s'", packageScan));
        }
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        while (jarEntryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = jarEntryEnumeration.nextElement();
            String jarEntryName = jarEntry.getName();

            if (jarEntryName.contains(pathName) && !jarEntryName.equals(pathName + Constants.FILE_SEPARATOR)) {
                if (jarEntry.isDirectory()) {
                    String beanClassName = jarEntry.getName().replace(Constants.FILE_SEPARATOR, Constants.POINT);
                    int endIndex = beanClassName.lastIndexOf(Constants.POINT);
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = beanClassName.substring(0, endIndex);
                    }
                    addJarClass(prefix);
                    if (jarEntry.getName().endsWith(Constants.CLASS)) {
                        Class<?> beanClass = null;

                        try {
                            beanClass = classLoader.loadClass(jarEntry.getName().replace(Constants.FILE_SEPARATOR, Constants.POINT).replace(Constants.CLASS, Constants.EMPTY));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (beanClass != null && (beanClass.isAnnotationPresent(SqlMapper.class)
                                        || beanClass.isAnnotationPresent(DbTable.class))
                        ) beanRegisterSet.add(beanClass);

                    }
                }
            }
        }

    }

}
