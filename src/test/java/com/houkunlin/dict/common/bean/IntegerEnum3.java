package com.houkunlin.dict.common.bean;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@DictConverter(onlyDictValue = true)
@Getter
@AllArgsConstructor
public enum IntegerEnum3 implements DictEnum<Integer> {
    ITEM1(1, "选项1"),
    ITEM2(2, "选项2"),
    ITEM3(3, "选项3");
    private final Integer value;
    private final String title;
}
