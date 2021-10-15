package com.houkunlin.system.dict.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
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
    protected final boolean dictTypeHas;

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
        this.beanFieldName = beanFieldName;
        this.dictText = dictText;
        this.dictType = dictText.value();
        this.dictTypeHas = StringUtils.hasText(dictType);
        this.destinationFieldName = getFieldName(dictText);
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
    public void serialize(@Nullable final Object value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        fromDictCache(value, gen);
    }

    /**
     * 从缓存中获取字典文本
     *
     * @param value 字典值对象
     * @param gen   JsonGenerator
     * @throws IOException 异常
     */
    protected void fromDictCache(@Nullable Object value, JsonGenerator gen) throws IOException {
        if (dictTypeHas) {
            writeFieldValue(gen, value, defaultNullableValue(obtainDictValueText(value)));
        } else {
            writeFieldValue(gen, value, defaultNullableValue(null));
            logger.warn("{}#{} @DictText annotation not set dictType value", beanClazz, beanFieldName);
        }
    }

    /**
     * 获取字段值的字典文本信息
     *
     * @param value 字段值（可能是一个需要分隔的字符串内容）
     * @return 字典值文本信息
     */
    protected Object obtainDictValueText(@Nullable Object value) {
        final String valueAsString = value == null ? "" : value.toString();
        final Array array = dictText.array();
        final String splitStr = array.split();

        if (Iterable.class.isAssignableFrom(fieldClazz)) {
            final List<String> texts = new ArrayList<>();
            if (value != null) {
                final Iterable<?> iterable = (Iterable<?>) value;
                iterable.forEach(o -> {
                    final String dictValueText = obtainDictValueText(String.valueOf(o));
                    if (!array.ignoreNull() || StringUtils.hasText(dictValueText)) {
                        texts.add(dictValueText);
                    }
                });
            }

            return obtainResults(array, texts);
        }

        if (fieldClazz.isArray()) {
            final List<String> texts = new ArrayList<>();
            if (value != null) {
                Object[] objects = (Object[]) value;
                for (final Object o : objects) {
                    final String dictValueText = obtainDictValueText(String.valueOf(o));
                    if (!array.ignoreNull() || StringUtils.hasText(dictValueText)) {
                        texts.add(dictValueText);
                    }
                }
            }

            return obtainResults(array, texts);
        }

        if (!StringUtils.hasText(splitStr)) {
            return obtainDictValueText(valueAsString);
        }

        final List<String> texts;
        if (CharSequence.class.isAssignableFrom(fieldClazz)) {
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
            logger.warn("{}#{} = {} 不是一个字符串类型的字段，无法使用分隔数组功能", beanClazz, beanFieldName, value);
            texts = Collections.emptyList();
        }

        return obtainResults(array, texts);
    }

    private Object obtainResults(final Array array, final List<String> texts) {
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
     * @param gen            JsonGenerator 对象
     * @param rawValueObject 实体类字典值
     * @param dictValueText  字典文本值
     * @throws IOException 异常
     */
    protected void writeFieldValue(JsonGenerator gen, @Nullable Object rawValueObject, Object dictValueText) throws IOException {
        if (isMapValue()) {
            final Map<String, Object> map = new HashMap<>();
            map.put("value", rawValueObject);
            map.put("text", dictValueText);
            if (StringUtils.hasText(dictText.fieldName())) {
                writeFieldValue(rawValueObject, gen);
                gen.writeFieldName(dictText.fieldName());
            }
            gen.writeObject(map);
        } else {
            writeFieldValue(rawValueObject, gen);
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
