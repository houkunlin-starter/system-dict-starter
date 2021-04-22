package com.houkunlin.system.dic.starter;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * 数据字典枚举接口。系统字典枚举接口
 *
 * @author HouKunLin
 */
public interface IDicEnums<T extends Serializable> {
    /**
     * 字典值
     *
     * @return 字典值
     */
    @JsonValue
    T getValue();

    /**
     * 字典文本
     *
     * @return 字典文本
     */
    String getTitle();

    /**
     * 通过枚举值从枚举列表中获取枚举对象
     *
     * @param values 枚举对象列表
     * @param value  枚举值
     * @param <T>    枚举值类型
     * @return 枚举对象
     */
    static <T extends Serializable, E extends Enum<E> & IDicEnums<T>> E valueOf(E[] values, T value) {
        for (final E enums : values) {
            if (enums.getValue().equals(value)) {
                return enums;
            }
        }
        return null;
    }
}
