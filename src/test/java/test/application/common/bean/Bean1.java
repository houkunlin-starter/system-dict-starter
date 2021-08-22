package test.application.common.bean;

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
public class Bean1 {
    @DictText("PeopleType")
    private String userType = "0";

    @DictText("PeopleType")
    private String userType1 = "1";

    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private String userType3 = "0";

    @DictText(enums = {PeopleType.class})
    private int userType31 = 0;

    @DictText(enums = {PeopleType.class})
    private Long userType32 = 0L;

    @DictText(enums = {PeopleType.class})
    private String userType4 = "1";

    @DictText("accident-type")
    private String accidentType = "中风";

    @DictText("accident-type")
    private String accidentType1 = "0";

    @DictText
    private String accidentType2 = "0";

    @DictText(nullable = DictText.Type.YES)
    private String accidentType21 = "0";

    @DictText(nullable = DictText.Type.NO)
    private String accidentType22 = "0";

    private PeopleType peopleType = PeopleType.ADMIN;
}
