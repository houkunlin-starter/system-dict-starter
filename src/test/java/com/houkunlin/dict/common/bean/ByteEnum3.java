package com.houkunlin.dict.common.bean;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@DictConverter(onlyDictValue = true)
@Getter
@AllArgsConstructor
public enum ByteEnum3 implements DictEnum<Byte> {
    ITEM1((byte) 1, "选项1"),
    ITEM2((byte) 2, "选项2"),
    ITEM3((byte) 3, "选项3");
    private final Byte value;
    private final String title;
}
