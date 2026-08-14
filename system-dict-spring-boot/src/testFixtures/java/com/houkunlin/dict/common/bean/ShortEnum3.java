package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用短整型类型字典枚举（3）
 * <p>
 * 用于测试短整型类型字典值的序列化与反序列化功能。
 * </p>
 *
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum ShortEnum3 implements DictEnum<Short> {
    ITEM1((short) 1, "选项1"),
    ITEM2((short) 2, "选项2"),
    ITEM3((short) 3, "选项3");
    private final Short value;
    private final String title;

    @JsonCreator
    public static ShortEnum3 fromValue(Short value) {
        return DictEnum.valueOf(values(), value);
    }
}
