package test.application.common.valid;

import com.houkunlin.system.dict.starter.json.DictText;
import com.houkunlin.system.dict.starter.json.DictValid;
import lombok.Data;
import test.application.common.bean.PeopleType;

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
