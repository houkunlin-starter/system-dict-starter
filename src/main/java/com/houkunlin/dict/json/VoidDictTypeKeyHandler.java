package com.houkunlin.dict.json;

import com.houkunlin.dict.annotation.DictText;

/**
 * DictTypeKeyHandler 默认实现
 *
 * @author HouKunLin
 * @since 1.4.7
 */
public class VoidDictTypeKeyHandler implements DictTypeKeyHandler<Object> {
    @Override
    public String getDictType(final Object bean, final String fieldName, final DictText dictText) {
        return null;
    }
}
