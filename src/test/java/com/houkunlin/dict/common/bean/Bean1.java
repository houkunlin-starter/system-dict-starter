package com.houkunlin.dict.common.bean;

import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.enums.DictBoolType;
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
    @DictArray
    @DictText(value = "PeopleType")
    private String userType = "0,1";

    @DictText("PeopleType")
    private String userType1 = "1";

    @DictArray
    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private String userType3 = "0,1,3,0,0,2";

    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private List<String> userType301 = Arrays.asList("0", "1", "3", "0", "0", "2");

    @DictArray
    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private List<String> userType302 = Arrays.asList("0", "1", "3", "0", "0", "2");

    @DictArray(toText = false)
    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private List<String> userType303 = Arrays.asList("0", "1", "3", "0", "0", "2");

    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private String[] userType304 = new String[]{"0", "1", "3", "0", "0", "2"};

    @DictArray
    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private String[] userType305 = new String[]{"0", "1", "3", "0", "0", "2"};

    @DictArray(toText = false)
    @DictText(value = "PeopleType", enums = {PeopleType.class})
    private String[] userType306 = new String[]{"0", "1", "3", "0", "0", "2"};

    @DictArray
    @DictText(enums = {PeopleType.class})
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

    @DictArray
    @DictText(nullable = DictBoolType.YES)
    private String accidentType21 = "0";

    @DictArray(split = "|", joinSeparator = ",")
    @DictText(nullable = DictBoolType.NO)
    private String accidentType22 = "0";

    private PeopleType peopleType = PeopleType.ADMIN;
}
