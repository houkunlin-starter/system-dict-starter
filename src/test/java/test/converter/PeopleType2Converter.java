package test.converter;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.common.bean.PeopleType2;
import org.springframework.core.convert.converter.Converter;

public class PeopleType2Converter implements Converter<String, PeopleType2> {
    @Override
    public PeopleType2 convert(String text) {
        return (PeopleType2) DictEnum.valueOf(PeopleType2.values(), Integer.valueOf(text));
    }
}
