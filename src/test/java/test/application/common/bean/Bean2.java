package test.application.common.bean;

import com.houkunlin.system.dic.starter.json.DicText;
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
    @DicText
    private PeopleType peopleType0 = PeopleType.ADMIN;
    @DicText(fieldName = "sdff")
    private PeopleType peopleType1 = PeopleType.ADMIN;
    @DicText(enums = {PeopleType.class})
    private PeopleType peopleType2 = PeopleType.ADMIN;

    @DicText(enums = {PeopleType.class}, mapValue = DicText.Type.YES)
    private PeopleType peopleType3 = PeopleType.ADMIN;

    @DicText(fieldName = "peopleType4-DIC", enums = {PeopleType.class}, mapValue = DicText.Type.YES)
    private PeopleType peopleType4 = PeopleType.ADMIN;
}
