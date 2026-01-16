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
@DictType(comment = "是否")
@DictType(value = "OPEN", comment = "开关状态")
@DictConverter
@Getter
@AllArgsConstructor
public enum Whether implements DictEnum<Boolean> {
    /**
     * 系统管理员
     */
    YES(true, "是"),
    /**
     * 普通用户
     */
    NO(false, "否"),
    ;
    // @JsonValue
    private final Boolean value;
    private final String title;

    /**
     * Jackson 枚举处理，把枚举值转换成枚举对象
     *
     * @param code 代码
     * @return 枚举对象
     */
    @JsonCreator
    public static Whether getItem(Boolean code) {
        return DictEnum.valueOf(values(), code);
    }
}
