package com.houkunlin.dict.jackson;

import com.houkunlin.dict.ClassUtil;
import com.houkunlin.dict.SystemDictAutoConfiguration;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.json.DictTypeKeyHandler;
import com.houkunlin.dict.json.DictWriter;
import com.houkunlin.dict.json.VoidDictTypeKeyHandler;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.lang.reflect.InvocationTargetException;

/**
 * 自定义字典值序列化器基类
 * <p>
 * 该类是字典值序列化器的抽象基类，封装了字典序列化的核心逻辑和配置信息。
 * 提供了字典文本获取、字典类型处理等基础功能，子类需要实现具体的序列化逻辑。
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@Getter
public abstract class DictValueSerializer extends ValueSerializer<Object> {
    /**
     * 字典值写入器，用于将字典值写入到JSON生成器
     */
    public static final DictWriter DICT_WRITER = new DictWriter();
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(DictValueSerializer.class);

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

    /**
     * 构造方法
     * <p>
     * 初始化字典值序列化器，设置字段信息和注解配置
     * </p>
     *
     * @param fieldName        字段名称
     * @param javaTypeRawClass Java类型信息
     * @param dictText         字典文本注解配置
     * @param dictArray        字典数组注解配置
     * @param dictTree         字典树注解配置
     */
    public DictValueSerializer(String fieldName, Class<?> javaTypeRawClass, DictText dictText, DictArray dictArray, DictTree dictTree) {
        this.fieldName = fieldName;
        this.javaTypeRawClass = javaTypeRawClass;
        this.dictText = dictText;
        this.dictArray = dictArray;
        this.dictTree = dictTree;
        this.outputFieldName = dictText.fieldName().isBlank() ? fieldName + "Text" : dictText.fieldName();
        this.useMap = dictText.mapValue().getValue(SystemDictAutoConfiguration::isMapValue);
        this.useRawValueType = SystemDictAutoConfiguration.isRawValue();
        this.useReplaceFieldValue = dictText.replace().getValue(SystemDictAutoConfiguration::isReplaceValue);
        this.textNullable = dictText.nullable().getValue(SystemDictAutoConfiguration::isTextValueDefaultNull);
        if (dictText.dictTypeHandler() == VoidDictTypeKeyHandler.class) {
            this.dictTypeKeyHandler = null;
        } else {
            this.dictTypeKeyHandler = getDictTypeKeyHandler(dictText);
        }
    }

    /**
     * 获取字典类型键处理器。
     * <p>
     * 尝试从Spring容器中获取字典类型键处理器实例，如果不存在则通过反射创建
     * </p>
     *
     * @param dictText 字典文本注解配置
     * @return 字典类型键处理器
     */
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

    /**
     * 开始序列化字典值。
     * <p>
     * 该方法在字典值序列化开始时调用，处理字段值的序列化逻辑。
     * 根据配置决定是否保留原字段值，以及是否使用Map格式输出。
     * </p>
     * <p>
     * 处理流程：
     * 1. 如果不替换原字段值，则先写入原字段值，再写入字典文本字段名
     * 2. 如果使用Map格式输出，则开始一个Map对象，写入value字段和对应值
     * </p>
     *
     * @param value 字段值，需要进行序列化的原始值
     * @param gen   JSON生成器，用于写入JSON数据
     * @param ctxt  序列化上下文，提供序列化相关的上下文信息
     */
    public void startSerialize(Object value, JsonGenerator gen, SerializationContext ctxt) {
        if (!useReplaceFieldValue) {
            if (useRawValueType) {
                gen.writePOJO(value);
            } else {
                DICT_WRITER.writeDictValueToText(gen, value, dictText);
            }
            gen.writeName(outputFieldName);
        }
        if (useMap) {
            gen.writeStartObject();
            gen.writeName("value");
            if (useRawValueType) {
                gen.writePOJO(value);
            } else {
                DICT_WRITER.writeDictValueToText(gen, value, dictText);
            }
            gen.writeName("text");
        }
    }

    /**
     * 结束序列化字典值。
     * <p>
     * 该方法在字典值序列化结束时调用，主要用于清理资源和完成序列化操作。
     * 目前主要处理Map格式输出的结束标记。
     * </p>
     *
     * @param value 字段值，需要进行序列化的原始值
     * @param gen   JSON生成器，用于写入JSON数据
     * @param ctxt  序列化上下文，提供序列化相关的上下文信息
     */
    public void endSerialize(Object value, JsonGenerator gen, SerializationContext ctxt) {
        if (useMap) {
            gen.writeEndObject();
        }
    }

    /**
     * 转换字典字段值，获取值对应字典文本。
     * <p>
     * 抽象方法，子类需要实现具体的字典值转换逻辑。
     * 该方法将字段值转换为对应的字典文本值，支持处理各种类型的字段值，
     * 包括基本类型、数组、集合、可迭代对象等。
     * </p>
     * <p>
     * 在 {@link com.houkunlin.dict.DictUtil#transform(Object)} 方法中，
     * 该方法被用于转换对象中含有字典文本翻译注解的字段值，
     * 将原始字段值转换为对应的字典文本值。
     * </p>
     *
     * @param bean       Bean 对象，用于提供上下文信息，例如在动态计算字典类型时使用
     * @param fieldValue 字段值，需要进行字典转换的原始值
     * @return 转换后的字典值，可能是字典文本、字典文本数组或其他转换后的形式
     */
    public abstract Object transformFieldValue(final Object bean, @Nullable final Object fieldValue);

}
