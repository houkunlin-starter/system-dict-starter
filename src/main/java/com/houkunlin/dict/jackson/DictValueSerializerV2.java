package com.houkunlin.dict.jackson;

import com.houkunlin.dict.ClassUtil;
import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.SystemDictAutoConfiguration;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.enums.NullStrategy;
import com.houkunlin.dict.json.DictTypeKeyHandler;
import com.houkunlin.dict.json.DictWriter;
import com.houkunlin.dict.json.VoidDictTypeKeyHandler;
import com.houkunlin.dict.properties.DictProperties;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ValueSerializer;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 自定义字典值序列化器基类
 * <p>
 * 该类是字典值序列化器的抽象基类，封装了字典序列化的核心逻辑和配置信息。
 * 提供了字典文本获取、字典类型处理等基础功能，子类需要实现具体的序列化逻辑。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0
 */
@Getter
public abstract class DictValueSerializerV2 extends ValueSerializer<Object> {
    /**
     * 字典值写入器
     */
    public static final DictWriter DICT_WRITER = new DictWriter();
    private static final Logger logger = LoggerFactory.getLogger(DictValueSerializerV2.class);

    // ==== Bean 基本信息
    /**
     * 字段名称
     * <p>
     * Bean中需要进行字典值序列化处理的字段名
     * </p>
     */
    protected final String fieldName;

    /**
     * Java类型信息
     * <p>
     * 字段的Java类型信息，由Jackson提供，用于精确的类型处理
     * </p>
     */
    protected final Class<?> javaTypeRawClass;

    // ==== 字段注解配置
    /**
     * 字典文本注解配置
     * <p>
     * 包含字典文本序列化的相关配置信息
     * </p>
     */
    protected final DictText dictText;

    /**
     * 字典数组注解配置
     * <p>
     * 包含字典数组序列化的相关配置信息
     * </p>
     */
    protected final DictArray dictArray;

    /**
     * 字典树注解配置
     * <p>
     * 包含字典树序列化的相关配置信息
     * </p>
     */
    protected final DictTree dictTree;

    // ==== DictArray 默认配置，这个 DictArray 可能为空，但是一些场景需要预设默认值
    protected final String dictArraySplit;
    protected final boolean dictArrayToText;
    protected final String dictArrayDelimiter;
    protected final NullStrategy dictArrayNullStrategy;

    // ==== DictTree 默认配置，这个 DictTree 可能为空，但是一些场景需要预设默认值
    protected final int dictTreeMaxDepth;
    protected final boolean dictTreeToText;
    protected final String dictTreeDelimiter;
    protected final NullStrategy dictTreeNullStrategy;

    // ==== 输出配置
    /**
     * 输出字段名称
     * <p>
     * 序列化后的字典值输出到JSON中的字段名称
     * </p>
     */
    protected final String outputFieldName;

    /**
     * 是否使用 Map 格式输出
     * <p>
     * 如果为true，字典值将以Map格式输出，包含值和文本信息
     * </p>
     */
    protected final boolean useMap;

    /**
     * 是否使用数组格式输出
     * <p>
     * 如果为true，字典值将以数组格式输出，适用于多值情况
     * </p>
     */
    protected final boolean useArray;

    /**
     * 是否使用原始值类型
     * <p>
     * 如果为true，保留数值类型的原始格式，否则统一转换为字符串
     * </p>
     */
    protected final boolean useRawValueType;

    /**
     * 是否替换原字段值
     * <p>
     * 如果为true，将用字典文本替换原字段值，否则在原字段基础上添加新字段
     * </p>
     */
    protected final boolean useReplaceFieldValue;

    /**
     * 是否忽略 null 值
     * <p>
     * 如果为true，当字段值为null时，不进行序列化输出
     * </p>
     */
    protected final boolean textNullable;

    /**
     * 字典类型键处理器
     * <p>
     * 用于动态计算字典类型的处理器，支持运行时字典类型计算
     * </p>
     */
    protected final DictTypeKeyHandler<Object> dictTypeKeyHandler;

    public DictValueSerializerV2(String fieldName, Class<?> javaTypeRawClass, DictText dictText, DictArray dictArray, DictTree dictTree) {
        this.fieldName = fieldName;
        this.javaTypeRawClass = javaTypeRawClass;
        this.dictText = dictText;
        this.dictArray = dictArray;
        this.dictTree = dictTree;
        this.outputFieldName = StringUtils.hasText(dictText.fieldName()) ? dictText.fieldName() : fieldName + "Text";
        this.useMap = dictText.mapValue().getValue(SystemDictAutoConfiguration::isMapValue);
        this.useArray = dictArray != null && !dictArray.toText();
        this.useRawValueType = SystemDictAutoConfiguration.isRawValue();
        this.useReplaceFieldValue = dictText.replace().getValue(SystemDictAutoConfiguration::isReplaceValue);
        this.textNullable = dictText.nullable().getValue(SystemDictAutoConfiguration::isTextValueDefaultNull);
        if (dictText.dictTypeHandler() == VoidDictTypeKeyHandler.class) {
            this.dictTypeKeyHandler = null;
        } else {
            this.dictTypeKeyHandler = getDictTypeKeyHandler(dictText);
        }
        if (dictArray == null) {
            // 与 DictArray 注解的默认值保持一致
            this.dictArraySplit = "";
            this.dictArrayToText = false;
            this.dictArrayDelimiter = "、";
            this.dictArrayNullStrategy = NullStrategy.NULL;
        } else {
            this.dictArraySplit = dictArray.split();
            this.dictArrayToText = dictArray.toText();
            this.dictArrayDelimiter = dictArray.delimiter();
            this.dictArrayNullStrategy = dictArray.nullStrategy();
        }
        if (dictTree == null) {
            // 与 DictTree 注解的默认值保持一致
            this.dictTreeMaxDepth = -1;
            this.dictTreeToText = false;
            this.dictTreeDelimiter = "/";
            this.dictTreeNullStrategy = NullStrategy.NULL;
        } else {
            this.dictTreeMaxDepth = dictTree.maxDepth();
            this.dictTreeToText = dictTree.toText();
            this.dictTreeDelimiter = dictTree.delimiter();
            this.dictTreeNullStrategy = dictTree.nullStrategy();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static DictTypeKeyHandler<Object> getDictTypeKeyHandler(DictText dictText) {
        Class<? extends DictTypeKeyHandler> dictTypeHandlerClazz = dictText.dictTypeHandler();
        if (dictTypeHandlerClazz == VoidDictTypeKeyHandler.class) {
            return null;
        }
        DictTypeKeyHandler<Object> dictTypeKeyHandler = SystemDictAutoConfiguration.getBean(dictTypeHandlerClazz);
        if (dictTypeKeyHandler != null) {
            return dictTypeKeyHandler;
        }
        try {
            return ClassUtil.newInstance(dictTypeHandlerClazz);
        } catch (NoSuchMethodException e) {
            logger.error("创建 {} 实例失败，没有有效的默认构造方法，请向 SpringBoot 提供此 Bean 对象", dictTypeHandlerClazz.getName(), e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("创建 {} 实例失败，请向 SpringBoot 提供此 Bean 对象", dictTypeHandlerClazz.getName(), e);
        }
        return null;
    }

    public String getDictText(final Object bean, final Object value, final String dictType, final String arrayItemValue) {
        String text = getDictTextByEnums(arrayItemValue);
        if (text != null) {
            return text;
        }
        if (dictTypeKeyHandler == null) {
            return DictUtil.getDictText(dictType, arrayItemValue);
        }
        return dictTypeKeyHandler.getDictText(bean, fieldName, value, dictText, dictType, arrayItemValue);
    }

    public String getDictParentValue(final Object bean, final Object value, final String dictType, final String arrayItemValue) {
        if (dictTypeKeyHandler == null) {
            return DictUtil.getDictParentValue(dictType, arrayItemValue);
        }
        return dictTypeKeyHandler.getDictParentValue(bean, fieldName, value, dictText, dictType, arrayItemValue);
    }

    /**
     * 获取实际的字典类型
     * <p>
     * 根据字典处理器动态获取字典类型，支持运行时字典类型计算
     * </p>
     *
     * @param bean 包含字典字段的Bean对象
     * @return 实际的字典类型
     */
    public String getDictType(Object bean) {
        if (dictTypeKeyHandler == null) {
            return dictText.value();
        }
        return dictTypeKeyHandler.getDictType(bean, fieldName, dictText);
    }

    public String getDictTextByEnums(Object arrayItemValue) {
        for (Class<? extends DictEnum> dictEnum : dictText.enums()) {
            if (!dictEnum.isEnum()) {
                continue;
            }
            DictEnum<?>[] enumConstants = dictEnum.getEnumConstants();
            for (DictEnum<?> enumConstant : enumConstants) {
                if (enumConstant.eq(arrayItemValue) || ObjectUtils.getDisplayString(enumConstant.getValue()).equals(ObjectUtils.getDisplayString(arrayItemValue))) {
                    return enumConstant.getTitle();
                }
            }
        }
        return null;
    }

    public List<String> getTreeDictText(Object bean, Object value, final String dictType, String arrayItemValue) {
        assert dictTree != null;
        int depth = dictTree.maxDepth();
        if (depth <= 0) {
            // 使用全局配置
            depth = SystemDictAutoConfiguration.get(DictProperties::getTreeDepth).orElse(-1);
        }
        final List<String> values = new LinkedList<>();
        String itemValue = arrayItemValue;
        do {
            final String text = getDictText(bean, value, dictType, itemValue);
            if (text != null) {
                values.add(text);
            } else if (dictTree.nullStrategy() != NullStrategy.IGNORE) {
                if (dictTree.nullStrategy() == NullStrategy.NULL) {
                    values.add(null);
                } else if (dictTree.nullStrategy() == NullStrategy.EMPTY) {
                    values.add("");
                }
            }
            itemValue = getDictParentValue(bean, value, dictType, itemValue);
        } while (itemValue != null && (depth <= 0 || --depth > 0));
        Collections.reverse(values);
        return values;
    }

    public void appendDictText(List<String> values, String dictText) {
        if (dictText != null) {
            values.add(dictText);
        } else if (dictTreeNullStrategy == NullStrategy.NULL) {
            values.add(null);
        } else if (dictTreeNullStrategy == NullStrategy.EMPTY) {
            values.add("");
        }
    }


}
