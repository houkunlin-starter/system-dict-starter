package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试用长整型类型字典枚举（3）
 * <p>
 * 用于测试长整型类型字典值的序列化与反序列化功能。
 * </p>
 *
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum LongEnum3 implements DictEnum<Long> {
    ITEM1(1L, "选项1"),
    ITEM2(2L, "选项2"),
    ITEM3(3L, "选项3");
    private final Long value;
    private final String title;

    @JsonCreator
    public static LongEnum3 fromValue(Long value) {
        return DictEnum.valueOf(values(), value);
    }
}
