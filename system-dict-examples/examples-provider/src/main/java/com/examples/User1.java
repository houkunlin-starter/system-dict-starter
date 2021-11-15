package com.examples;

import com.houkunlin.system.dict.starter.json.DictText;
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
