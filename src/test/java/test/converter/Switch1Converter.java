package test.converter;

import com.houkunlin.system.dict.starter.DictEnum;
import org.springframework.core.convert.converter.Converter;
import test.application.common.bean.Switch;

public class Switch1Converter implements Converter<String, Switch> {

    @Override
    public Switch convert(String text) {
        try {
            return Switch.valueOf(text);
        } catch (Exception var3) {
            return DictEnum.valueOf(Switch.values(), text);
        }
    }

}
