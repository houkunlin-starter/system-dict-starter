package com.houkunlin.system.dict.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.SystemDictStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    protected final Class<?> beanClazz;
    /**
     * 字段的类型
     */
    protected final Class<?> fieldClazz;
    /**
     * 使用了这个注解的字段名称
     */
    protected final String beanFieldName;
    /**
     * 字典输出字段名称
     */
    protected final String destinationFieldName;
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
    protected final boolean hasFieldName;
    protected final boolean isIterable;
    protected final boolean isArray;
    protected final boolean isCharSequence;
    protected final boolean isNeedSpiltValue;

    /**
     * 一般情况下的场景，{@link DictText} 的普通用法
     *
     * @param beanClazz     实体类 class
     * @param beanFieldName 实体类字段名称
     * @param dictText      实体类字段上的 {@link DictText} 注解对象
     */
    public DictTextJsonSerializerDefault(Class<?> beanClazz, Class<?> fieldClazz, String beanFieldName, DictText dictText) {
        this.beanClazz = beanClazz;
        this.fieldClazz = fieldClazz;
        this.isIterable = Iterable.class.isAssignableFrom(fieldClazz);
        this.isArray = fieldClazz.isArray();
        this.isCharSequence = CharSequence.class.isAssignableFrom(fieldClazz);
        this.beanFieldName = beanFieldName;
        this.dictText = dictText;
        this.array = dictText.array();
        this.isNeedSpiltValue = StringUtils.hasText(array.split());
        this.dictType = dictText.value();
        this.hasDictType = StringUtils.hasText(dictType);
        this.destinationFieldName = getFieldName(dictText);
        this.hasFieldName = StringUtils.hasText(dictText.fieldName());
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
        fromDictCache(fieldValue, gen);
    }

    /**
     * 从缓存中获取字典文本
     *
     * @param fieldValue 字典值对象
     * @param gen        JsonGenerator
     * @throws IOException 异常
     */
    protected void fromDictCache(@Nullable Object fieldValue, JsonGenerator gen) throws IOException {
        if (hasDictType) {
            writeFieldValue(gen, fieldValue, defaultNullableValue(obtainDictValueText(fieldValue)));
        } else {
            writeFieldValue(gen, fieldValue, defaultNullableValue(null));
            logger.warn("{}#{} @DictText annotation not set dictType value", beanClazz, beanFieldName);
        }
    }

    /**
     * 获取字段值的字典文本信息
     *
     * @param fieldValue 字段值（可能是一个需要分隔的字符串内容）
     * @return 字典值文本信息
     */
    protected Object obtainDictValueText(@Nullable Object fieldValue) {
        if (fieldValue == null) {
            return obtainResults(Collections.emptyList());
        }
        final String valueAsString = fieldValue.toString();

        if (isIterable) {
            final List<String> texts = new ArrayList<>();
            final Iterable<?> iterable = (Iterable<?>) fieldValue;
            iterable.forEach(o -> {
                final String dictValueText;
                if (o instanceof DictEnum) {
                    dictValueText = ((DictEnum) o).getTitle();
                } else {
                    dictValueText = obtainDictValueText(String.valueOf(o));
                }
                if (!array.ignoreNull() || StringUtils.hasText(dictValueText)) {
                    texts.add(dictValueText);
                }
            });

            return obtainResults(texts);
        }

        if (isArray) {
            final List<String> texts = new ArrayList<>();
            Object[] objects = (Object[]) fieldValue;
            for (final Object o : objects) {
                final String dictValueText = obtainDictValueText(String.valueOf(o));
                if (!array.ignoreNull() || StringUtils.hasText(dictValueText)) {
                    texts.add(dictValueText);
                }
            }

            return obtainResults(texts);
        }

        if (!isNeedSpiltValue) {
            return obtainDictValueText(valueAsString);
        }

        final List<String> texts;
        if (isCharSequence) {
            final String splitStr = array.split();
            if (valueAsString.contains(splitStr)) {
                texts = new ArrayList<>();
                final String[] splitValue = valueAsString.split(splitStr);
                for (final Object o : splitValue) {
                    final String dictValueText = obtainDictValueText(String.valueOf(o));
                    if (!array.ignoreNull() || StringUtils.hasText(dictValueText)) {
                        texts.add(dictValueText);
                    }
                }
            } else {
                texts = Collections.emptyList();
            }
        } else {
            logger.warn("{}#{} = {} 不是一个字符串类型的字段，无法使用分隔数组功能", beanClazz, beanFieldName, fieldValue);
            texts = Collections.emptyList();
        }

        return obtainResults(texts);
    }

    private Object obtainResults(final List<String> texts) {
        if (array.toText()) {
            if (texts.isEmpty()) {
                return null;
            }
            return String.join(array.joinSeparator(), texts);
        }

        return texts;
    }

    /**
     * 从数据字典中获取字典值对应的文本信息
     *
     * @param dictValue 字典值
     * @return 字典值文本
     */
    protected String obtainDictValueText(String dictValue) {
        return DictUtil.getDictText(dictType, dictValue);
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
        if (isMapValue()) {
            final Map<String, Object> map = new HashMap<>();
            map.put("value", fieldValue);
            map.put("text", dictValueText);
            if (hasFieldName) {
                writeFieldValue(fieldValue, gen);
                gen.writeFieldName(dictText.fieldName());
            }
            gen.writeObject(map);
        } else {
            writeFieldValue(fieldValue, gen);
            gen.writeFieldName(destinationFieldName);
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
            gen.writeString(String.valueOf(fieldValue));
        }
    }

    /**
     * 获取默认值
     *
     * @param value 原始值
     * @return 处理结果
     */
    protected Object defaultNullableValue(Object value) {
        if (isNullableValue()) {
            return value;
        }
        return value == null ? "" : value;
    }

    private boolean isMapValue() {
        if (dictText.mapValue() == DictText.Type.YES) {
            return true;
        }
        return dictText.mapValue() == DictText.Type.GLOBAL && SystemDictStarter.isMapValue();
    }

    private boolean isNullableValue() {
        if (dictText.nullable() == DictText.Type.YES) {
            return true;
        }
        return dictText.nullable() == DictText.Type.GLOBAL && SystemDictStarter.isTextValueDefaultNull();
    }
}
