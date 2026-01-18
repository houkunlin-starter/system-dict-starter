package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictText;

/**
 * DictTypeKeyHandler 默认实现，用于处理字典类型的键。
 * <p>
 * 该实现是一个空实现，总是返回 null，表示使用默认的字典类型处理逻辑。
 * 当不需要自定义字典类型获取逻辑时，可以使用此实现。
 * </p>
 *
 * @author HouKunLin
 * @since 1.4.7
 */
public class DictTypeKeyHandlerVoidImpl implements DictTypeKeyHandler<Object> {
    /**
     * 获取字典类型。
     * <p>
     * 空实现，总是返回 null，表示使用默认的字典类型处理逻辑。
     * </p>
     *
     * @param bean      Bean 对象
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @return 总是返回 null
     */
    @Override
    public String getDictType(final Object bean, final String fieldName, final DictText dictText) {
        return null;
    }
}
