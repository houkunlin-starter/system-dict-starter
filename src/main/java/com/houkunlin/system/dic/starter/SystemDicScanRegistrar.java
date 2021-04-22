package com.houkunlin.system.dic.starter;

import com.houkunlin.system.dic.starter.json.DicType;
import com.houkunlin.system.dic.starter.provider.SystemDicProvider;
import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 系统数据字典自动扫描
 *
 * @author HouKunLin
 */
public class SystemDicScanRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanFactoryAware {
    private static final Logger logger = LoggerFactory.getLogger(SystemDicScanRegistrar.class);
    private final ClassPathScanningCandidateComponentProvider provider;
    private BeanFactory beanFactory;
    private ClassLoader classLoader;
    private SystemDicProvider systemDicProvider;
    private String applicationName;

    public SystemDicScanRegistrar() {
        provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(IDicEnums.class));
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.classLoader = resourceLoader.getClassLoader();
        assert this.classLoader != null;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata annotationMetadata, @NonNull BeanDefinitionRegistry registry) {
        final Environment environment = beanFactory.getBean(Environment.class);
        this.applicationName = environment.getProperty("spring.application.name", "default-app");
        this.systemDicProvider = beanFactory.getBean(SystemDicProvider.class);
        Set<String> packagesToScan = getPackagesToScan(annotationMetadata);
        packagesToScan.forEach(this::scanPackage);
    }

    /**
     * 扫描指定包路径下的系统数据字典
     *
     * @param basePackage 包名
     */
    private void scanPackage(String basePackage) {
        final Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        for (BeanDefinition component : components) {
            try {
                final Class<?> loadClass = classLoader.loadClass(component.getBeanClassName());
                if (loadClass.isEnum()) {
                    handleDic(loadClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理系统数据字典对象
     *
     * @param dicClass 字典对象
     */
    private void handleDic(Class<?> dicClass) {
        final DicType annotation = dicClass.getDeclaredAnnotation(DicType.class);
        String dicType;
        String dicTitle;
        if (annotation != null) {
            dicType = annotation.value();
            if (annotation.comment().isBlank()) {
                dicTitle = dicClass.getSimpleName();
            } else {
                dicTitle = annotation.comment();
            }
        } else {
            dicType = dicClass.getSimpleName();
            dicTitle = dicClass.getSimpleName();
        }

        List<DicValueVo<? extends Serializable>> list = new ArrayList<>();
        final IDicEnums<?>[] enumConstants = (IDicEnums<?>[]) dicClass.getEnumConstants();
        for (IDicEnums<?> enums : enumConstants) {
            if (logger.isDebugEnabled()) {
                logger.debug("class {} : - {} - {} - {} - {}", dicClass.getName(), dicType, enums.getValue(), enums.getTitle(), enums);
            }
            list.add(new DicValueVo<>(dicType, enums.getValue(), enums.getTitle(), null, 0));
        }
        final DicTypeVo dicTypeVo = new DicTypeVo(dicTitle, dicType, "From Application: " + applicationName, list);
        systemDicProvider.addDic(dicTypeVo);
    }

    /**
     * 获得需要扫描的包列表
     *
     * @param metadata 注解元数据
     * @return 包列表，至少包含一个默认的 com.pension.system 包路径
     */
    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(SystemDicScan.class.getName()));
        if (attributes == null) {
            return Collections.emptySet();
        }
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        Set<String> packagesToScan = new LinkedHashSet<>(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        if (packagesToScan.isEmpty()) {
            packagesToScan.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        // 默认都添加系统管理模块的扫描路径
        packagesToScan.add("com.system.dic");
        return packagesToScan;
    }
}
