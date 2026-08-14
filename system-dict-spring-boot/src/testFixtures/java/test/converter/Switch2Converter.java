package test.converter;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.common.bean.Switch2;
import org.springframework.core.convert.converter.Converter;

public class Switch2Converter implements Converter<String, Switch2> {
    @Override
    public Switch2 convert(String text) {
        return (Switch2) DictEnum.valueOf(Switch2.values(), text);
    }
}
