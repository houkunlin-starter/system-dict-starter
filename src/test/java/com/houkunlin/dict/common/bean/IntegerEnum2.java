package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IntegerEnum2 implements DictEnum<Integer> {
    ITEM1(1, "选项1"),
    ITEM2(2, "选项2"),
    ITEM3(3, "选项3");
    private final Integer value;
    private final String title;

    @JsonCreator
    public static IntegerEnum2 fromValue(Integer value) {
        return DictEnum.valueOf(values(), value);
    }
}
