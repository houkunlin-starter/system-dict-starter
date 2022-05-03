package test.converter;

import com.houkunlin.system.dict.starter.DictEnum;
import org.springframework.core.convert.converter.Converter;
import test.application.common.bean.Switch2;

public class Switch2Converter implements Converter<String, Switch2> {
    @Override
    public test.application.common.bean.Switch2 convert(String text) {
        return (Switch2) DictEnum.valueOf(test.application.common.bean.Switch2.values(), text);
    }
}
