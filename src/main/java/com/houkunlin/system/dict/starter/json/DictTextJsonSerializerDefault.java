package com.houkunlin.system.dict.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.SystemDictStarter;
import com.houkunlin.system.dict.starter.properties.DictProperties;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 一般情况下的场景，{@link DictText} 的普通用法
 *
 * @author HouKunLin
 * @since 1.4.3
 */
public class DictTextJsonSerializerDefault extends JsonSerializer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(DictTextJsonSerializerDefault.class);
    /**
     * 使用了这个注解的对象
     */
    protected final Class<?> beanClass;
    /**
     * 字段的类型
     */
    protected final Class<?> beanFieldClass;
    /**
     * 使用了这个注解的字段名称
     */
    protected final String beanFieldName;
    /**
     * 字典输出字段名称
     */
    @Getter
    protected final String outFieldName;
    /**
     * 字典转换注解对象
     */
    protected final DictText dictText;
    /**
     * 字典类型代码
     */
    protected final String dictType;
    protected final Array array;
    protected final boolean hasDictType;
    protected final boolean hasDictTextFieldName;
    protected final boolean isIterable;
    protected final boolean isArray;
    protected final boolean isCharSequence;
    protected final boolean isNeedSpiltValue;
    protected final Object defaultDictTextResult;
    protected DictTypeKeyHandler<Object> dictTypeKeyHandler = null;

    /**
     * 一般情况下的场景，{@link DictText} 的普通用法
     *
     * @param beanClass     实体类 class
     * @param beanFieldName 实体类字段名称
     * @param dictText      实体类字段上的 {@link DictText} 注解对象
     */
    public DictTextJsonSerializerDefault(Class<?> beanClass, Class<?> beanFieldClass, String beanFieldName, DictText dictText) {
        this.beanClass = beanClass;
        this.beanFieldClass = beanFieldClass;
        this.isIterable = Iterable.class.isAssignableFrom(beanFieldClass);
        this.isArray = beanFieldClass.isArray();
        this.isCharSequence = CharSequence.class.isAssignableFrom(beanFieldClass);
        this.beanFieldName = beanFieldName;
        this.dictText = dictText;
        this.array = dictText.array();
        this.isNeedSpiltValue = StringUtils.hasText(array.split());
        this.dictType = dictText.value();
        this.hasDictType = StringUtils.hasText(dictType) || dictText.dictTypeHandler() != VoidDictTypeKeyHandler.class;
        this.outFieldName = getFieldName(dictText);
        this.hasDictTextFieldName = StringUtils.hasText(dictText.fieldName());
        this.defaultDictTextResult = obtainResult(Collections.emptyList());
    }

    /**
     * 获取字典文本的字段名称
     *
     * @param dictText 注解对象
     * @return 输出的Json字段名称
     */
    private String getFieldName(DictText dictText) {
        final String fieldName = dictText.fieldName();
        if (StringUtils.hasText(fieldName)) {
            return fieldName;
        } else {
            return beanFieldName + "Text";
        }
    }

    @Override
    public void serialize(@Nullable final Object fieldValue, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (fieldValue == null) {
            writeFieldValue(gen, null, defaultNullableValue(defaultDictTextResult));
            return;
        }
        if (hasDictType) {
            final Object dictValueText = obtainDictValueText(gen.getCurrentValue(), fieldValue);
            writeFieldValue(gen, fieldValue, defaultNullableValue(dictValueText));
        } else {
            writeFieldValue(gen, fieldValue, defaultNullableValue(defaultDictTextResult));
            logger.warn("{}#{} @DictText annotation not set dictType value", beanClass, beanFieldName);
        }
    }

    /**
     * 序列化字典值
     *
     * @param beanObject 实体类对象
     * @param fieldValue 这个实体类的某个字段值（不需要确定这个字段，因为这个字段的信息就在当前的序列化器类属性中）
     * @return 字典文本结果
     */
    public Object serialize(final Object beanObject, @Nullable final Object fieldValue) {
        if (fieldValue == null) {
            return defaultNullableValue(defaultDictTextResult);
        }
        if (hasDictType) {
            final Object dictValueText = obtainDictValueText(beanObject, fieldValue);
            return defaultNullableValue(dictValueText);
        } else {
            logger.warn("{}#{} @DictText annotation not set dictType value", beanClass, beanFieldName);
            return defaultNullableValue(defaultDictTextResult);
        }
    }

    /**
     * 获取字段值的字典文本信息
     *
     * @param fieldValue 字段值（可能是一个需要分隔的字符串内容）
     * @return 字典值文本信息
     */
    protected Object obtainDictValueText(final Object bean, @NonNull Object fieldValue) {
        final String fieldValueString = fieldValue.toString();

        if (isIterable) {
            return processIterableField(bean, (Iterable<?>) fieldValue);
        }

        if (isArray) {
            return processArrayField(bean, (Object[]) fieldValue);
        }

        if (!isNeedSpiltValue) {
            // 不需要对值进行分割处理，直接当作一个普通的字典值去获取数据
            return obtainDictValueText(bean, fieldValueString);
        }

        if (isCharSequence) {
            return processStringField(bean, fieldValueString);
        }

        logger.warn("{}#{} = {} 不是一个字符串类型的字段，无法使用分隔数组功能", beanClass, beanFieldName, fieldValue);

        return defaultDictTextResult;
    }

    /**
     * 处理字段是 集合 类型的场景
     *
     * @param fieldValues 字段值（集合类型）
     * @return 字典文本结果
     */
    private Object processIterableField(final Object bean, final Iterable<?> fieldValues) {
        final List<String> texts = new ArrayList<>();
        for (final Object o : fieldValues) {
            processFieldArrayValue(bean, texts, o);
        }

        return obtainResult(texts);
    }

    /**
     * 处理字段是 数据 类型的场景
     *
     * @param fieldValues 字段值（数组类型）
     * @return 字典文本结果
     */
    private Object processArrayField(final Object bean, final Object[] fieldValues) {
        final List<String> texts = new ArrayList<>();
        for (final Object o : fieldValues) {
            processFieldArrayValue(bean, texts, o);
        }

        return obtainResult(texts);
    }

    /**
     * 处理字段是 字符串 类型的场景
     *
     * @param fieldValueString 字段值（字符串类型）
     * @return 字典文本结果
     */
    private Object processStringField(final Object bean, final String fieldValueString) {
        final String splitStr = array.split();
        final List<String> texts = new ArrayList<>();
        final String[] splitValue = fieldValueString.split(splitStr);
        for (final Object o : splitValue) {
            final String dictValueText = obtainDictValueText(bean, String.valueOf(o));
            if (!array.ignoreNull() || StringUtils.hasText(dictValueText)) {
                texts.add(dictValueText);
            }
        }
        return obtainResult(texts);
    }

    /**
     * 处理 集合、数组 类型的字段里面的 单个值 信息
     *
     * @param texts          存放字典文本结果的对象
     * @param fieldValueItem 集合、数组 的单个值
     */
    private void processFieldArrayValue(final Object bean, final List<String> texts, final Object fieldValueItem) {
        final String dictValueText;
        if (fieldValueItem instanceof DictEnum) {
            dictValueText = ((DictEnum<?>) fieldValueItem).getTitle();
        } else {
            dictValueText = obtainDictValueText(bean, String.valueOf(fieldValueItem));
        }
        if (!array.ignoreNull() || StringUtils.hasText(dictValueText)) {
            texts.add(dictValueText);
        }
    }

    private Object obtainResult(final List<String> dictTexts) {
        if (array.toText()) {
            if (dictTexts.isEmpty()) {
                return null;
            }
            return String.join(array.joinSeparator(), dictTexts);
        }

        return dictTexts;
    }

    /**
     * 从数据字典中获取字典值对应的文本信息
     *
     * @param dictValue 字典值
     * @return 字典值文本
     */
    protected String obtainDictValueText(final Object bean, String dictValue) {
        final String dictTypeKey = getDictTypeByTypeKeyHandler(bean, dictValue);
        // @since 1.4.6 - START
        if (dictText.tree()) {
            int depth = dictText.treeDepth();
            if (depth <= 0) {
                // 使用全局配置
                depth = SystemDictStarter.get(DictProperties::getTreeDepth).orElse(-1);
            }
            final List<String> values = new LinkedList<>();
            String value = dictValue;
            do {
                final String text = DictUtil.getDictText(dictTypeKey, value);
                if (text != null) {
                    values.add(0, text);
                }
                value = DictUtil.getDictParentValue(dictTypeKey, value);
            } while (value != null && (depth <= 0 || --depth > 0));
            if (values.isEmpty()) {
                return null;
            }
            return String.join("/", values);
        }
        // @since 1.4.6 - END
        return DictUtil.getDictText(dictTypeKey, dictValue);
    }

    /**
     * 获取字典类型代码
     *
     * @param bean      实体类对象
     * @param dictValue 字段值
     * @return 字典类型代码
     * @since 1.4.7
     */
    protected String getDictTypeByTypeKeyHandler(final Object bean, String dictValue) {
        final Class<? extends DictTypeKeyHandler> factoryClass = dictText.dictTypeHandler();
        if (factoryClass == VoidDictTypeKeyHandler.class) {
            return dictType;
        }
        if (dictTypeKeyHandler == null) {
            dictTypeKeyHandler = SystemDictStarter.getBean(factoryClass);
            if (dictTypeKeyHandler == null) {
                try {
                    dictTypeKeyHandler = factoryClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + factoryClass.getName() + " 实例失败，请向 SpringBoot 提供此 Bean 对象", e);
                    return dictType;
                }
            }
        }
        return dictTypeKeyHandler.getDictType(bean, beanFieldName, dictValue, dictText);
    }

    /**
     * 把数据字典原始值和转换后的字典文本值写入到 Json 中
     *
     * @param gen           JsonGenerator 对象
     * @param fieldValue    实体类字典值
     * @param dictValueText 字典文本值
     * @throws IOException 异常
     */
    protected void writeFieldValue(JsonGenerator gen, @Nullable Object fieldValue, Object dictValueText) throws IOException {
        final boolean isReplaceValue = dictText.replace().getValue(SystemDictStarter::isReplaceValue);
        if (dictText.mapValue().getValue(SystemDictStarter::isMapValue)) {
            final Map<String, Object> map = new HashMap<>();
            map.put("value", fieldValue);
            map.put("text", dictValueText);
            if (!isReplaceValue) {
                writeFieldValue(fieldValue, gen);
                gen.writeFieldName(outFieldName);
            }
            gen.writeObject(map);
        } else {
            if (!isReplaceValue) {
                writeFieldValue(fieldValue, gen);
                gen.writeFieldName(outFieldName);
            }
            gen.writeObject(dictValueText);
        }
    }

    /**
     * 把字段字典值写入到JSON数据中
     *
     * @param fieldValue 字段值
     * @param gen        JsonGenerator
     * @throws IOException 异常
     */
    private void writeFieldValue(@Nullable Object fieldValue, JsonGenerator gen) throws IOException {
        if (SystemDictStarter.isRawValue()) {
            gen.writeObject(fieldValue);
        } else {
            gen.writeString(fieldValue == null ? "" : fieldValue.toString());
        }
    }

    /**
     * 获取默认值
     *
     * @param dictValueText 原始值
     * @return 处理结果
     */
    protected Object defaultNullableValue(Object dictValueText) {
        if (dictText.nullable().getValue(SystemDictStarter::isTextValueDefaultNull)) {
            return dictValueText;
        }
        return dictValueText == null ? "" : dictValueText;
    }
}
