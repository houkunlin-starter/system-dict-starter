package test.application.common.valid;

import com.houkunlin.system.dic.starter.json.DicText;
import com.houkunlin.system.dic.starter.json.DicValid;
import lombok.Data;
import test.application.common.bean.PeopleType;

/**
 * @author HouKunLin
 */
@Data
public class DicValidBean {
    @DicValid("PeopleType")
    @DicText(enums = {PeopleType.class})
    private String userType1;
    @DicValid(value = "PeopleType", message = "用户类型参数错误")
    @DicText(enums = {PeopleType.class})
    private String userType2;
}
