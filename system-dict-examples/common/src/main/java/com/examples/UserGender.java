package com.examples;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.system.dict.starter.DictEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户性别
 *
 * @author HouKunLin
 */
@Getter
@AllArgsConstructor
public enum UserGender implements DictEnum<Integer> {
    /** 男 */
    MALE(0, "男"),
    /** 女 */
    FEMALE(1, "女"),
    ;
    private final Integer value;
    private final String title;

    @JsonCreator
    public static UserGender create(final Integer value) {
        return DictEnum.valueOf(values(), value);
    }
}
