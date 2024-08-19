package test.converter;

import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.common.bean.PeopleType;
import org.springframework.core.convert.converter.Converter;

public class PeopleType1Converter implements Converter<String, PeopleType> {

    @Override
    public PeopleType convert(String text) {
        try {
            return PeopleType.valueOf(text);
        } catch (Exception var3) {
            return DictEnum.valueOf(PeopleType.values(), Integer.valueOf(text));
        }
    }

}
