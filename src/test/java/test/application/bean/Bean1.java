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
    private String userType;

    @DicText("PeopleType")
    private String userType1;

    @DicText(value = "PeopleType", enums = {PeopleType.class})
    private String userType3;

    @DicText(enums = {PeopleType.class})
    private String userType4;

    @DicText("accident-type")
    private String accidentType;

    @DicText("accident-type")
    private String accidentType1;

    @DicText
    private String accidentType2;
    private PeopleType peopleType;
}
