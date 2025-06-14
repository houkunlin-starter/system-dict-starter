package com.houkunlin.system.dict.starter;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * 数据字典枚举接口。系统字典枚举接口
 *
 * @author HouKunLin
 */
public interface DictEnum<T extends Serializable> {
    /**
     * 通过枚举值从枚举列表中获取枚举对象
     *
     * @param values 枚举对象列表
     * @param value  枚举值
     * @param <T>    枚举值类型
     * @param <E>    枚举对象类型
     * @return 枚举对象
     */
    static <T extends Serializable, E extends Enum<E> & DictEnum<T>> E valueOf(E[] values, T value) {
        for (final E enums : values) {
            if (enums.getValue().equals(value)) {
                return enums;
            }
        }
        return null;
    }

    /**
     * 父级字典值
     *
     * @return 父级字典值
     */
    default T getParentValue() {
        return null;
    }

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
     * 排序值
     *
     * @return 排序值
     */
    default int getSorted() {
        return 0;
    }

    /**
     * 是否禁用
     *
     * @return 是否禁用
     */
    default boolean isDisabled() {
        return false;
    }

    /**
     * 判断字典值是否相等
     *
     * @param o 传入的值，可为当前的枚举对象
     * @return 判断是否相等
     */
    default boolean eq(Object o) {
        return this == o || Objects.equal(o, getValue());
    }
}
