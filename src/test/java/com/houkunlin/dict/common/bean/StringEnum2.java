package com.houkunlin.dict.common.bean;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@DictConverter
@Getter
@AllArgsConstructor
public enum StringEnum2 implements DictEnum<String> {
    ITEM1("1", "选项1"),
    ITEM2("2", "选项2"),
    ITEM3("3", "选项3");
    private final String value;
    private final String title;
}
