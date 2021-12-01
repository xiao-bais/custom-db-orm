package com.custom.scanner;

import com.custom.comm.BasicDao;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
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
 * @Desc：jdbc映射层的接口扫描
 **/
@Slf4j
public class MapperBeanScanner {

    /**
    * 所有需要注册的bean集合
    */
    private Set<Class<?>> beanRegisterSet = new HashSet<>();

    /**
    * 类加载器
    */
    private ClassLoader classLoader = MapperBeanScanner.class.getClassLoader();

    /**
    * 扫描的包
    */
    private String packageScan = SymbolConst.EMPTY;

    /**
    * 资源路径
    */
    private URL url;

    public MapperBeanScanner(String... packageScans) {
        for (String scan : packageScans) {
            packageScan = scan;
            if (JudgeUtilsAx.isEmpty(packageScan)) {
                continue;
            }
            this.scannerPackage();
        }
    }

    public Set<Class<?>> getBeanRegisterList(){
        return this.beanRegisterSet;
    }


    /**
    * 扫描包
    */
    private void scannerPackage() {
        try {
            url = classLoader.getResource(packageScan.replace(SymbolConst.POINT,SymbolConst.SLASH));
            if(url == null) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_URL, packageScan));
            }
            String protocol = url.getProtocol();
            if(SymbolConst.FILE.equals(protocol)) {
                addLocalClass(packageScan);
            }else if(SymbolConst.JAVA.equals(protocol)) {
                addJarClass(packageScan);
            }
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


    /**
    * 加载本地类
    */
    @SuppressWarnings("unchecked")
    private void addLocalClass(final String packageName) throws URISyntaxException {

        try {
            url = classLoader.getResource(packageName.replace(SymbolConst.POINT, SymbolConst.SLASH));
            if(url == null) {
                throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_URL, packageScan));
            }
            URI uri = url.toURI();
            File classFile = new File(uri);
            classFile.listFiles(pathName -> {
                if(pathName.isDirectory()) {
                    try {
                        addLocalClass(packageName + SymbolConst.POINT + pathName.getName());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                if(pathName.getName().endsWith(SymbolConst.CLASS)) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(packageName + SymbolConst.POINT + pathName.getName().replace(SymbolConst.CLASS, SymbolConst.EMPTY));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(clazz != null && BasicDao.class.isAssignableFrom(clazz)) {
                        beanRegisterSet.add(clazz);
                    }
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
        if(JudgeUtilsAx.isEmpty(packageName)) return;
        String pathName = packageName.replace(SymbolConst.POINT, SymbolConst.SLASH);
        JarFile jarFile = null;

        url = classLoader.getResource(packageName);
        if(url == null) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_NOT_FOUND_URL, packageScan));
        }
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        while (jarEntryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = jarEntryEnumeration.nextElement();
            String jarEntryName = jarEntry.getName();

            if(jarEntryName.contains(pathName) && !jarEntryName.equals(pathName + SymbolConst.SLASH)) {
                if(jarEntry.isDirectory()) {
                    String beanClassName = jarEntry.getName().replace(SymbolConst.SLASH, SymbolConst.POINT);
                    int endIndex = beanClassName.lastIndexOf(SymbolConst.POINT);
                    String prefix = null;
                    if(endIndex > 0) {
                        prefix = beanClassName.substring(0, endIndex);
                    }
                    addJarClass(prefix);
                    if(jarEntry.getName().endsWith(SymbolConst.CLASS)) {
                        Class<?> beanClass = null;

                        try {
                            beanClass = classLoader.loadClass(jarEntry.getName().replace(SymbolConst.SLASH, SymbolConst.POINT).replace(SymbolConst.CLASS, SymbolConst.EMPTY));
                        }catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if(beanClass != null && BasicDao.class.isAssignableFrom(beanClass)) {
                            beanRegisterSet.add(beanClass);
                        }
                    }
                }
            }
        }

    }

}
