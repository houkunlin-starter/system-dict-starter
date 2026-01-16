package com.houkunlin.dict.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictConverter;
import com.houkunlin.dict.annotation.DictType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HouKunLin
 */
@DictConverter
@DictType(value = "PeopleType", comment = "用户类型")
@Getter
@AllArgsConstructor
public enum PeopleType implements DictEnum<Integer> {
    /**
     * 系统管理员
     */
    ADMIN(0, "系统管理"),
    /**
     * 普通用户
     */
    USER(1, "普通用户"),
    /**
     * 其他用户
     */
    OTHER(2, "其他用户"),
    ;
    // @JsonValue
    private final Integer value;
    private final String title;

    /**
     * Jackson 枚举处理，把枚举值转换成枚举对象
     *
     * @param code 代码
     * @return 枚举对象
     */
    @JsonCreator
    public static PeopleType getItem(Integer code) {
        return DictEnum.valueOf(values(), code);
    }
}
