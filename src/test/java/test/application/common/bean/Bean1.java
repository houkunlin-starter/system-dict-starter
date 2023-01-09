package test.application.common.bean;

import com.houkunlin.system.dict.starter.json.Array;
import com.houkunlin.system.dict.starter.json.DictBoolType;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author HouKunLin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bean1 {
    @DictText(value = "PeopleType", array = @Array)
    private String userType = "0,1";

    @DictText("PeopleType")
    private String userType1 = "1";

    @DictText(value = "PeopleType", enums = {PeopleType.class}, array = @Array)
    private String userType3 = "0,1,3,0,0,2";

    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private List<String> userType301 = Arrays.asList("0", "1", "3", "0", "0", "2");

    @DictText(value = "PeopleType", enums = {PeopleType.class}, array = @Array)
    private List<String> userType302 = Arrays.asList("0", "1", "3", "0", "0", "2");

    @DictText(value = "PeopleType", enums = {PeopleType.class}, array = @Array(toText = false))
    private List<String> userType303 = Arrays.asList("0", "1", "3", "0", "0", "2");

    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private String[] userType304 = new String[]{"0", "1", "3", "0", "0", "2"};

    @DictText(value = "PeopleType", enums = {PeopleType.class}, array = @Array)
    private String[] userType305 = new String[]{"0", "1", "3", "0", "0", "2"};

    @DictText(value = "PeopleType", enums = {PeopleType.class}, array = @Array(toText = false))
    private String[] userType306 = new String[]{"0", "1", "3", "0", "0", "2"};

    @DictText(enums = {PeopleType.class}, array = @Array)
    private int userType31 = 11;

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

    @DictText(nullable = DictBoolType.YES, array = @Array)
    private String accidentType21 = "0";

    @DictText(nullable = DictBoolType.NO, array = @Array(split = "|", joinSeparator = ","))
    private String accidentType22 = "0";

    private PeopleType peopleType = PeopleType.ADMIN;
}
