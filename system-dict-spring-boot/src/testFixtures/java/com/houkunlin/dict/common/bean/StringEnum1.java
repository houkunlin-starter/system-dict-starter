package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用字符串类型字典枚举（1）
 * <p>
 * 用于测试字符串类型字典值的序列化与反序列化功能。
 * </p>
 *
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum StringEnum1 implements DictEnum<String> {
    ITEM1("1", "选项1"),
    ITEM2("2", "选项2"),
    ITEM3("3", "选项3");
    private final String value;
    private final String title;

    @JsonCreator
    public static StringEnum1 fromValue(String value) {
        return DictEnum.valueOf(values(), value);
    }
}
