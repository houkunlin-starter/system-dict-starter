package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用普通枚举（1），不实现字典枚举接口
 * <p>
 * 用于测试普通枚举类型字典值的序列化与反序列化功能。
 * </p>
 *
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum SimpleEnum1 {
    ITEM1("1", "选项1"),
    ITEM2("2", "选项2"),
    ITEM3("3", "选项3");
    private final String value;
    private final String title;

    @JsonCreator
    public static SimpleEnum1 fromValue(String value) {
        for (SimpleEnum1 enum1 : values()) {
            if (enum1.value.equals(value)) {
                return enum1;
            }
        }
        return null;
    }
}
