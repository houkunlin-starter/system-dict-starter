package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@DictConverter(onlyDictValue = true)
@Getter
@AllArgsConstructor
public enum DoubleEnum3 implements DictEnum<Double> {
    ITEM1(1.0D, "选项1"),
    ITEM2(2.0D, "选项2"),
    ITEM3(3.0D, "选项3");
    private final Double value;
    private final String title;

    @JsonCreator
    public static DoubleEnum3 fromValue(Double value) {
        return DictEnum.valueOf(values(), value);
    }
}
