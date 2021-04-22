package test.application.bean;

import com.system.dic.starter.json.DicText;
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
    @DicText("PeopleType")
    private String userType = "0";

    @DicText("PeopleType")
    private String userType1 = "1";

    @DicText(value = "PeopleType", enums = {PeopleType.class})
    private String userType3 = "0";

    @DicText(enums = {PeopleType.class})
    private int userType31 = 0;

    @DicText(enums = {PeopleType.class})
    private Long userType32 = 0L;

    @DicText(enums = {PeopleType.class})
    private String userType4 = "1";

    @DicText("accident-type")
    private String accidentType = "中风";

    @DicText("accident-type")
    private String accidentType1 = "0";

    @DicText
    private String accidentType2 = "0";
    private PeopleType peopleType = PeopleType.ADMIN;
}
