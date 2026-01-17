package com.houkunlin.dict.jackson;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.enums.NullStrategy;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import java.util.*;

public class DictValueSerializerV2Impl extends DictValueSerializerV2 {
    private static final Logger logger = LoggerFactory.getLogger(DictValueSerializerV2Impl.class);

    public DictValueSerializerV2Impl(String fieldName, Class<?> javaTypeRawClass, DictText dictText, DictArray dictArray, DictTree dictTree) {
        super(fieldName, javaTypeRawClass, dictText, dictArray, dictTree);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (!useReplaceFieldValue) {
            DICT_WRITER.writeDictValue(gen, value, dictText, useRawValueType);
            gen.writeName(outputFieldName);
        }
        if (useMap) {
            gen.writeStartObject();
            gen.writeName("value");
            DICT_WRITER.writeDictValue(gen, value, dictText, useRawValueType);
            gen.writeName("text");
        }
        if (value != null) {
            Object bean = gen.currentValue();
            String dictType = getDictType(bean);
            // serialize(gen, bean, value, dictType, value, false);


            if (value.getClass().isArray()) {
                if (dictArrayToText) {
                    List<String> list = new ArrayList<>();
                    for (Object o : (Object[]) value) {
                        if (dictTree == null) {
                            if (o instanceof DictEnum<?> dictEnum) {
                                appendDictText(list, dictEnum.getTitle());
                            } else {
                                String s = getDictText(bean, value, dictType, String.valueOf(o));
                                appendDictText(list, s);
                            }
                        } else {
                            List<String> treeDictText = getTreeDictText(bean, value, dictType, String.valueOf(o));
                            list.add(String.join(dictTreeDelimiter, treeDictText));
                        }
                    }
                    gen.writeString(String.join(dictArrayDelimiter, list));
                } else {
                    gen.writeStartArray();
                    for (Object o : (Object[]) value) {
                        serialize(gen, bean, value, dictType, o);
                    }
                    gen.writeEndArray();
                }
            } else if (value instanceof Collection<?> v) {
                if (dictArrayToText) {
                    List<String> list = new ArrayList<>();
                    for (Object o : v) {
                        if (dictTree == null) {
                            if (o instanceof DictEnum<?> dictEnum) {
                                appendDictText(list, dictEnum.getTitle());
                            } else {
                                String s = getDictText(bean, value, dictType, String.valueOf(o));
                                appendDictText(list, s);
                            }
                        } else {
                            List<String> treeDictText = getTreeDictText(bean, value, dictType, String.valueOf(o));
                            list.add(String.join(dictTreeDelimiter, treeDictText));
                        }
                    }
                    gen.writeString(String.join(dictArrayDelimiter, list));
                } else {
                    gen.writeStartArray();
                    for (Object o : v) {
                        serialize(gen, bean, value, dictType, o);
                    }
                    gen.writeEndArray();
                }
            } else if (value instanceof Iterable<?> v) {
                if (dictArrayToText) {
                    List<String> list = new ArrayList<>();
                    for (Object o : v) {
                        if (dictTree == null) {
                            if (o instanceof DictEnum<?> dictEnum) {
                                appendDictText(list, dictEnum.getTitle());
                            } else {
                                String s = getDictText(bean, value, dictType, String.valueOf(o));
                                appendDictText(list, s);
                            }
                        } else {
                            List<String> treeDictText = getTreeDictText(bean, value, dictType, String.valueOf(o));
                            list.add(String.join(dictTreeDelimiter, treeDictText));
                        }
                    }
                    gen.writeString(String.join(dictArrayDelimiter, list));
                } else {
                    gen.writeStartArray();
                    for (Object o : v) {
                        serialize(gen, bean, value, dictType, o);
                    }
                    gen.writeEndArray();
                }
            } else if (value instanceof DictEnum<?> v) {
                gen.writeString(v.getTitle());
            } else if (javaTypeRawClass.isEnum()) {
                logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
                gen.writeString("");
                // } else if (javaType.isEnumImplType()) {
                //     logger.warn("不支持 EnumImpl 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
                //     gen.writeString("");
            } else if (value instanceof Map<?, ?>) {
                logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
                gen.writeString("");
            } else if (value instanceof CharSequence v && !dictArraySplit.isEmpty()) {
                if (dictTree == null) {
                    List<String> dictTextList = new ArrayList<>();
                    String[] split = ObjectUtils.getDisplayString(v).split(dictArraySplit);
                    for (String o : split) {
                        String s = getDictText(bean, value, dictType, o);
                        appendDictText(dictTextList, s);
                    }
                    writeDictTextArray(gen, dictTextList);
                } else {
                    List<List<String>> dictTextList = new ArrayList<>();
                    String[] split = ObjectUtils.getDisplayString(v).split(dictArraySplit);
                    for (String s : split) {
                        dictTextList.add(getTreeDictText(bean, value, dictType, s));
                    }
                    writeDictTextArrayTree(gen, dictTextList);
                }
            } else {
                if (dictTree == null) {
                    String s = getDictText(bean, value, dictType, value.toString());
                    if (s != null) {
                        gen.writeString(s);
                    } else if (textNullable) {
                        gen.writeNull();
                    } else {
                        gen.writeString("");
                    }
                } else {
                    List<String> treeDictText = getTreeDictText(bean, value, dictType, value.toString());
                    writeDictTextArrayTree0(gen, treeDictText);
                }
            }


        } else {
            if (textNullable) {
                gen.writeNull();
            } else if (dictArray != null && (
                javaTypeRawClass.isArray() ||
                    Collection.class.isAssignableFrom(javaTypeRawClass) ||
                    Iterable.class.isAssignableFrom(javaTypeRawClass))) {
                if (dictArrayToText) {
                    gen.writeString("");
                } else {
                    gen.writeStartArray();
                    gen.writeEndArray();
                }
            } else if (Map.class.isAssignableFrom(javaTypeRawClass)) {
                gen.writeStartObject();
                gen.writeEndObject();
            } else if (dictArray != null) {
                if (dictArrayToText) {
                    gen.writeString("");
                } else {
                    gen.writeStartArray();
                    gen.writeEndArray();
                }
            } else {
                gen.writeString("");
            }
        }
        if (useMap) {
            gen.writeEndObject();
        }
    }

    public void serialize(JsonGenerator gen, Object bean, Object value, String dictType, Object arrayItemValue) {
        if (arrayItemValue == null) {
            if (dictArray != null) {
                if (dictArrayNullStrategy == NullStrategy.NULL) {
                    gen.writeNull();
                } else if (dictArrayNullStrategy == NullStrategy.EMPTY) {
                    gen.writeString("");
                }
            } else {
                gen.writeNull();
            }
            return;
        }
        if (arrayItemValue.getClass().isArray()) {
            gen.writeStartArray();
            for (Object o : (Object[]) value) {
                serialize(gen, bean, value, dictType, o);
            }
            gen.writeEndArray();
        } else if (arrayItemValue instanceof Collection<?> v) {
            gen.writeStartArray();
            for (Object o : v) {
                serialize(gen, bean, value, dictType, o);
            }
            gen.writeEndArray();
        } else if (arrayItemValue instanceof Iterable<?> v) {
            gen.writeStartArray();
            for (Object o : v) {
                serialize(gen, bean, value, dictType, o);
            }
            gen.writeEndArray();
        } else if (arrayItemValue instanceof DictEnum<?> v) {
            gen.writeString(v.getTitle());
        } else if (javaTypeRawClass.isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            gen.writeString("");
            // } else if (javaType.isEnumImplType()) {
            //     logger.warn("不支持 EnumImpl 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            //     gen.writeString("");
        } else if (arrayItemValue instanceof Map<?, ?>) {
            logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            gen.writeString("");
        } else if (arrayItemValue instanceof CharSequence v && !dictArraySplit.isEmpty()) {
            if (dictTree == null) {
                List<String> dictTextList = new ArrayList<>();
                String[] split = ObjectUtils.getDisplayString(v).split(dictArraySplit);
                for (String s : split) {
                    String s1 = getDictText(bean, value, dictType, s);
                    if (s1 != null) {
                        dictTextList.add(s1);
                    } else if (dictArrayNullStrategy == NullStrategy.NULL) {
                        dictTextList.add(null);
                    } else if (dictArrayNullStrategy == NullStrategy.EMPTY) {
                        dictTextList.add("");
                    }
                }
                writeDictTextArray(gen, dictTextList);
            } else {
                List<List<String>> dictTextList = new ArrayList<>();
                String[] split = ObjectUtils.getDisplayString(v).split(dictArraySplit);
                for (String s : split) {
                    dictTextList.add(getTreeDictText(bean, value, dictType, s));
                }
                writeDictTextArrayTree(gen, dictTextList);
            }
        } else {
            if (dictTree == null) {
                String s = getDictText(bean, value, dictType, arrayItemValue.toString());
                if (s != null) {
                    gen.writeString(s);
                } else if (dictArray != null) {
                    if (dictArrayNullStrategy == NullStrategy.NULL) {
                        gen.writeNull();
                    } else if (dictArrayNullStrategy == NullStrategy.EMPTY) {
                        gen.writeString("");
                    }
                } else {
                    gen.writeNull();
                }
            } else {
                List<String> treeDictText = getTreeDictText(bean, value, dictType, arrayItemValue.toString());
                writeDictTextArrayTree0(gen, treeDictText);
            }
        }
    }

    public Object serialize(final Object bean, @Nullable final Object fieldValue) {
        if (fieldValue != null) {
            String dictType = getDictType(bean);


            if (fieldValue.getClass().isArray()) {
                List<Object> list = new ArrayList<>();
                for (Object o : (Object[]) fieldValue) {
                    list.add(serialize(bean, fieldValue, dictType, o));
                }
                return list;
            } else if (fieldValue instanceof Collection<?> v) {
                List<Object> list = new ArrayList<>();
                for (Object o : v) {
                    list.add(serialize(bean, fieldValue, dictType, o));
                }
                return list;
            } else if (fieldValue instanceof Iterable<?> v) {
                List<Object> list = new ArrayList<>();
                for (Object o : v) {
                    list.add(serialize(bean, fieldValue, dictType, o));
                }
                return list;
            } else if (fieldValue instanceof DictEnum<?> v) {
                return v.getTitle();
            } else if (javaTypeRawClass.isEnum()) {
                logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, fieldValue);
                return "";
                // } else if (javaType.isEnumImplType()) {
                //     logger.warn("不支持 EnumImpl 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
                //     gen.writeString("");
            } else if (fieldValue instanceof Map<?, ?>) {
                logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, fieldValue);
                return "";
            } else if (fieldValue instanceof CharSequence v && !dictArraySplit.isEmpty()) {
                if (dictTree == null) {
                    List<String> dictTextList = new ArrayList<>();
                    String[] split = ObjectUtils.getDisplayString(v).split(dictArraySplit);
                    for (String s : split) {
                        String s1 = getDictText(bean, fieldValue, dictType, s);
                        if (s1 != null) {
                            dictTextList.add(s1);
                        } else if (dictArrayNullStrategy == NullStrategy.NULL) {
                            dictTextList.add(null);
                        } else if (dictArrayNullStrategy == NullStrategy.EMPTY) {
                            dictTextList.add("");
                        }
                    }
                    return getDictTextArray(dictTextList);
                } else {
                    List<List<String>> dictTextList = new ArrayList<>();
                    String[] split = ObjectUtils.getDisplayString(v).split(dictArraySplit);
                    for (String s : split) {
                        dictTextList.add(getTreeDictText(bean, fieldValue, dictType, s));
                    }
                    return getDictTextArrayTree(dictTextList);
                }
            } else {
                if (dictTree == null) {
                    String s = getDictText(bean, fieldValue, dictType, fieldValue.toString());
                    if (s != null) {
                        return s;
                    } else if (textNullable) {
                        return null;
                    } else {
                        return "";
                    }
                } else {
                    List<String> treeDictText = getTreeDictText(bean, fieldValue, dictType, fieldValue.toString());
                    return getDictTextArrayTree0(treeDictText);
                }
            }

        } else {
            if (textNullable) {
                return null;
            } else if (dictArray != null && (
                javaTypeRawClass.isArray() ||
                    Collection.class.isAssignableFrom(javaTypeRawClass) ||
                    Iterable.class.isAssignableFrom(javaTypeRawClass))) {
                return Collections.emptyList();
            } else if (Map.class.isAssignableFrom(javaTypeRawClass)) {
                return Collections.emptyMap();
            } else if (dictArray != null) {
                if (dictArrayToText) {
                    return "";
                } else {
                    return Collections.emptyList();
                }
            } else {
                return "";
            }
        }
    }

    public Object serialize(Object bean, Object value, String dictType, Object arrayItemValue) {
        if (arrayItemValue.getClass().isArray()) {
            List<Object> list = new ArrayList<>();
            for (Object o : (Object[]) value) {
                list.add(serialize(bean, value, dictType, o));
            }
            return list;
        } else if (arrayItemValue instanceof Collection<?> v) {
            List<Object> list = new ArrayList<>();
            for (Object o : v) {
                list.add(serialize(bean, value, dictType, o));
            }
            return list;
        } else if (arrayItemValue instanceof Iterable<?> v) {
            List<Object> list = new ArrayList<>();
            for (Object o : v) {
                list.add(serialize(bean, value, dictType, o));
            }
            return list;
        } else if (arrayItemValue instanceof DictEnum<?> v) {
            return v.getTitle();
        } else if (javaTypeRawClass.isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            return "";
            // } else if (javaType.isEnumImplType()) {
            //     logger.warn("不支持 EnumImpl 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            //     return "";
        } else if (value instanceof Map<?, ?>) {
            logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            return "";
        } else if (arrayItemValue instanceof CharSequence v && !dictArraySplit.isEmpty()) {
            if (dictTree == null) {
                List<String> dictTextList = new ArrayList<>();
                String[] split = ObjectUtils.getDisplayString(v).split(dictArraySplit);
                for (String s : split) {
                    String s1 = getDictText(bean, value, dictType, s);
                    if (s1 != null) {
                        dictTextList.add(s1);
                    } else if (dictArrayNullStrategy == NullStrategy.NULL) {
                        dictTextList.add(null);
                    } else if (dictArrayNullStrategy == NullStrategy.EMPTY) {
                        dictTextList.add("");
                    }
                }
                return getDictTextArray(dictTextList);
            } else {
                List<List<String>> dictTextList = new ArrayList<>();
                String[] split = ObjectUtils.getDisplayString(v).split(dictArraySplit);
                for (String s : split) {
                    dictTextList.add(getTreeDictText(bean, value, dictType, s));
                }
                return getDictTextArrayTree(dictTextList);
            }
        } else {
            if (dictTree == null) {
                String s = getDictText(bean, value, dictType, arrayItemValue.toString());
                if (s != null) {
                    return s;
                } else if (dictArray != null) {
                    if (dictArrayNullStrategy == NullStrategy.NULL) {
                        return null;
                    } else if (dictArrayNullStrategy == NullStrategy.EMPTY) {
                        return "";
                    }
                }
                return null;
            } else {
                List<String> treeDictText = getTreeDictText(bean, value, dictType, arrayItemValue.toString());
                return getDictTextArrayTree0(treeDictText);
            }
        }
    }

    public void writeDictTextArray0(JsonGenerator gen, List<String> dictTextList) throws JacksonException {
        gen.writeStartArray();
        for (String dictText : dictTextList) {
            gen.writeString(dictText);
        }
        gen.writeEndArray();
    }

    public Object getDictTextArray0(List<String> dictTextList) throws JacksonException {
        return dictTextList;
    }

    public void writeDictTextArray1(JsonGenerator gen, List<String> dictTextList, NullStrategy nullStrategy, boolean toText, String joinSeparator) throws JacksonException {
        if (nullStrategy == NullStrategy.IGNORE) {
            dictTextList.removeIf(Objects::isNull);
        }
        if (toText) {
            gen.writeString(String.join(joinSeparator, dictTextList));
        } else {
            gen.writeStartArray();
            for (String dictText : dictTextList) {
                gen.writeString(dictText);
            }
            gen.writeEndArray();
        }
    }

    public Object getDictTextArray1(List<String> dictTextList, NullStrategy nullStrategy, boolean toText, String joinSeparator) throws JacksonException {
        if (nullStrategy == NullStrategy.IGNORE) {
            dictTextList.removeIf(Objects::isNull);
        }
        if (toText) {
            return String.join(joinSeparator, dictTextList);
        } else {
            return dictTextList;
        }
    }

    public void writeDictTextArray(JsonGenerator gen, List<String> dictTextList) throws JacksonException {
        if (dictArray == null) {
            writeDictTextArray0(gen, dictTextList);
        } else {
            writeDictTextArray1(gen, dictTextList, dictArrayNullStrategy, dictArrayToText, dictArrayDelimiter);
        }
    }

    public Object getDictTextArray(List<String> dictTextList) throws JacksonException {
        if (dictArray == null) {
            return getDictTextArray0(dictTextList);
        } else {
            return getDictTextArray1(dictTextList, dictArrayNullStrategy, dictArrayToText, dictArrayDelimiter);
        }
    }

    public void writeDictTextArrayTree0(JsonGenerator gen, List<String> dictTextList) throws JacksonException {
        // 不用再判断 dictTree 是否为 null，因为如果为 null，就不会调用到这个方法
        writeDictTextArray1(gen, dictTextList, dictTreeNullStrategy, dictTreeToText, dictTreeDelimiter);
    }

    public Object getDictTextArrayTree0(List<String> dictTextList) throws JacksonException {
        // 不用再判断 dictTree 是否为 null，因为如果为 null，就不会调用到这个方法
        return getDictTextArray1(dictTextList, dictTreeNullStrategy, dictTreeToText, dictTreeDelimiter);
    }

    public void writeDictTextArrayTree(JsonGenerator gen, List<List<String>> dictTextList) throws JacksonException {
        gen.writeStartArray();
        for (List<String> dictText : dictTextList) {
            writeDictTextArrayTree0(gen, dictText);
        }
        gen.writeEndArray();
    }

    public Object getDictTextArrayTree(List<List<String>> dictTextList) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (List<String> dictText : dictTextList) {
            list.add(getDictTextArrayTree0(dictText));
        }
        return list;
    }
}
