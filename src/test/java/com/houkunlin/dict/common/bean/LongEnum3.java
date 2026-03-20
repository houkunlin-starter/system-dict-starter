package com.houkunlin.dict.common.bean;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@DictConverter(onlyDictValue = true)
@Getter
@AllArgsConstructor
public enum LongEnum3 implements DictEnum<Long> {
    ITEM1(1L, "选项1"),
    ITEM2(2L, "选项2"),
    ITEM3(3L, "选项3");
    private final Long value;
    private final String title;
}
