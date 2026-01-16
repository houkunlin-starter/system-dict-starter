package com.houkunlin.dict.common.bean;

import com.houkunlin.dict.common.provider.UserDictProvider;
import com.houkunlin.dict.annotation.DictText;
import lombok.*;

/**
 * @author HouKunLin
 */
@Data
@Builder(builderMethodName = "builderUser1")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User1 extends User {
    @DictText("dictHobby")
    private String hobby;
    @DictText("dictNation")
    private String nation;
    @DictText(UserDictProvider.DICT_TYPE)
    private String createdBy;
}
