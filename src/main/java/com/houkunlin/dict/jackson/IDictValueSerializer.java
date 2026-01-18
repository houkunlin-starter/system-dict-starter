package com.houkunlin.dict.jackson;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.enums.NullStrategy;
import com.houkunlin.dict.json.DictTypeKeyHandler;
import org.springframework.util.ObjectUtils;
import tools.jackson.core.JsonGenerator;

import java.util.List;

/**
 * 字典值序列化接口
 * <p>
 * 该接口定义了字典值序列化的核心方法，用于处理字典值的转换和序列化。
 * 主要功能包括：
 * <ul>
 * <li>获取字典类型</li>
 * <li>获取字典文本</li>
 * <li>获取字典父级值</li>
 * <li>通过枚举获取字典文本</li>
 * <li>处理数组类型的字典值</li>
 * <li>处理空值策略</li>
 * </ul>
 * 该接口是字典序列化系统的核心组件，被各种字典序列化实现类所实现，
 * 用于在 JSON 序列化过程中处理字典值的转换。
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
public interface IDictValueSerializer {
    /**
     * 是否忽略 null 值
     * <p>
     * 如果为 true，当字段值为 null 时，不进行序列化输出；
     * 如果为 false，当字段值为 null 时，根据配置进行序列化输出。
     * </p>
     *
     * @return 是否忽略 null 值
     */
    boolean isTextNullable();

    /**
     * 字典类型键处理器
     * <p>
     * 用于动态计算字典类型的处理器，支持运行时字典类型计算。
     * 当字典类型需要根据运行时上下文动态计算时，使用该处理器。
     * </p>
     *
     * @return 字典类型键处理器
     */
    DictTypeKeyHandler<Object> getDictTypeKeyHandler();

    /**
     * 获取字典类型
     * <p>
     * 该方法用于获取字典类型代码，实现逻辑如下：
     * 1. 首先获取字典类型键处理器（DictTypeKeyHandler）
     * 2. 如果字典类型键处理器为 null，则直接使用 DictText 注解中指定的字典类型
     * 3. 如果字典类型键处理器不为 null，则调用其 getDictType 方法动态计算字典类型
     * 4. 如果字典类型键处理器返回 null，则回退到使用 DictText 注解中指定的字典类型
     * </p>
     * <p>
     * 该方法支持运行时动态计算字典类型，当字典类型需要根据上下文信息确定时，
     * 可以通过实现自定义的 DictTypeKeyHandler 来实现动态计算逻辑。同时，
     * 该方法还提供了回退机制，确保即使动态计算失败也能使用默认的字典类型。
     * </p>
     *
     * @param bean      目标对象，用于动态计算字典类型时提供上下文信息
     * @param fieldName 字段名称，用于动态计算字典类型时提供字段信息
     * @param dictText  字典文本注解，包含默认的字典类型配置
     * @return 字典类型代码，可能是动态计算的或默认的
     */
    default String getDictType(Object bean, String fieldName, DictText dictText) {
        DictTypeKeyHandler<Object> dictTypeKeyHandler = getDictTypeKeyHandler();
        if (dictTypeKeyHandler == null) {
            return dictText.value();
        }
        String dictType = dictTypeKeyHandler.getDictType(bean, fieldName, dictText);
        if (dictType != null) {
            return dictType;
        }
        return dictText.value();
    }

    /**
     * 获取字典文本
     * <p>
     * 该方法用于获取字典文本，处理逻辑如下：
     * 1. 首先检查 DictText 注解中是否指定了枚举类，如果指定了，则尝试通过枚举获取字典文本
     * 2. 如果通过枚举获取失败或未指定枚举类，则使用字典类型键处理器获取字典文本
     * 3. 如果字典类型键处理器为 null，则直接使用 DictUtil.getDictText 方法获取字典文本
     * </p>
     *
     * @param bean           目标对象
     * @param fieldName      字段名称
     * @param value          字段值
     * @param dictText       字典文本注解
     * @param dictType       字典类型代码
     * @param arrayItemValue 数组项值
     * @return 字典文本
     */
    default String getDictText(final Object bean, String fieldName, final Object value, DictText dictText, final String dictType, final String arrayItemValue) {
        Class<? extends DictEnum>[] enums = dictText.enums();
        if (enums.length > 0) {
            String dictTextByEnums = getDictTextByEnums(enums, arrayItemValue);
            if (dictTextByEnums != null) {
                return dictTextByEnums;
            }
        }
        DictTypeKeyHandler<Object> dictTypeKeyHandler = getDictTypeKeyHandler();
        if (dictTypeKeyHandler == null) {
            return DictUtil.getDictText(dictType, arrayItemValue);
        }
        return dictTypeKeyHandler.getDictText(bean, fieldName, value, dictText, dictType, arrayItemValue);
    }

    /**
     * 获取字典父级值
     * <p>
     * 该方法用于获取字典父级值，优先使用字典类型键处理器来获取，
     * 如果字典类型键处理器为 null，则直接使用 DictUtil.getDictParentValue 方法获取。
     * </p>
     *
     * @param bean          目标对象
     * @param fieldName     字段名称
     * @param value         字段值
     * @param dictText      字典文本注解
     * @param dictType      字典类型代码
     * @param arrayItemValue 数组项值
     * @return 字典父级值
     */
    default String getDictParentValue(final Object bean, String fieldName, final Object value, DictText dictText, final String dictType, final String arrayItemValue) {
        DictTypeKeyHandler<Object> dictTypeKeyHandler = getDictTypeKeyHandler();
        if (dictTypeKeyHandler == null) {
            return DictUtil.getDictParentValue(dictType, arrayItemValue);
        }
        return dictTypeKeyHandler.getDictParentValue(bean, fieldName, value, dictText, dictType, arrayItemValue);
    }

    /**
     * 通过枚举获取字典文本
     * <p>
     * 该方法用于通过枚举获取字典文本，遍历指定的枚举类数组，
     * 对于每个枚举类，获取其所有枚举常量，然后比较枚举常量的值是否与传入的数组项值相等，
     * 如果相等，则返回该枚举常量的标题作为字典文本。
     * </p>
     *
     * @param enums         枚举类数组
     * @param arrayItemValue 数组项值
     * @return 字典文本，如果未找到则返回 null
     */
    default String getDictTextByEnums(Class<? extends DictEnum>[] enums, String arrayItemValue) {
        for (Class<? extends DictEnum> dictEnum : enums) {
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

    /**
     * 将文本添加到列表中
     * <p>
     * 该方法用于处理数组类型的字典值，根据配置的空值策略将文本添加到列表中：
     * 1. 如果文本不为 null，则直接添加到列表中
     * 2. 如果文本为 null，则根据 DictArray 注解中的 nullStrategy 处理：
     *    - IGNORE：忽略该值，不添加到列表中
     *    - NULL：添加 null 到列表中
     *    - EMPTY：添加空字符串到列表中
     * </p>
     *
     * @param textList 文本列表
     * @param text     文本
     * @param dictArray 字典数组注解
     */
    default void appendTextToList(List<String> textList, String text, DictArray dictArray) {
        if (text != null) {
            textList.add(text);
        } else if (dictArray.nullStrategy() != NullStrategy.IGNORE) {
            if (dictArray.nullStrategy() == NullStrategy.NULL) {
                textList.add(null);
            } else {
                textList.add("");
            }
        }
    }

    /**
     * 写入数组文本
     * <p>
     * 该方法用于将数组类型的字典文本写入 JSON 生成器，根据配置的空值策略处理：
     * 1. 如果文本不为 null，则写入字符串
     * 2. 如果文本为 null，则根据 DictArray 注解中的 nullStrategy 处理：
     *    - IGNORE：忽略该值，不写入
     *    - NULL：写入 null
     *    - EMPTY：写入空字符串
     * </p>
     *
     * @param gen      JSON 生成器
     * @param text     文本
     * @param dictArray 字典数组注解
     */
    default void writeArrayText(JsonGenerator gen, String text, DictArray dictArray) {
        if (text != null) {
            gen.writeString(text);
        } else if (dictArray.nullStrategy() != NullStrategy.IGNORE) {
            if (dictArray.nullStrategy() == NullStrategy.NULL) {
                gen.writeNull();
            } else {
                gen.writeString("");
            }
        }
    }
}
