package com.houkunlin.system.dic.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.houkunlin.system.dic.starter.DicEnum;
import com.houkunlin.system.dic.starter.DicUtil;
import com.houkunlin.system.dic.starter.SystemDicStarter;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DicText 注解的序列化器，处理 DicText 注解，把字段的数据字典值转换成为数据字典文本信息
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@Getter
public class DicTextJsonSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private static final Logger logger = LoggerFactory.getLogger(DicTextJsonSerializer.class);
    private static final ConcurrentHashMap<String, JsonSerializer<Object>> CACHE = new ConcurrentHashMap<>();
    /**
     * 使用了这个注解的对象
     */
    private final Class<?> beanClazz;
    /**
     * 使用了这个注解的字段名称
     */
    private final String beanFieldName;
    /**
     * 字典输出字段名称
     */
    private final String destinationFieldName;
    /**
     * 字典转换注解对象
     */
    private final DicText dicText;
    /**
     * 字典类型代码
     */
    private final String dicType;
    /**
     * 直接使用系统字典枚举的枚举对象列表
     */
    private final Class<? extends DicEnum<?>>[] enumsClass;
    /**
     * 缓存了直接使用系统字典枚举来渲染数据字典文本的所有数据
     */
    private static final Table<Class<? extends DicEnum<?>>, Serializable, String> CACHE_ENUMS = HashBasedTable.create();

    public DicTextJsonSerializer() {
        this.beanClazz = null;
        this.beanFieldName = null;
        this.destinationFieldName = null;
        this.dicText = null;
        this.dicType = null;
        this.enumsClass = null;
    }

    public DicTextJsonSerializer(Class<?> beanClazz, String beanFieldName, DicText dicText) {
        this.beanClazz = beanClazz;
        this.beanFieldName = beanFieldName;
        this.dicText = dicText;
        this.dicType = dicText.value();
        this.destinationFieldName = getFieldName(dicText);
        this.enumsClass = dicText.enums();
        if (this.enumsClass.length == 0) {
            return;
        }
        initEnumsClass();
    }

    public DicTextJsonSerializer(Class<?> beanClazz, String beanFieldName, Class<? extends DicEnum<?>>[] enumsClass) {
        this.beanClazz = beanClazz;
        this.beanFieldName = beanFieldName;
        this.dicText = null;
        this.dicType = null;
        this.destinationFieldName = beanFieldName + "Text";
        this.enumsClass = enumsClass;
        if (this.enumsClass.length == 0) {
            return;
        }
        initEnumsClass();
    }

    public DicTextJsonSerializer(Class<?> beanClazz, String beanFieldName, DicText dicText, Class<? extends DicEnum<?>>[] enumsClass) {
        this.beanClazz = beanClazz;
        this.beanFieldName = beanFieldName;
        this.dicText = dicText;
        this.dicType = dicText.value();
        this.destinationFieldName = getFieldName(dicText);
        this.enumsClass = enumsClass;
        if (this.enumsClass.length == 0) {
            return;
        }
        initEnumsClass();
    }

    private String getFieldName(DicText dicText) {
        final String fieldName = dicText.fieldName();
        if (StringUtils.hasText(fieldName)) {
            return fieldName;
        } else {
            return beanFieldName + "Text";
        }
    }

    private void initEnumsClass() {
        // 解析系统字典枚举列表
        for (final Class<? extends DicEnum<?>> enumClass : this.enumsClass) {
            if (!enumClass.isEnum()) {
                continue;
            }
            final DicEnum<?>[] enumConstants = enumClass.getEnumConstants();
            // 解析枚举对象枚举列表
            for (DicEnum<?> enums : enumConstants) {
                // 缓存系统字典枚举对象的解析数据
                CACHE_ENUMS.put(enumClass, String.valueOf(enums.getValue()), enums.getTitle());
            }
        }
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (formFieldEnumsClass(value, gen)) {
            return;
        }
        if (formDicTextEnumsClass(value, gen)) {
            return;
        }
        formDicCache(value, gen);
    }

    /**
     * 字段是系统字典枚举对象
     *
     * @param value 实体类字段值，此时该值可能是一个系统字典枚举对象
     * @param gen
     * @return 是否设置成功
     */
    private boolean formFieldEnumsClass(Object value, JsonGenerator gen) throws IOException {
        if (value instanceof DicEnum) {
            final DicEnum enums = (DicEnum) value;
            final String title = getTitleFormClass(enums.getValue());
            if (title == null) {
                logger.warn("{}#{} = {} 本身是一个 系统字典枚举对象，但是由于未找到其值因而会进行进一步的信息获取。实际上这里不应该发生的", beanClazz, beanFieldName, value);
                return false;
            }
            writeFieldValue(gen, enums.getValue(), defaultValue(title));
            return true;
        }
        return false;
    }

    /**
     * 字段是普通类型，但是使用 @DicText 标记了来自枚举对象取值
     *
     * @param value 实体类字段值
     * @param gen
     * @return
     */
    private boolean formDicTextEnumsClass(Object value, JsonGenerator gen) throws IOException {
        if (enumsClass != null && enumsClass.length > 0) {
            final String title = getTitleFormClass(value);
            if (title == null) {
                logger.warn("{}#{} = {} 指定了从多个字典枚举中取值，但是由于未找到其值因而会进行进一步的信息获取。实际上这里不应该发生的", beanClazz, beanFieldName, value);
                return false;
            }
            writeFieldValue(gen, value, defaultValue(title));
            return true;
        }
        return false;
    }

    /**
     * 从缓存中获取字典文本
     *
     * @param value 字典值对象
     * @param gen
     * @throws IOException
     */
    private void formDicCache(Object value, JsonGenerator gen) throws IOException {
        if (dicType != null && StringUtils.hasText(dicType)) {
            writeFieldValue(gen, value, DicUtil.getDicValueTitle(dicType, String.valueOf(value)));
        } else {
            writeFieldValue(gen, value, defaultValue(null));
            logger.warn("{}#{} @DicText annotation not set dicType value", beanClazz, beanFieldName);
        }
    }

    /**
     * 把字段字典值写入到JSON数据中
     *
     * @param fieldValue 字段值
     * @param gen
     * @throws IOException
     */
    private void writeFieldValue(Object fieldValue, JsonGenerator gen) throws IOException {
        if (SystemDicStarter.isRawValue()) {
            gen.writeObject(fieldValue);
        } else {
            gen.writeString(String.valueOf(fieldValue));
        }
    }

    /**
     * 把数据字典原始值和转换后的字典文本值写入到 Json 中
     *
     * @param gen            JsonGenerator 对象
     * @param rawValueObject 实体类字典值
     * @param dicValueText   字典文本值
     * @throws IOException
     */
    private void writeFieldValue(JsonGenerator gen, Object rawValueObject, Object dicValueText) throws IOException {
        if (dicText == null) {
            writeFieldValue(rawValueObject, gen);
            return;
        }
        if (dicText.mapValue() == DicText.Type.YES || (SystemDicStarter.isMapValue() && dicText.mapValue() == DicText.Type.GLOBAL)) {
            final Map<String, Object> map = new HashMap<>();
            map.put("value", rawValueObject);
            map.put("text", dicValueText);
            if (StringUtils.hasText(dicText.fieldName())) {
                writeFieldValue(rawValueObject, gen);
                gen.writeFieldName(dicText.fieldName());
                gen.writeObject(map);
            } else {
                gen.writeObject(map);
            }
        } else {
            writeFieldValue(rawValueObject, gen);
            gen.writeFieldName(destinationFieldName);
            gen.writeObject(dicValueText);
        }
    }

    /**
     * 从系统字典枚举对象中获取字典文本信息
     *
     * @param value 字典值
     * @return 字典文本
     * @throws IOException
     */
    private String getTitleFormClass(Object value) throws IOException {
        assert enumsClass != null;

        String cacheTitle = null;
        for (final Class<? extends DicEnum<?>> aClass : enumsClass) {
            cacheTitle = CACHE_ENUMS.get(aClass, String.valueOf(value));
            if (cacheTitle != null) {
                break;
            }
        }
        return cacheTitle;
    }

    private Object defaultValue(Object value) {
        if (dicText != null) {
            if (dicText.nullable() == DicText.Type.GLOBAL) {
                return defaultValue(value, SystemDicStarter.isTextValueDefaultNull());
            }
            return defaultValue(value, dicText.nullable() == DicText.Type.YES);
        }
        return defaultValue(value, SystemDicStarter.isTextValueDefaultNull());
    }

    /**
     * 获取默认值
     *
     * @param value    原始值
     * @param nullable 是否为null
     * @return 处理结果
     */
    private Object defaultValue(Object value, boolean nullable) {
        if (nullable) {
            return value;
        }
        if (value == null) {
            return "";
        }
        return value;
    }

    /**
     * property.getMember().getDeclaringClass() 是这个实体类的对象class
     * property.member 是一个 AnnotationMethod 对象，字段名一致的时候 property.member.hashCode() 是相同的
     * 当字段名相同的时候 property.hashCode() 是相同的
     * 当 字段名 相同的时候：property.member.hashCode() 是相同的
     * 当 DicText 参数全部相同的时候：annotation.hashCode() 是相同的
     * 因此 property.member 和 annotation 都不能作为 CacheMap 的 Key
     * 否则导致不同 field 的 DicText 注解可能只有一个 JsonSerializer 对象，从而导致 DicJsonSerializer 参数出现错误、冲突，因此Json数据错误
     * property.member.member 是这个字段对应的 Getter 方法，因此能够保证唯一
     * 但是想要更优化的缓存，需要把 Key 设置为：字段名 + 注解对象 两个合并使用，才能保证唯一缓存
     * 这样可以由相同的 field 加相同的 DicText 共用同一个 JsonSerializer 对象，当任何一个参数不同时都会重新创建一个 JsonSerializer 对象
     * 方案一： property.getMember().getMember() 对象是字段的 Getter 方法对象
     * 当 DicText 参数一致的时候， DicText.hashCode() 是相同的
     * 因此导致不同 field 的 DicText 注解可能只有一个 JsonSerializer 对象，从而导致数据字典 JSON 字段名称出现错误、冲突
     * 方案二：此时需要把 field 加入到 cache 的 key 中
     * 这样可以由相同的 field 加相同的 DicText 共用同一个 JsonSerializer 对象，当任何一个参数不同时都会重新创建一个 JsonSerializer 对象
     *
     * @param prov
     * @param property
     * @return
     * @throws JsonMappingException
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        // 为空直接跳过
        if (property != null) {
            final JavaType javaType = property.getType();
            final String fieldName = property.getName();
            final Class<?> javaTypeRawClass = javaType.getRawClass();

            // @DicText 注解目前仅对 字段、方法 起作用，因此这里拿到的注解一定是不为null的
            final DicText annotation = property.getAnnotation(DicText.class);

            // 直接使用系统字典对象作为字段类型，需要进行一个特殊的处理
            if (DicEnum.class.isAssignableFrom(javaTypeRawClass)) {
                final Class<? extends DicEnum<?>> aClass = (Class<? extends DicEnum<?>>) javaTypeRawClass;
                if (annotation != null) {
                    // @DicText 注解目前仅对 字段、方法 起作用，因此这个条件判断的内容一定是会执行的
                    return CACHE.computeIfAbsent(javaTypeRawClass.getName() + ":" + fieldName + annotation.hashCode(), key ->
                            new DicTextJsonSerializer(
                                    property.getMember().getDeclaringClass(),
                                    fieldName,
                                    annotation,
                                    new Class[]{aClass})
                    );
                }
                // 这里的代码实际已经过时，由于在本次提交，引入了 @DicType 注解来对系统字典进行自定义配置，因此实际上不会执行到这里。执行到这里是表示 @DicText 对整个类起了作用
                return CACHE.computeIfAbsent(javaTypeRawClass.getName() + ":" + fieldName, key ->
                        new DicTextJsonSerializer(
                                property.getMember().getDeclaringClass(),
                                fieldName,
                                new Class[]{aClass}));
            }
            if (annotation != null) {
                // @DicText 注解目前仅对 字段、方法 起作用，因此这个条件判断的内容一定是会执行的
                return CACHE.computeIfAbsent(fieldName + annotation.hashCode(), key ->
                        new DicTextJsonSerializer(
                                property.getMember().getDeclaringClass(),
                                fieldName,
                                annotation)
                );
            }
            try {
                // 这里的代码实际已经过时，由于在本次提交，引入了 @DicType 注解来对系统字典进行自定义配置，因此实际上不会执行到这里。执行到这里是表示 @DicText 对整个类起了作用
                return prov.findValueSerializer(javaType, property);
            } catch (JsonMappingException e) {
                throw new JsonMappingException(null, "无法解析 " + javaTypeRawClass + " 类型的字典序列化对象。由于在该对象上使用了 @DicText 注解，但其未实现 DicEnum 接口可能就会出现这个异常", e);
            }
        }
        return prov.findNullValueSerializer(null);
    }
}
