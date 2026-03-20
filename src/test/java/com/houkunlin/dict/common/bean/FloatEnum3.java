package com.houkunlin.dict.common.bean;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@DictConverter(onlyDictValue = true)
@Getter
@AllArgsConstructor
public enum FloatEnum3 implements DictEnum<Float> {
    ITEM1(1.0F, "选项1"),
    ITEM2(2.0F, "选项2"),
    ITEM3(3.0F, "选项3");
    private final Float value;
    private final String title;
}
