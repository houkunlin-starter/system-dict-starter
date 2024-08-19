package test.converter;

import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.common.bean.PeopleType2;
import org.springframework.core.convert.converter.Converter;

public class PeopleType2Converter implements Converter<String, PeopleType2> {
    @Override
    public PeopleType2 convert(String text) {
        return (PeopleType2) DictEnum.valueOf(PeopleType2.values(), Integer.valueOf(text));
    }
}
