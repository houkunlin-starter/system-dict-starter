package com.houkunlin.system.dict.starter.common.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.json.DictConverter;
import com.houkunlin.system.dict.starter.json.DictType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author HouKunLin
 */
@DictType
@DictConverter
@Getter
@AllArgsConstructor
public enum ACLStatusEnum implements DictEnum<Integer> {
    NONE(1, "不可读写", false, false),
    READ(2, "可读", true, false),
    WRITE(3, "可写", true, true),
    ;
    private final Integer value;
    private final String title;
    private final boolean read;
    private final boolean write;

    /**
     * Jackson 枚举处理，把枚举值转换成枚举对象
     *
     * @param code 代码
     * @return 枚举对象
     */
    @JsonCreator
    public static ACLStatusEnum getItem(Integer code) {
        return DictEnum.valueOf(values(), code);
    }


    @Override
    public Map<String, Object> getData() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("read", read);
        map.put("write", write);
        // return Map.of("read", read, "write", write);
        return map;
    }
}
