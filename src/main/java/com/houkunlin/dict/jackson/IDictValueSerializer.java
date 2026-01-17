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

public interface IDictValueSerializer {
    /**
     * 是否忽略 null 值
     * <p>
     * 如果为true，当字段值为null时，不进行序列化输出
     * </p>
     */
    boolean isTextNullable();

    /**
     * 字典类型键处理器
     * <p>
     * 用于动态计算字典类型的处理器，支持运行时字典类型计算
     * </p>
     */
    DictTypeKeyHandler<Object> getDictTypeKeyHandler();


    default String getDictType(Object bean, String fieldName, DictText dictText) {
        DictTypeKeyHandler<Object> dictTypeKeyHandler = getDictTypeKeyHandler();
        if (dictTypeKeyHandler == null) {
            return dictText.value();
        }
        return dictTypeKeyHandler.getDictType(bean, fieldName, dictText);
    }

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

    default String getDictParentValue(final Object bean, String fieldName, final Object value, DictText dictText, final String dictType, final String arrayItemValue) {
        DictTypeKeyHandler<Object> dictTypeKeyHandler = getDictTypeKeyHandler();
        if (dictTypeKeyHandler == null) {
            return DictUtil.getDictParentValue(dictType, arrayItemValue);
        }
        return dictTypeKeyHandler.getDictParentValue(bean, fieldName, value, dictText, dictType, arrayItemValue);
    }

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
