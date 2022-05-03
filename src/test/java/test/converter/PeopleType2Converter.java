package test.converter;

import com.houkunlin.system.dict.starter.DictEnum;
import org.springframework.core.convert.converter.Converter;
import test.application.common.bean.PeopleType2;

public class PeopleType2Converter implements Converter<String, PeopleType2> {
    @Override
    public test.application.common.bean.PeopleType2 convert(String text) {
        return (PeopleType2) DictEnum.valueOf(test.application.common.bean.PeopleType2.values(), Integer.valueOf(text));
    }
}
