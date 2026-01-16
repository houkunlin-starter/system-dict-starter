package com.houkunlin.system.dict.starter.json;

import com.houkunlin.system.dict.starter.annotation.DictText;

/**
 * DictTypeKeyHandler 默认实现
 *
 * @author HouKunLin
 * @since 1.4.7
 */
public class VoidDictTypeKeyHandler implements DictTypeKeyHandler<Object> {
    @Override
    public String getDictType(final Object bean, final String fieldName, final String fieldValueItem, final DictText dictText) {
        return null;
    }
}
