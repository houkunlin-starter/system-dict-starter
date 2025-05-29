package com.houkunlin.system.dict.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.houkunlin.system.dict.starter.ClassUtil;
import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.SystemDictAutoConfiguration;
import com.houkunlin.system.dict.starter.properties.DictProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * 一般情况下的场景，{@link DictText} 的普通用法
 *
 * @author HouKunLin
 * @since 1.4.3
 */
public class DictTextJsonSerializerDefault extends DictTextJsonSerializerBasic {
    private static final Logger logger = LoggerFactory.getLogger(DictTextJsonSerializerDefault.class);
    /**
     * 字典转换注解对象
     */
    protected final DictText dictText;
    /**
     * 字典类型代码 {@link #dictText} 字段的 value 属性内容
     */
    protected final String dictType;
    /**
     * 是否存在字典类型。{@link #dictText} 字段的 value 属性内容是否存在
     */
    protected final boolean hasDictType;
    /**
     * 自定义的字典类型处理对象
     */
    protected DictTypeKeyHandler<Object> dictTypeKeyHandler = null;
    protected boolean dictTypeKeyHandlerInit = false;

    /**
     * 一般情况下的场景，{@link DictText} 的普通用法
     *
     * @param beanClass     实体类 class
     * @param beanFieldName 实体类字段名称
     * @param dictText      实体类字段上的 {@link DictText} 注解对象
     */
    public DictTextJsonSerializerDefault(Class<?> beanClass, Class<?> beanFieldClass, String beanFieldName, DictText dictText) {
        super(beanClass, beanFieldClass, beanFieldName, dictText.array(), dictText.fieldName());
        this.dictText = dictText;
        this.dictType = dictText.value();
        this.hasDictType = StringUtils.hasText(dictType) || dictText.dictTypeHandler() != VoidDictTypeKeyHandler.class;
    }

    @Override
    public void serialize(@Nullable final Object fieldValue, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (fieldValue == null) {
            writeFieldValue(gen, null, defaultNullableValue(defaultDictTextResult));
            return;
        }
        if (hasDictType) {
            final Object dictValueText = obtainDictValueText(gen.currentValue(), fieldValue);
            writeFieldValue(gen, fieldValue, defaultNullableValue(dictValueText));
        } else {
            writeFieldValue(gen, fieldValue, defaultNullableValue(defaultDictTextResult));
            logger.warn("{}#{} @DictText annotation not set dictType value", beanClass, beanFieldName);
        }
    }

    @Override
    public Object serialize(final Object bean, @Nullable final Object fieldValue) {
        if (fieldValue == null) {
            return defaultNullableValue(defaultDictTextResult);
        }
        if (hasDictType) {
            final Object dictValueText = obtainDictValueText(bean, fieldValue);
            return defaultNullableValue(dictValueText);
        } else {
            logger.warn("{}#{} @DictText annotation not set dictType value", beanClass, beanFieldName);
            return defaultNullableValue(defaultDictTextResult);
        }
    }

    /**
     * 从数据字典中获取字典值对应的文本信息
     *
     * @param dictValue 字典值
     * @return 字典值文本
     */
    @Override
    public String obtainDictValueText(final Object bean, String dictValue) {
        final String dictTypeKey = getDictTypeByTypeKeyHandler(bean, dictValue);
        // @since 1.4.6 - START
        if (dictText.tree()) {
            int depth = dictText.treeDepth();
            if (depth <= 0) {
                // 使用全局配置
                depth = SystemDictAutoConfiguration.get(DictProperties::getTreeDepth).orElse(-1);
            }
            final List<String> values = new LinkedList<>();
            String value = dictValue;
            do {
                final String text = getDictTextByTypeKeyHandler(bean, dictValue, dictTypeKey, value);
                if (text != null) {
                    values.add(0, text);
                }
                value = getDictParentValueByTypeKeyHandler(bean, dictValue, dictTypeKey, value);
            } while (value != null && (depth <= 0 || --depth > 0));
            if (values.isEmpty()) {
                return null;
            }
            return String.join("/", values);
        }
        // @since 1.4.6 - END
        return getDictTextByTypeKeyHandler(bean, dictValue, dictTypeKey, dictValue);
    }

    /**
     * 获取字典类型代码
     *
     * @param bean      实体类对象
     * @param fieldValueItem 字段值
     * @return 字典类型代码
     * @since 1.4.7
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected String getDictTypeByTypeKeyHandler(final Object bean, String fieldValueItem) {
        final Class<? extends DictTypeKeyHandler> factoryClass = dictText.dictTypeHandler();
        if (factoryClass == VoidDictTypeKeyHandler.class) {
            return dictType;
        }
        if (dictTypeKeyHandler == null && !dictTypeKeyHandlerInit) {
            dictTypeKeyHandlerInit = true;
            dictTypeKeyHandler = SystemDictAutoConfiguration.getBean(factoryClass);
            if (dictTypeKeyHandler == null) {
                try {
                    Constructor<?> defaultConstructor = ClassUtil.getDefaultConstructor(factoryClass);
                    if (defaultConstructor != null) {
                        dictTypeKeyHandler = (DictTypeKeyHandler<Object>) defaultConstructor.newInstance();
                    } else {
                        logger.error("创建 {} 实例失败，没有有效的默认构造方法，请向 SpringBoot 提供此 Bean 对象", factoryClass.getName());
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.error("创建 {} 实例失败，请向 SpringBoot 提供此 Bean 对象", factoryClass.getName(), e);
                    return dictType;
                }
            }
        }
        if (dictTypeKeyHandler == null) {
            return dictType;
        }
        return dictTypeKeyHandler.getDictType(bean, beanFieldName, fieldValueItem, dictText);
    }

    /**
     * 获取字典文本
     *
     * @param bean           实体类对象
     * @param fieldValueItem 字段值
     * @param dictType       字典类型
     * @param dictValue      字典值
     * @return 字典文本
     * @since 1.5.2
     */
    private String getDictTextByTypeKeyHandler(final Object bean, final String fieldValueItem, final String dictType, final String dictValue) {
        if (dictTypeKeyHandler == null) {
            return DictUtil.getDictText(dictType, dictValue);
        }
        return dictTypeKeyHandler.getDictText(bean, beanFieldName, fieldValueItem, dictText, dictType, dictValue);
    }

    /**
     * 获取父级字典值
     *
     * @param bean           实体类对象
     * @param fieldValueItem 字段值
     * @param dictType       字典类型
     * @param dictValue      字典值
     * @return 父级字典值
     * @since 1.5.2
     */
    private String getDictParentValueByTypeKeyHandler(final Object bean, final String fieldValueItem, final String dictType, final String dictValue) {
        if (dictTypeKeyHandler == null) {
            return DictUtil.getDictText(dictType, dictValue);
        }
        return dictTypeKeyHandler.getDictParentValue(bean, beanFieldName, fieldValueItem, dictText, dictType, dictValue);
    }

    @Override
    public DictBoolType nullable() {
        return dictText.nullable();
    }

    @Override
    public DictBoolType replace() {
        return dictText.replace();
    }

    @Override
    public DictBoolType mapValue() {
        return dictText.mapValue();
    }

    @Override
    public boolean tree() {
        return dictText.tree();
    }

    @Override
    public int treeDepth() {
        return dictText.treeDepth();
    }
}
