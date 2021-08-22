package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.javassist.DynamicGenerateConverterImpl;
import com.houkunlin.system.dict.starter.json.DictConverter;
import com.houkunlin.system.dict.starter.json.DictType;
import com.houkunlin.system.dict.starter.provider.SystemDictProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 系统数据字典自动扫描
 *
 * @author HouKunLin
 */
public class SystemDictScanRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanFactoryAware {
    private static final Logger logger = LoggerFactory.getLogger(SystemDictScanRegistrar.class);
    private final ClassPathScanningCandidateComponentProvider provider;
    private final DynamicGenerateConverterImpl generateConverter = new DynamicGenerateConverterImpl();
    private ClassLoader classLoader;
    private SystemDictProvider systemDicProvider;
    private String applicationName;
    private DefaultListableBeanFactory beanFactory;

    public SystemDictScanRegistrar() {
        provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(DictEnum.class));
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
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
        this.systemDicProvider = beanFactory.getBean(SystemDictProvider.class);
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
        final DictType annotation = dicClass.getDeclaredAnnotation(DictType.class);
        final DictConverter dicConverter = dicClass.getDeclaredAnnotation(DictConverter.class);
        if (dicConverter != null) {
            generateConverter.registerBean(beanFactory, dicClass, dicConverter);
        }
        String dicType;
        String dicTitle;
        if (annotation != null) {
            dicType = annotation.value();
            if (StringUtils.hasText(annotation.comment())) {
                dicTitle = annotation.comment();
            } else {
                dicTitle = dicClass.getSimpleName();
            }
        } else {
            dicType = dicClass.getSimpleName();
            dicTitle = dicClass.getSimpleName();
        }

        List<DictValueVo<? extends Serializable>> list = new ArrayList<>();
        final DictEnum<?>[] enumConstants = (DictEnum<?>[]) dicClass.getEnumConstants();
        for (DictEnum<?> enums : enumConstants) {
            if (logger.isDebugEnabled()) {
                logger.debug("class {} : - {} - {} - {} - {}", dicClass.getName(), dicType, enums.getValue(), enums.getTitle(), enums);
            }
            list.add(new DictValueVo<>(dicType, enums.getValue(), enums.getTitle(), null, 0));
        }
        final DictTypeVo dicTypeVo = new DictTypeVo(dicTitle, dicType, "From Application: " + applicationName, list);
        systemDicProvider.addDic(dicTypeVo);
    }

    /**
     * 获得需要扫描的包列表
     *
     * @param metadata 注解元数据
     * @return 包列表，至少包含一个默认的 com.pension.system 包路径
     */
    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(SystemDictScan.class.getName()));
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
        return packagesToScan;
    }
}
