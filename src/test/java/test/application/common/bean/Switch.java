package test.application.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.json.DictConverter;
import com.houkunlin.system.dict.starter.json.DictType;
import com.houkunlin.system.dict.starter.json.DictTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HouKunLin
 */
@DictTypes({@DictType(comment = "开关"),
    @DictType(value = "OPEN", comment = "开关状态")})
@DictConverter
@Getter
@AllArgsConstructor
public enum Switch implements DictEnum<String> {
    /**
     * 系统管理员
     */
    ON("on", "开"),
    /**
     * 普通用户
     */
    OFF("off", "关"),
    ;
    // @JsonValue
    private final String value;
    private final String title;

    /**
     * Jackson 枚举处理，把枚举值转换成枚举对象
     *
     * @param code 代码
     * @return 枚举对象
     */
    @JsonCreator
    public static Switch getItem(String code) {
        return DictEnum.valueOf(values(), code);
    }
}
