package com.houkunlin.dict.json;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.SystemDictAutoConfiguration;
import com.houkunlin.dict.annotation.Array;
import com.houkunlin.dict.annotation.DictText;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 一般情况下的场景，字典文本转换的基本功能代码
 *
 * @author HouKunLin
 * @since 1.5.0
 */
public abstract class DictTextJsonSerializerBasic extends ValueSerializer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(DictTextJsonSerializerBasic.class);
    public static final DictWriter dictWriter = new DictWriter();
    /**
     * 使用了这个注解的对象
     */
    @Getter
    protected final Class<?> beanClass;
    /**
     * 字段的类型
     */
    @Getter
    protected final Class<?> beanFieldClass;
    /**
     * 使用了这个注解的字段名称
     */
    @Getter
    protected final String beanFieldName;
    /**
     * 字典输出字段名称
     */
    @Getter
    protected final String outFieldName;
    /**
     * 字典值内容是否是一个数组内容的配置信息。字典注解对象的 array 字段属性内容
     */
    protected final Array array;
    /**
     * {@link #beanFieldClass} 是否是一个集合对象。
     */
    protected final boolean isIterable;
    /**
     * {@link #beanFieldClass} 是否是一个数组对象
     */
    protected final boolean isArray;
    /**
     * {@link #beanFieldClass} 是否是一个字符串对象
     */
    protected final boolean isCharSequence;
    /**
     * 字典值（{@link #beanClass} 的 {@link #beanFieldName} 值）是否需要进行分割成数组
     */
    protected final boolean isNeedSpiltValue;
    /**
     * 如果字典为 null 默认的字典文本结果
     */
    protected final Object defaultDictTextResult;

    /**
     * 一般情况下的普通用法
     *
     * @param beanClass           数据类 class
     * @param beanFieldClass      数据类的字段类型 class
     * @param beanFieldName       数据类字段名称
     * @param array               字典值的数组分割配置
     * @param annotationFieldName 注解配置的输出字段名称
     */
    public DictTextJsonSerializerBasic(Class<?> beanClass, Class<?> beanFieldClass, String beanFieldName, Array array, String annotationFieldName) {
        this.beanClass = beanClass;
        this.beanFieldClass = beanFieldClass;
        this.isIterable = Iterable.class.isAssignableFrom(beanFieldClass);
        this.isArray = beanFieldClass.isArray();
        this.isCharSequence = CharSequence.class.isAssignableFrom(beanFieldClass);
        this.beanFieldName = beanFieldName;
        this.array = array;
        this.isNeedSpiltValue = StringUtils.hasText(array.split());
        this.outFieldName = getOutFieldName(annotationFieldName, beanFieldName);
        this.defaultDictTextResult = obtainResult(Collections.emptyList());
    }

    /**
     * 获取字典文本的字段名称
     *
     * @param annotationFieldName 注解对象的 fieldName 内容
     * @param beanFieldName       数据类字段名称
     * @return 输出的Json字段名称
     */
    protected String getOutFieldName(final String annotationFieldName, final String beanFieldName) {
        if (StringUtils.hasText(annotationFieldName)) {
            return annotationFieldName;
        } else {
            return beanFieldName + "Text";
        }
    }

    /**
     * 获取字段值的字典文本信息
     *
     * @param bean       数据类对象
     * @param fieldValue 字段值（可能是一个需要分隔的字符串内容）
     * @return 字典值文本信息
     */
    protected Object obtainDictValueText(final Object bean, @NonNull Object fieldValue) {
        final String fieldValueString = fieldValue.toString();

        if (isIterable) {
            return obtainResult(getDictTitles(bean, (Iterable<?>) fieldValue, true));
        }

        if (isArray) {
            return obtainResult(getDictTitles(bean, Arrays.asList((Object[]) fieldValue), true));
        }

        if (!isNeedSpiltValue) {
            // 不需要对值进行分割处理，直接当作一个普通的字典值去获取数据
            return obtainDictValueText(bean, fieldValueString);
        }

        if (isCharSequence) {
            final String[] splitValue = fieldValueString.split(array.split());
            return obtainResult(getDictTitles(bean, Arrays.asList(splitValue), false));
        }

        logger.warn("{}#{} = {} 不是一个字符串类型的字段，无法使用分隔数组功能", beanClass, beanFieldName, fieldValue);

        return defaultDictTextResult;
    }

    /**
     * 获取集合的字典文本列表
     *
     * @param bean     数据类对象
     * @param values   字典值列表
     * @param isObject 字典值是否可能是一个对象
     * @return 字典文本列表
     */
    public List<String> getDictTitles(final Object bean, final Iterable<?> values, final boolean isObject) {
        final List<String> result = new ArrayList<>();

        for (Object fieldValueItem : values) {
            final String dictValueText;
            if (isObject && fieldValueItem instanceof DictEnum) {
                dictValueText = ((DictEnum<?>) fieldValueItem).getTitle();
            } else {
                dictValueText = obtainDictValueText(bean, String.valueOf(fieldValueItem));
            }
            if (!array.ignoreNull() || StringUtils.hasText(dictValueText)) {
                result.add(dictValueText);
            }
        }

        return result;
    }

    public Object obtainResult(final List<String> dictTexts) {
        if (array.toText()) {
            if (dictTexts.isEmpty()) {
                return null;
            }
            return String.join(array.joinSeparator(), dictTexts);
        }

        return dictTexts;
    }

    /**
     * 把数据字典原始值和转换后的字典文本值写入到 Json 中
     *
     * @param gen           JsonGenerator 对象
     * @param fieldValue    数据类字典值
     * @param dictValueText 字典文本值
     * @throws IOException 异常
     */
    protected void writeFieldValue(JsonGenerator gen, @Nullable Object fieldValue, Object dictValueText) throws JacksonException {
        final boolean isReplaceValue = isReplaceValue();
        if (mapValue().getValue(SystemDictAutoConfiguration::isMapValue)) {
            if (!isReplaceValue) {
                dictWriter.writeDictValue(gen, fieldValue, getDictText());
                gen.writeName(outFieldName);
            }
            gen.writeStartObject();
            gen.writeName("value");
            dictWriter.writeDictValue(gen, fieldValue, getDictText());
            gen.writeName("text");
            dictWriter.writeDictText(gen, dictValueText, getDictText());
            gen.writeEndObject();
        } else {
            if (!isReplaceValue) {
                dictWriter.writeDictValue(gen, fieldValue, getDictText());
                gen.writeName(outFieldName);
            }
            dictWriter.writeDictText(gen, dictValueText, getDictText());
        }
    }

    /**
     * 是否替换字段值
     *
     * @return boolean
     */
    public boolean isReplaceValue() {
        return replace().getValue(SystemDictAutoConfiguration::isReplaceValue);
    }

    /**
     * 获取默认值
     *
     * @param dictValueText 原始值
     * @return 处理结果
     */
    public Object defaultNullableValue(Object dictValueText) {
        if (nullable().getValue(SystemDictAutoConfiguration::isTextValueDefaultNull)) {
            return dictValueText;
        }
        return dictValueText == null ? "" : dictValueText;
    }

    /**
     * 序列化字典值
     *
     * @param bean       实体类对象
     * @param fieldValue 这个实体类的某个字段值（不需要确定这个字段，因为这个字段的信息就在当前的序列化器类属性中）
     * @return 字典文本结果
     */
    public abstract Object serialize(final Object bean, @Nullable final Object fieldValue);

    /**
     * 从数据字典中获取字典值对应的文本信息
     *
     * @param bean 实体类对象
     * @param dictValue 字典值
     * @return 字典值文本
     */
    public abstract String obtainDictValueText(final Object bean, String dictValue);

    /**
     * 设置当没有获取到数据时是否为 null。
     *
     * @return 是否需要返回 null
     * @see DictText#nullable()
     */
    public abstract DictBoolType nullable();

    /**
     * 标记是否替换原始值，不使用 {@link DictText#fieldName()} 字段输出，直接用字典文本替换原来的字典值输出。
     * 默认使用 全局配置 配置参数
     *
     * @return 是否替换原始值
     * @see DictText#replace()
     */
    public abstract DictBoolType replace();

    /**
     * 此配置将会使 #fieldName 配置失效；用来标记是否使用 Map 对象返回字典值信息
     * <pre>
     * {
     *   "peopleType" : {
     *     "value" : 0,
     *     "text" : "系统管理"
     *   }
     * }
     * </pre>
     *
     * @return 是否返回 map 值
     * @see DictText#mapValue()
     */
    public abstract DictBoolType mapValue();

    /**
     * 是否是树形结构数据；
     *
     * @return boolean <ul>
     * <li>true 是树形结构数据，加载父级信息（采用递归加载，可能会多次加载父级信息）；</li>
     * <li>false 不是树形结构数据，不加载父级信息；</li>
     * </ul>
     * @see DictText#tree()
     */
    public abstract boolean tree();

    /**
     * 为防止陷入死循环，请设置树形结构数据的向访问的最大访问深度，超过最大访问深度则直接返回。
     *
     * @return int &lt;= 0 视为不限制深度
     */
    public abstract int treeDepth();

    public abstract DictText getDictText();
}
