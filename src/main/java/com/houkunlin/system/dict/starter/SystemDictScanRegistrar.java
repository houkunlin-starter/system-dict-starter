package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.bytecode.IDictConverterGenerate;
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
    private IDictConverterGenerate generateConverter;
    private ClassLoader classLoader;
    private SystemDictProvider systemDictProvider;
    private String applicationName;
    private BeanFactory beanFactory;
    private BeanDefinitionRegistry registry;
    private SystemDictConverterWebMvcConfigurer webMvcConfigurer;

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
        this.registry = registry;
        this.systemDictProvider = beanFactory.getBean(SystemDictProvider.class);
        this.generateConverter = beanFactory.getBean(IDictConverterGenerate.class);
        this.webMvcConfigurer = beanFactory.getBean(SystemDictConverterWebMvcConfigurer.class);
        Set<String> packagesToScan = getPackagesToScan(annotationMetadata);
        packagesToScan.forEach(this::scanPackage);
    }

    /**
     * 扫描指定包路径下的系统数据字典
     *
     * @param basePackage 包名
     */
    @SuppressWarnings({"unchecked"})
    private void scanPackage(String basePackage) {
        final Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        for (BeanDefinition component : components) {
            try {
                final Class<?> loadClass = classLoader.loadClass(component.getBeanClassName());
                if (loadClass.isEnum()) {
                    handleDict((Class<DictEnum<Serializable>>) loadClass);
                }
            } catch (ClassNotFoundException e) {
                logger.error("扫描系统字典枚举失败，虽然不影响启动，但是最终会影响 @DictText 注解功能", e);
            }
        }
    }

    /**
     * 处理系统数据字典对象
     *
     * @param dictClass 字典对象
     */
    private <T extends Serializable> void handleDict(final Class<DictEnum<T>> dictClass) {
        final DictConverter converter = dictClass.getDeclaredAnnotation(DictConverter.class);
        if (converter != null) {
            try {
                final Class<DictEnum<T>> converterClass = generateConverter.getConverterClass(dictClass, converter);
                webMvcConfigurer.addConverterClass(converterClass);
            } catch (Exception e) {
                logger.error("自动创建系统字典枚举 {} 的 Converter 转换器失败，不影响系统启动，但是会影响此枚举转换器功能", dictClass.getName(), e);
            }
            // generateConverter.registerBean(registry, dictClass, converter);
        }
        final DictType[] annotation = dictClass.getDeclaredAnnotationsByType(DictType.class);
        if (annotation.length > 0) {
            for (final DictType dictType : annotation) {
                handleDict(dictClass, dictType);
            }
        } else {
            handleDict(dictClass, null);
        }
    }

    /**
     * 处理系统数据字典对象
     *
     * @param dictClass 字典对象
     */
    private <T extends Serializable> void handleDict(final Class<DictEnum<T>> dictClass, final DictType annotation) {
        final String dictType;
        String dictTitle;
        if (annotation != null) {
            if (StringUtils.hasText(annotation.comment())) {
                dictTitle = annotation.comment();
            } else {
                dictTitle = dictClass.getSimpleName();
            }
            if (StringUtils.hasText(annotation.value())) {
                dictType = annotation.value();
            } else {
                dictType = dictClass.getSimpleName();
            }
        } else {
            dictType = dictClass.getSimpleName();
            dictTitle = dictClass.getSimpleName();
        }

        final DictTypeVo dictTypeVo = systemDictProvider.getDict(dictType, () -> new DictTypeVo(dictTitle, dictType, "From Application: " + applicationName, new ArrayList<>()));
        List<DictValueVo> list = dictTypeVo.getChildren();
        final DictEnum<?>[] enumConstants = dictClass.getEnumConstants();
        for (DictEnum<?> enums : enumConstants) {
            final Serializable value = enums.getValue();
            boolean exists = false;
            for (DictValueVo valueVo : list) {
                if (Objects.equals(valueVo.getValue(), value)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                DictValueVo vo = DictValueVo.builder()
                    .dictType(dictType)
                    .parentValue(enums.getParentValue())
                    .value(value)
                    .title(enums.getTitle())
                    .sorted(enums.getSorted())
                    .disabled(enums.isDisabled())
                    .data(enums.getData())
                    .build();
                list.add(vo);
            }
            if (logger.isDebugEnabled()) {
                if (value instanceof String) {
                    logger.debug("dict enum: {}.{}(\"{}\", \"{}\") by dict type: {} {}", dictClass.getName(), enums, value, enums.getTitle(), dictType, exists ? "已经存在，忽略处理" : "将写入缓存");
                } else {
                    logger.debug("dict enum: {}.{}({}, \"{}\") by dict type: {} {}", dictClass.getName(), enums, value, enums.getTitle(), dictType, exists ? "已经存在，忽略处理" : "将写入缓存");
                }
            }
        }
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
