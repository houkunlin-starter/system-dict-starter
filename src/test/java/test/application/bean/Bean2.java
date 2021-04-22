package test.application.bean;

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
    @DicText(fieldName = "sdff")
    private PeopleType peopleType1 = PeopleType.ADMIN;
}
