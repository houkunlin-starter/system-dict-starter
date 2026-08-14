package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用布尔类型字典枚举（3）
 * <p>
 * 用于测试布尔类型字典值的序列化与反序列化功能。
 * </p>
 *
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum BooleanEnum3 implements DictEnum<Boolean> {
    ITEM1(true, "选项1"),
    ITEM2(false, "选项2");
    private final Boolean value;
    private final String title;

    @JsonCreator
    public static BooleanEnum3 fromValue(Boolean value) {
        return DictEnum.valueOf(values(), value);
    }
}
