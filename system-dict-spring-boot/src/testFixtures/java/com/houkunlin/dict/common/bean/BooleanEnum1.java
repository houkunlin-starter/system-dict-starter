package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BooleanEnum1 implements DictEnum<Boolean> {
    ITEM1(true, "选项1"),
    ITEM2(false, "选项2");
    private final Boolean value;
    private final String title;

    @JsonCreator
    public static BooleanEnum1 fromValue(Boolean value) {
        return DictEnum.valueOf(values(), value);
    }
}
