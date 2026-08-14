package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用字节类型字典枚举（3）
 * <p>
 * 用于测试字节类型字典值的序列化与反序列化功能。
 * </p>
 *
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum ByteEnum3 implements DictEnum<Byte> {
    ITEM1((byte) 1, "选项1"),
    ITEM2((byte) 2, "选项2"),
    ITEM3((byte) 3, "选项3");
    private final Byte value;
    private final String title;

    @JsonCreator
    public static ByteEnum3 fromValue(Byte value) {
        return DictEnum.valueOf(values(), value);
    }
}
