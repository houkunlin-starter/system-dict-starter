package com.houkunlin.system.dict.starter.common.valid;

import com.houkunlin.system.dict.starter.common.bean.PeopleType;
import com.houkunlin.system.dict.starter.annotation.DictText;
import com.houkunlin.system.dict.starter.annotation.DictValid;
import lombok.Data;

/**
 * @author HouKunLin
 */
@Data
public class DictValidBean {
    @DictValid("PeopleType")
    @DictText(enums = {PeopleType.class})
    private String userType1;
    @DictValid(value = "PeopleType", message = "用户类型参数错误")
    @DictText(enums = {PeopleType.class})
    private String userType2;
}
