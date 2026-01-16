package com.houkunlin.system.dict.starter.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.annotation.DictConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HouKunLin
 */
@DictConverter
@Getter
@AllArgsConstructor
public enum SiteType implements DictEnum<Integer> {
    /**
     * 系统管理员
     */
    SITE1(1, "网站1"),
    /**
     * 普通用户
     */
    SITE2(2, "网站2"),
    /**
     * 其他用户
     */
    SITE3(3, "网站3"),
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
    public static SiteType getItem(Integer code) {
        return DictEnum.valueOf(values(), code);
    }
}
