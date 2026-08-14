package test.converter;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.common.bean.Switch;
import org.springframework.core.convert.converter.Converter;

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
