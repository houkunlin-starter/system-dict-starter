package com.houkunlin.dict.common.bean;

import com.houkunlin.dict.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DoubleEnum1 implements DictEnum<Double> {
    ITEM1(1.0D, "选项1"),
    ITEM2(2.0D, "选项2"),
    ITEM3(3.0D, "选项3");
    private final Double value;
    private final String title;
}
