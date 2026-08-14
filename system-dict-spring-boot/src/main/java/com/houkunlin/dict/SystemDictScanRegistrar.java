package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.provider.SystemDictProvider;
import org.jspecify.annotations.NonNull;
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
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 系统数据字典自动扫描注册器
 * <p>
 * 配合 {@link SystemDictScan} 注解使用，负责扫描指定包路径下实现了 {@link DictEnum} 接口的枚举类，
 * 将枚举的字典值信息注册到 {@link SystemDictProvider} 系统字典提供者中。
 * </p>
 *
 * @author HouKunLin
 */
public class SystemDictScanRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanFactoryAware {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(SystemDictScanRegistrar.class);
    /**
     * 类路径扫描组件提供者，用于扫描指定包下的候选组件
     */
    private final ClassPathScanningCandidateComponentProvider provider;
    /**
     * 类加载器
     */
    private ClassLoader classLoader;
    /**
     * 系统字典提供者，用于注册扫描到的系统字典信息
     */
    private SystemDictProvider systemDictProvider;
    /**
     * 当前应用名称
     */
    private String applicationName;
    /**
     * Spring Bean 工厂
     */
    private BeanFactory beanFactory;

    /**
     * 构造方法
     * <p>
     * 初始化类路径扫描组件提供者，并配置仅扫描实现了 {@link DictEnum} 接口的类。
     * </p>
     */
    public SystemDictScanRegistrar() {
        provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(DictEnum.class));
    }

    /**
     * 设置 Spring Bean 工厂
     *
     * @param beanFactory Spring Bean 工厂
     */
    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 设置资源加载器
     * <p>
     * 从资源加载器中获取类加载器，用于后续加载扫描到的枚举类。
     * </p>
     *
     * @param resourceLoader 资源加载器
     */
    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.classLoader = resourceLoader.getClassLoader();
        assert this.classLoader != null;
    }

    /**
     * 注册 Bean 定义
     * <p>
     * 获取应用名称与系统字典提供者，解析需要扫描的包路径，并逐个包执行字典枚举扫描。
     * </p>
     *
     * @param annotationMetadata 注解元数据
     * @param registry           Bean 定义注册器
     */
    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata annotationMetadata, @NonNull BeanDefinitionRegistry registry) {
        final Environment environment = beanFactory.getBean(Environment.class);
        this.applicationName = environment.getProperty("spring.application.name", "default-app");
        this.systemDictProvider = beanFactory.getBean(SystemDictProvider.class);
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
     * @param <T>       字典值的类型
     */
    private <T extends Serializable> void handleDict(final Class<DictEnum<T>> dictClass) {
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
     * <p>
     * 根据 {@link DictType} 注解解析字典类型代码与标题，将枚举的各个字典值注册到系统字典提供者中。
     * 已存在的字典值将被忽略，避免重复写入缓存。
     * </p>
     *
     * @param dictClass 字典对象
     * @param annotation 字典类型注解，可以为 null
     * @param <T>        字典值的类型
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

        final com.houkunlin.dict.bean.DictType dictTypeVo = systemDictProvider.getDict(dictType, () -> new com.houkunlin.dict.bean.DictType(dictTitle, dictType, "From Application: " + applicationName, new ArrayList<>()));
        List<DictValue> list = dictTypeVo.getChildren();
        final DictEnum<?>[] enumConstants = dictClass.getEnumConstants();
        for (DictEnum<?> enums : enumConstants) {
            final Serializable value = enums.getValue();
            boolean exists = false;
            for (DictValue valueVo : list) {
                if (Objects.equals(valueVo.getValue(), value)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                DictValue vo = DictValue.builder()
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
     * @return 包列表，当未指定任何扫描包时，默认使用标注 {@link SystemDictScan} 注解的类所在包路径
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
