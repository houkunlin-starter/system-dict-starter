package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用双精度类型字典枚举（2）
 * <p>
 * 用于测试双精度类型字典值的序列化与反序列化功能。
 * </p>
 *
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum DoubleEnum2 implements DictEnum<Double> {
    ITEM1(1.0D, "选项1"),
    ITEM2(2.0D, "选项2"),
    ITEM3(3.0D, "选项3");
    private final Double value;
    private final String title;

    @JsonCreator
    public static DoubleEnum2 fromValue(Double value) {
        return DictEnum.valueOf(values(), value);
    }
}
