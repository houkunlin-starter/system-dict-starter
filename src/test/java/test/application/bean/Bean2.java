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
public class Bean2 {
    private PeopleType peopleType;
    @DicText(fieldName = "sdff")
    private PeopleType peopleType1;
}
