package mybatis.sharding.quickstart.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.SystemPropertyUtils;

import lombok.extern.slf4j.Slf4j;
import mybatis.sharding.quickstart.context.ShardMethodFactory;


@Slf4j
public class PackageUtil {

    static char DIAN = '.';
    static String STRING_DIAN = ".";

    protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static Set<Method> findPackageInnerShardMethodsAndRegist(String scanPackages, Class<? extends Annotation> annotation) {
        return findPackageInnerShardMethodsAndRegist(scanPackages,annotation,true);
    }
    
    public static Set<Method> findPackageInnerShardMethodsAndRegist(String scanPackages, Class<? extends Annotation> annotation,boolean check) {
        // 获取所有的类
        Set<String> clazzSet = findPackageClass(scanPackages);
        Set<Method> methods = new HashSet<Method>();
        // 遍历类，查询相应的annotation方法
        for (String clazz : clazzSet) {
            try {
                Set<Method> ms = findShardMethodsAndRegist(clazz, annotation,check);
                if (ms != null) {
                    methods.addAll(ms);
                }
            }
            catch (ClassNotFoundException ignore) {
            }
        }
        return methods;
    }


    /**
     * 根据扫描包的,查询下面的所有类
     *
     * @param scanPackages
     *            扫描的package路径
     * @return
     */
    public static Set<String> findPackageClass(String scanPackages) {
        if (StringUtils.isBlank(scanPackages)) {
            return Collections.emptySet();
        }
        // 验证及排重包路径,避免父子路径多次扫描
        Set<String> packages = checkPackage(scanPackages);
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory =
                new CachingMetadataReaderFactory(resourcePatternResolver);
        Set<String> clazzSet = new HashSet<String>();
        for (String basePackage : packages) {
            if (StringUtils.isBlank(basePackage)) {
                continue;
            }
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils
                        .convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage))
                    + "/" + DEFAULT_RESOURCE_PATTERN;
            try {
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    // 检查resource，这里的resource都是class
                    String clazz = loadClassName(metadataReaderFactory, resource);
                    clazzSet.add(clazz);
                }
            }
            catch (Exception e) {
                log.error("获取包内shard方法失败,package:" + basePackage, e);
            }
        }
        return clazzSet;
    }


    /**
     * 排重、检测package父子关系，避免多次扫描
     *
     * @param scanPackages
     * @return 返回检查后有效的路径集合
     */
    private static Set<String> checkPackage(String scanPackages) {
        if (StringUtils.isBlank(scanPackages)) {
            return Collections.emptySet();
        }
        Set<String> packages = new HashSet<String>();
        // 排重路径
        Collections.addAll(packages, scanPackages.split(","));
        for (String pInArr : packages.toArray(new String[packages.size()])) {
            if (StringUtils.isBlank(pInArr) || pInArr.equals(STRING_DIAN) || pInArr.startsWith(STRING_DIAN)) {
                continue;
            }
            if (pInArr.endsWith(STRING_DIAN)) {
                pInArr = pInArr.substring(0, pInArr.length() - 1);
            }
            Iterator<String> packageIte = packages.iterator();
            boolean needAdd = true;
            while (packageIte.hasNext()) {
                String pack = packageIte.next();
                if (pInArr.startsWith(pack + DIAN)) {
                    // 如果待加入的路径是已经加入的pack的子集，不加入
                    needAdd = false;
                }
                else if (pack.startsWith(pInArr + DIAN)) {
                    // 如果待加入的路径是已经加入的pack的父集，删除已加入的pack
                    packageIte.remove();
                }
            }
            if (needAdd) {
                packages.add(pInArr);
            }
        }
        return packages;
    }


    /**
     * 加载资源，根据resource获取className
     *
     * @param metadataReaderFactory
     *            spring中用来读取resource为class的工具
     * @param resource
     *            这里的资源就是一个Class
     * @throws IOException
     */
    private static String loadClassName(MetadataReaderFactory metadataReaderFactory, Resource resource)
            throws IOException {
        try {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (metadataReader != null) {
                    return metadataReader.getClassMetadata().getClassName();
                }
            }
        }
        catch (Exception e) {
            log.error("根据resource获取类名称失败", e);
        }
        return null;
    }


    //
    public static Set<Method> findShardMethodsAndRegist(String fullClassName,Class<? extends Annotation> anno,boolean check) throws ClassNotFoundException {
        Set<Method> methodSet = new HashSet<Method>();
        Class<?> clz = Class.forName(fullClassName);
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                continue;
            }
            Annotation annotation = method.getAnnotation(anno);
            
            String statementId = fullClassName + DIAN + method.getName();
            
            if (annotation != null) {
                if (check) {
                    Method checkExists = ShardMethodFactory.getStatementMethodById(statementId);
                    if (checkExists!=null) {
                        throw new IllegalArgumentException(" duplicate key : "+statementId);
                    }
                }
                ShardMethodFactory.regMethod(statementId, method);
                methodSet.add(method);
            }
        }
        return methodSet;
    }

}