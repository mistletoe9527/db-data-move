package com.potlid.scanner;

import com.potlid.common.define.Transfer;
import com.potlid.handler.AnnotationHandler;
import com.potlid.handler.DefaultAnnotationHandler;
import com.potlid.manager.TransferJobManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by styb on 2018/4/2.
 */
public class ClassScanner {
    private static final Logger logger = Logger.getLogger(ClassScanner.class);
    private static final String RESOURCE_PATTERN = "**/%s/**/*.class";
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static ClassScanner classScanner=new ClassScanner();
    private ClassScanner(){}
    public static ClassScanner getScanner(){
        return ClassScanner.classScanner;
    }
    public final Set<Class<?>> scan(String[] confPkgs, Class<? extends Annotation>... annotationTags){
        Set<Class<?>> resClazzSet = new HashSet<Class<?>>();
        List<AnnotationTypeFilter> typeFilters = new LinkedList<AnnotationTypeFilter>();
        if (ArrayUtils.isNotEmpty(annotationTags)){
            for (Class<? extends Annotation> annotation : annotationTags) {
                typeFilters.add(new AnnotationTypeFilter(annotation, false));
            }
        }
        if (ArrayUtils.isNotEmpty(confPkgs)) {
            for (String pkg : confPkgs) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX  + String.format(RESOURCE_PATTERN, ClassUtils.convertClassNameToResourcePath(pkg));
                try {
                    Resource[] resources = resourcePatternResolver.getResources(pattern);
                    MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                    for (Resource resource : resources) {
                        if (resource.isReadable()) {
                            MetadataReader reader = readerFactory.getMetadataReader(resource);
                            String className = reader.getClassMetadata().getClassName();
                            if (ifMatchesEntityType(reader, readerFactory,typeFilters)) {
                                Class<?> curClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                                resClazzSet.add(curClass);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("error in scan "+StringUtils.join(typeFilters, ","),e);
                }
            }
        }
        return resClazzSet;
    }


    public final void scan(ApplicationContext applicationContext,Class<? extends Annotation> annoClass) throws Exception{
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(annoClass);
        if(!CollectionUtils.isEmpty(beanMap)){
            logger.info("get transfer bean size ="+beanMap.size());
            AnnotationHandler annotationHandler=new DefaultAnnotationHandler();
            List<Object> list=new ArrayList<Object>();
            list.addAll(beanMap.values());
            Collections.sort(list, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    return o1.getClass().getAnnotation(Transfer.class).order()-o2.getClass().getAnnotation(Transfer.class).order();
                }
            });
            for(Object bean:list) {
                //检查并添加任务
                TransferJobManager.getInstance().inspect(annotationHandler.handleAnnotation(bean, null));
            }


        }else{
            logger.info("transfer bean is null!");
        }
    }

    /**
     * 检查当前扫描到的类是否含有任何一个指定的注解标记
     * @param reader
     * @param readerFactory
     * @return ture/false
     */
    private boolean ifMatchesEntityType(MetadataReader reader, MetadataReaderFactory readerFactory,List<AnnotationTypeFilter> typeFilters) {
        if (!CollectionUtils.isEmpty(typeFilters)) {
            for (TypeFilter filter : typeFilters) {
                try {
                    if (filter.match(reader, readerFactory)) {
                        return true;
                    }
                } catch (IOException e) {
                    logger.error("过滤匹配类型时出错 {}"+e.getMessage(),e);
                }
            }
        }
        return false;
    }

}
