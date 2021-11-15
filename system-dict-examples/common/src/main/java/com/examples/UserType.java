package com.examples;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.system.dict.starter.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum UserType implements DictEnum<Integer> {
    /** 系统管理员 */
    ADMIN(0, "系统管理员"),
    /** 系统管理员 */
    USER(1, "普通用户"),
    ;
    private final Integer value;
    private final String title;

    @JsonCreator
    public static UserType create(final Integer value) {
        return DictEnum.valueOf(values(), value);
    }
}
