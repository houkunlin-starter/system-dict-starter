package com.houkunlin.system.dict.starter.common.bean;

import com.houkunlin.system.dict.starter.json.DictBoolType;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HouKunLin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bean2 {
    private PeopleType peopleType = PeopleType.ADMIN;
    @DictText
    private PeopleType peopleType0 = PeopleType.ADMIN;
    @DictText(fieldName = "sdff")
    private PeopleType peopleType1 = PeopleType.ADMIN;
    @DictText(enums = {PeopleType.class})
    private PeopleType peopleType2 = PeopleType.ADMIN;

    @DictText(enums = {PeopleType.class}, mapValue = DictBoolType.YES)
    private PeopleType peopleType3 = PeopleType.ADMIN;

    @DictText(fieldName = "peopleType4-DIC", enums = {PeopleType.class}, mapValue = DictBoolType.YES)
    private PeopleType peopleType4 = PeopleType.ADMIN;
}
