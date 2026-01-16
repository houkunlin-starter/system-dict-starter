package com.houkunlin.dict.common.valid;

import com.houkunlin.dict.common.bean.PeopleType;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictValid;
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
